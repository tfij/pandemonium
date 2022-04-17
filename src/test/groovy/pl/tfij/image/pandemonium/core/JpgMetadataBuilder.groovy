package pl.tfij.image.pandemonium.core

import groovy.transform.CompileStatic

@CompileStatic
class JpgMetadataBuilder {

    private List<String> keywords

    static JpgMetadataBuilder defaultJpgMetadata() {
        return new JpgMetadataBuilder()
                .withKeywords("key1", "key2")
    }

    JpgMetadataBuilder withKeywords(String... keywords) {
        this.keywords = keywords.toList()
        return this
    }

    JpgMetadata build() {
        return new JpgMetadata(
                File.createTempFile("image", "jpg"),
                6000,
                4000,
                new Size(20_000),
                "Nikon D780",
                "Gimp",
                new FNumber(2, 1),
                new ExposureTime(1, 500),
                100,
                "2020-01-12 10:09:15",
                0,
                50,
                50,
                "Lorem ipsum",
                keywords,
                "dolores",
                PhotoRating._3
        )
    }

}
