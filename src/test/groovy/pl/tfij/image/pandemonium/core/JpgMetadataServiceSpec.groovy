package pl.tfij.image.pandemonium.core

import spock.lang.Specification
import spock.lang.Unroll

import java.nio.channels.FileChannel

class JpgMetadataServiceSpec extends Specification {
    public static final JpgMetadataService jpgMetadataService = new JpgMetadataService(new InMemoryKeywordRepository())

    def "Should load jpg metadata"() {
        given: "jpg image with configured metadata"
        File file = new File("src/test/resources/image-with-metadata.jpg")

        when: "I load image"
        JpgMetadata image = jpgMetadataService.load(file)

        then: "metadata is loaded"
        image.width == 600
        image.height == 400
        image.size.kb(1) == 62.5
        image.title == "a title"
        image.keywords == ["Niemcy", "Krajobraz", "Jesień"]
        image.comment == "lorem ipsum"
        image.cameraModel == "NIKON D5300"
        image.software == "AfterShot 2.4.0.119"
        image.exposureTime.toText() == "1/350 s"
        image.fNumber.toText() == "6.7"
        image.iso == 1600
        image.dataTimeOriginal == "2019:11:04 11:40:08"
        image.flash == 0
        image.focusLength == 28
        image.focusLengthIn35mmFormat == 42
    }

    def "Should load jpg file with no metadata"() {
        given: "jpg image with empty metadata set"
        File file = new File("src/test/resources/image-with-no-metadata.jpg")

        when: "I load image"
        JpgMetadata image = jpgMetadataService.load(file)

        then: "metadata is loaded"
        image.width == 600
        image.height == 400
        image.size.kb(1) == 71.7
        image.title == ""
        image.keywords == []
        image.comment == ""
        image.cameraModel == ""
        image.software == ""
        image.exposureTime == null
        image.fNumber == null
        image.iso == null
        image.dataTimeOriginal == null
        image.flash == null
        image.focusLength == null
        image.focusLengthIn35mmFormat == null
    }

    @Unroll
    def "Should throw exception loading #fileName"() {
        given: "not jpg file"
        File file = new File("src/test/resources/$fileName")

        when: "I try to load not jpg file"
        jpgMetadataService.load(file)

        then:
        thrown IllegalArgumentException

        where:
        fileName << [
            "text-file.jpg",
            "text-file.txt",
            "not-existing-file.jpg"
        ]
    }

    def "Should save metadata"() {
        given: "loaded jpg image with configured metadata"
        File file = new File("src/test/resources/image-with-metadata.jpg")
        JpgMetadata image = jpgMetadataService.load(file)

        when: "I update metadata"
        JpgMetadata newImage = image.setTitle("new title")
                .addKeyword("new keyword")
                .setComment("new comment")

        and: "save image"
        File outputFile = File.createTempFile("outputFile", ".jpg")
        jpgMetadataService.saveAs(newImage, outputFile)

        then: "metadata is updated"
        JpgMetadata updatedImage = jpgMetadataService.load(outputFile)
        updatedImage.title == "new title"
        updatedImage.keywords == ["Niemcy", "Krajobraz", "Jesień", "new keyword"]
        updatedImage.comment == "new comment"
        updatedImage.cameraModel == "NIKON D5300"
        updatedImage.software == "AfterShot 2.4.0.119"
        updatedImage.exposureTime.toText() == "1/350 s"
        updatedImage.fNumber.toText() == "6.7"
        updatedImage.iso == 1600
        updatedImage.dataTimeOriginal == "2019:11:04 11:40:08"
        updatedImage.flash == 0
        updatedImage.focusLength == 28
        updatedImage.focusLengthIn35mmFormat == 42
    }

    def "Should save metadata in jpg file without any metadata"() {
        given: "jpg image with empty metadata set"
        File file = new File("src/test/resources/image-with-no-metadata.jpg")
        JpgMetadata image = jpgMetadataService.load(file)

        when: "I update metadata"
        JpgMetadata newImage = image.setTitle("new title")
                .addKeyword("new keyword")
                .setComment("new comment")

        and: "save image"
        File outputFile = File.createTempFile("outputFile", ".jpg")
        jpgMetadataService.saveAs(newImage, outputFile)

        then: "metadata is updated"
        JpgMetadata updatedImage = jpgMetadataService.load(outputFile)
        updatedImage.title == "new title"
        updatedImage.keywords == ["new keyword"]
        updatedImage.comment == "new comment"
        image.cameraModel == ""
        image.software == ""
        image.exposureTime == null
        image.fNumber == null
        image.iso == null
        image.dataTimeOriginal == null
        image.flash == null
        image.focusLength == null
        image.focusLengthIn35mmFormat == null
    }

    def "Should override file saving metadata"() {
        given: "jpg image with empty metadata set"
        File copedFile = createTempCopy(new File("src/test/resources/image-with-metadata.jpg"))
        JpgMetadata image = jpgMetadataService.load(copedFile)

        when: "I change metadata"
        JpgMetadata newImage = image.setTitle("new title")
                .addKeyword("new keyword")
                .setComment("new comment")

        and: "save image"
        jpgMetadataService.save(newImage)

        then: "metadata is updated"
        JpgMetadata updatedImage = jpgMetadataService.load(copedFile)
        updatedImage.title == "new title"
        updatedImage.keywords == ["Niemcy", "Krajobraz", "Jesień", "new keyword"]
        updatedImage.comment == "new comment"
        updatedImage.cameraModel == "NIKON D5300"
        updatedImage.software == "AfterShot 2.4.0.119"
        updatedImage.exposureTime.toText() == "1/350 s"
        updatedImage.fNumber.toText() == "6.7"
        updatedImage.iso == 1600
        updatedImage.dataTimeOriginal == "2019:11:04 11:40:08"
        updatedImage.flash == 0
        updatedImage.focusLength == 28
        updatedImage.focusLengthIn35mmFormat == 42
    }

    private static File createTempCopy(File originalFile) {
        File copedFile = File.createTempFile("copedFile", ".jpg")
        copyFile(originalFile, copedFile)
        return copedFile
    }

    private static void copyFile(File sourceFile, File destFile)
            throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        FileChannel source = null
        FileChannel destination = null
        source = new FileInputStream(sourceFile).getChannel()
        destination = new FileOutputStream(destFile).getChannel()
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size())
        }
        if (source != null) {
            source.close()
        }
        if (destination != null) {
            destination.close()
        }

    }

}
