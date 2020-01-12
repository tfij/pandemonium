package pl.tfij.image.pandemonium.core

import spock.lang.Specification

class JpgMetadataSpec extends Specification {

    def "Should remove keyword"() {
        given: "image with keywords"
        JpgMetadata jpgMetadata = JpgMetadataBuilder.defaultJpgMetadata()
                .withKeywords("k1", "k2")
                .build()

        when: "I remove one keyword"
        JpgMetadata updatedImage = jpgMetadata.removeKeyword("k1")

        then: "Image contains only not removed keywords"
        updatedImage.keywords == ["k2"]
    }

    def "Should ignore remove if image does'n contains the keyword"() {
        given: "image with keywords"
        JpgMetadata jpgMetadata = JpgMetadataBuilder.defaultJpgMetadata()
                .withKeywords("k1", "k2")
                .build()

        when: "I try to remove not existing keyword"
        JpgMetadata updatedImage = jpgMetadata.removeKeyword("k3")

        then: "image keywords are not updated"
        updatedImage.keywords == ["k1", "k2"]
    }

    def "Should add keyword"() {
        given: "image with keywords"
        JpgMetadata jpgMetadata = JpgMetadataBuilder.defaultJpgMetadata()
                .withKeywords("k1", "k2")
                .build()

        when: "I add one keyword"
        JpgMetadata updatedImage = jpgMetadata.addKeyword("k3")

        then: "image contains old and new keywords"
        updatedImage.keywords == ["k1", "k2", "k3"]
    }

    def "Should ignore adding if image already contains the keyword"() {
        given: "image with keywords"
        JpgMetadata jpgMetadata = JpgMetadataBuilder.defaultJpgMetadata()
                .withKeywords("k1", "k2")
                .build()

        when: "I try to add duplicated keyword"
        JpgMetadata updatedImage = jpgMetadata.addKeyword("k1")

        then: "image contains distinct list of keywords"
        updatedImage.keywords == ["k1", "k2"]
    }

    def "Should set distinct list of keywords"() {
        given: "image with keywords"
        JpgMetadata jpgMetadata = JpgMetadataBuilder.defaultJpgMetadata().build()

        when: "I try to set keyword list with duplicates"
        JpgMetadata updatedImage = jpgMetadata.setKeywords(["n1", "n1", "n2", "n3", "n2"])

        then: "image contains distinct list of keywords"
        updatedImage.keywords == ["n1", "n2", "n3"]
    }

}
