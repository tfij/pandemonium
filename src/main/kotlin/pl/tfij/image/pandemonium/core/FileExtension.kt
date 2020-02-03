package pl.tfij.image.pandemonium.core

import java.io.File

private val jpgExtensions = listOf(
    "jpg",
    "jpeg"
)

fun File.isJpg(): Boolean {
    return isFile && jpgExtensions.contains(extension.toLowerCase())
}
