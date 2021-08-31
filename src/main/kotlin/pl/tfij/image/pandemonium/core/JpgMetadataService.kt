package pl.tfij.image.pandemonium.core

import org.apache.commons.imaging.ImageFormats
import org.apache.commons.imaging.ImageInfo
import org.apache.commons.imaging.ImageReadException
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.common.RationalNumber
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_EXPOSURE_TIME
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_FLASH
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_FNUMBER
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_FOCAL_LENGTH
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_FOCAL_LENGTH_IN_35MM_FORMAT
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_ISO
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_SOFTWARE
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoXpString
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class JpgMetadataService(private val keywordRepository: KeywordRepository) {
    fun load(file: File): JpgMetadata {
        require(file.exists()) { "File ${file.absoluteFile} does not exists." }
        val imageInfo = getImageInfo(file)
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
            software = metadata.exif?.findField(EXIF_TAG_SOFTWARE)?.stringValue ?: "",
            fNumber = (metadata.exif?.findField(EXIF_TAG_FNUMBER)?.value as RationalNumber?)?.let { FNumber(it.numerator, it.divisor) },
            exposureTime = (metadata.exif?.findField(EXIF_TAG_EXPOSURE_TIME)?.value as RationalNumber?)?.let { ExposureTime(it.numerator, it.divisor) },
            iso = metadata.exif?.findField(EXIF_TAG_ISO)?.intValue,
            dataTimeOriginal = metadata.exif?.findField(EXIF_TAG_DATE_TIME_ORIGINAL)?.stringValue,
            flash = metadata.exif?.findField(EXIF_TAG_FLASH)?.intValue,
            focusLength = (metadata.exif?.findField(EXIF_TAG_FOCAL_LENGTH)?.value as RationalNumber?)?.toInt(),
            focusLengthIn35mmFormat = metadata.exif?.findField(EXIF_TAG_FOCAL_LENGTH_IN_35MM_FORMAT)?.intValue
        )
    }

    private fun getImageInfo(file: File): ImageInfo {
        try {
            return Imaging.getImageInfo(file)
        } catch (ex: ImageReadException) {
            throw IllegalArgumentException("File ${file.absolutePath} is invalid. ${ex.message}", ex)
        }
    }

    private fun metadata(file: File): JpegImageMetadata {
        return Imaging.getMetadata(file)
            ?.let { it as JpegImageMetadata }
            ?: JpegImageMetadata(null, null)
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

    fun standardKeywords(): List<String> {
        return keywordRepository.standardKeywords()
    }

    fun lastUsedKeywords(): List<String> {
        return keywordRepository.lastUsedKeywords()
    }

    fun addLastUsedKeyword(keyword: String) {
        keywordRepository.addLastUsedKeyword(keyword)
    }

    fun addStandardKeyword(keyword: String) {
        keywordRepository.addStandardKeyword(keyword)
    }

    fun deleteStandardKeyword(keyword: String) {
        keywordRepository.deleteStandardKeyword(keyword)
    }

    companion object {
        private val EXIF_TAG_MODEL = TagInfoAscii("Model", 0x0110, -1, TiffDirectoryType.TIFF_DIRECTORY_IFD0)
        private val EXIF_TAG_XP_TITLE = TagInfoXpString("XPTitle", 0x9c9b, TiffDirectoryType.TIFF_DIRECTORY_IFD0)
        private val EXIF_TAG_XP_KEYWORDS = TagInfoXpString("XPKeywords", 0x9c9e, TiffDirectoryType.TIFF_DIRECTORY_IFD0)
        private val EXIF_TAG_XP_COMMENT = TagInfoXpString("XPComment", 0x9c9c, TiffDirectoryType.TIFF_DIRECTORY_IFD0)
    }
}
