package pl.tfij.image.pandemonium.core

import org.apache.commons.imaging.common.RationalNumber
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

fun main() {
    val jpgImages = JpgMetadataService()
    val image = jpgImages.load(File("src/main/resources/image-with-metadata.jpg"))
    jpgImages.saveAs(image.setKeywords(listOf("Niemcy", "Krajobraz", "Jesień")).setTitle("a title").setComment("lorem ipsum"), File("src/main/resources/DSC_1183-out.jpg"))
}


data class JpgMetadata(
    val file: File,
    val width: Int,
    val height: Int,
    val size: Size,
    val cameraModel: String,
    val software: String,
    val fNumber: RationalNumber?,
    val exposureTime: RationalNumber?,
    val iso: Int?,
    val dataTimeOriginal: String?,
    val flash: Int?,
    val focusLength: RationalNumber?,
    val focusLengthIn35mmFormat: Int?,
    val title: String,
    val keywords: List<String>,
    val comment: String
) {
    fun setTitle(title: String): JpgMetadata = copy(title = title)

    fun setKeywords(keywords: List<String>): JpgMetadata = copy(keywords = keywords)

    fun addKeyword(keyword: String): JpgMetadata = setKeywords(keywords.plus(keyword))

    fun setComment(comment: String): JpgMetadata = copy(comment = comment)
}

data class Size(val bytes: Long) {
    fun kb(): BigDecimal = BigDecimal.valueOf(bytes).divide(BigDecimal.valueOf(1024))
    fun kb(scale: Int): BigDecimal = kb().setScale(scale, RoundingMode.CEILING)
}
