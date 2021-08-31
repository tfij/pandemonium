package pl.tfij.image.pandemonium.core

import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

data class JpgMetadata(
    val file: File,
    val width: Int,
    val height: Int,
    val size: Size,
    val cameraModel: String,
    val software: String,
    val fNumber: FNumber?,
    val exposureTime: ExposureTime?,
    val iso: Int?,
    val dataTimeOriginal: String?,
    val flash: Int?,
    val focusLength: Int?,
    val focusLengthIn35mmFormat: Int?,
    val title: String,
    val keywords: List<String>,
    val comment: String
) {
    fun setTitle(title: String): JpgMetadata = copy(title = title)

    fun setKeywords(keywords: List<String>): JpgMetadata = copy(keywords = keywords.distinct())

    fun addKeyword(keyword: String): JpgMetadata = setKeywords(keywords.plus(keyword).distinct())

    fun removeKeyword(keyword: String): JpgMetadata = setKeywords(keywords.minus(keyword))

    fun setComment(comment: String): JpgMetadata = copy(comment = comment)
}

data class Size(val bytes: Long) {
    fun kb(): BigDecimal = BigDecimal.valueOf(bytes).divide(_1024)
    fun mb(): BigDecimal = BigDecimal.valueOf(bytes).divide(_1024).divide(_1024)
    fun kb(scale: Int): BigDecimal = kb().setScale(scale, RoundingMode.CEILING)
    fun mb(scale: Int): BigDecimal = mb().setScale(scale, RoundingMode.CEILING)

    companion object {
        private val _1024 = BigDecimal.valueOf(1024)
    }
}

data class ExposureTime(private val numerator: Int, private val divisor: Int) {
    fun toText(): String {
        return if (numerator.toDouble() < divisor.toDouble()) {
            if (divisor.rem(numerator) == 0) {
                "1/${divisor / numerator} s"
            } else {
                "$numerator/$divisor s"
            }
        } else {
            BigDecimal(numerator).divide(BigDecimal(divisor), 1, RoundingMode.HALF_EVEN)
                .stripTrailingZeros()
                .toPlainString()
                .let { "$it s" }
        }
    }
}

data class FNumber(private val numerator: Int, private val divisor: Int) {
    fun toText(): String {
        return BigDecimal(numerator).divide(BigDecimal(divisor), 1, RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString()
    }
}
