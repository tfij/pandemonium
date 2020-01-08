package pl.tfij.image.pandemonium

import org.apache.commons.imaging.ImageFormats
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.common.RationalNumber
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoXpString
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet
import java.io.*

class JpgMetadataService {
    fun load(file: File): JpgMetadata {
        require(file.exists()) { "File ${file.absoluteFile} does not exists." }
        require(JPG_EXTENSION.contains(file.extension.toLowerCase())) { "jpg file required, found ${file.extension}" }
        val imageInfo = Imaging.getImageInfo(file)
        require(imageInfo.format.name == ImageFormats.JPEG.name) { "jpg format file required, found ${imageInfo.format.name}" }
        val metadata = metadata(file)
        return JpgMetadata(
            file = file,
            width = imageInfo.width,
            height = imageInfo.height,
            size = Size(file.length()),
            title = metadata.exif?.findField(EXIF_TAG_XP_TITLE)?.stringValue ?: "",
            keywords = metadata.exif?.findField(EXIF_TAG_XP_KEYWORDS)?.stringValue?.split(";") ?: emptyList(),
            comment = metadata.exif?.findField(EXIF_TAG_XP_COMMENT)?.stringValue ?: "",
            cameraModel = metadata.exif?.findField(EXIF_TAG_MODEL)?.stringValue ?: "",
            software = metadata.exif?.findField(ExifTagConstants.EXIF_TAG_SOFTWARE)?.stringValue ?: "",
            fNumber = metadata.exif?.findField(ExifTagConstants.EXIF_TAG_FNUMBER)?.value as RationalNumber?,
            exposureTime = metadata.exif?.findField(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME)?.value as RationalNumber?,
            iso = metadata.exif?.findField(ExifTagConstants.EXIF_TAG_ISO)?.intValue,
            dataTimeOriginal = metadata.exif?.findField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)?.stringValue,
            flash = metadata.exif?.findField(ExifTagConstants.EXIF_TAG_FLASH)?.intValue,
            focusLength = metadata.exif?.findField(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH)?.value as RationalNumber?,
            focusLengthIn35mmFormat = metadata.exif?.findField(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH_IN_35MM_FORMAT)?.intValue
        )
    }

    private fun metadata(file: File): JpegImageMetadata {
        return (Imaging.getMetadata(file)
            ?.let { it as JpegImageMetadata }
            ?: JpegImageMetadata(null, null))
    }

    fun saveAs(jpgMetadata: JpgMetadata, output: File) {
        FileOutputStream(output).use { fos ->
            BufferedOutputStream(fos).use { os ->
                saveMetadataInOutputStream(jpgMetadata, os)
            }
        }
    }

    fun save(jpgMetadata: JpgMetadata) {
        ByteArrayOutputStream().use { os ->
            saveMetadataInOutputStream(jpgMetadata, os)
            FileOutputStream(jpgMetadata.file).use {
                os.writeTo(it)
            }
        }
    }

    private fun saveMetadataInOutputStream(jpgMetadata: JpgMetadata, os: OutputStream) {
        val outputSet = metadata(jpgMetadata.file).exif?.outputSet ?: TiffOutputSet()
        val exifDirectory = outputSet.getOrCreateRootDirectory()
        exifDirectory.setTag(EXIF_TAG_XP_TITLE, jpgMetadata.title)
        exifDirectory.setTag(EXIF_TAG_XP_KEYWORDS, jpgMetadata.keywords.joinToString(";"))
        exifDirectory.setTag(EXIF_TAG_XP_COMMENT, jpgMetadata.comment)
        ExifRewriter().updateExifMetadataLossless(jpgMetadata.file, os, outputSet)
    }



    private fun TiffOutputDirectory.setTag(tagInfo: TagInfoXpString, value: String) {
        this.removeField(tagInfo) // remove old value
        this.add(tagInfo, value)
    }

    companion object {
        private val EXIF_TAG_MODEL = TagInfoAscii("Model", 0x0110, -1, TiffDirectoryType.TIFF_DIRECTORY_IFD0)
        private val EXIF_TAG_XP_TITLE = TagInfoXpString("XPTitle", 0x9c9b, TiffDirectoryType.TIFF_DIRECTORY_IFD0)
        private val EXIF_TAG_XP_KEYWORDS = TagInfoXpString("XPKeywords", 0x9c9e, TiffDirectoryType.TIFF_DIRECTORY_IFD0)
        private val EXIF_TAG_XP_COMMENT = TagInfoXpString("XPComment", 0x9c9c, TiffDirectoryType.TIFF_DIRECTORY_IFD0)
        private val JPG_EXTENSION = setOf("jpg", "jpeg")
    }
}