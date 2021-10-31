package pl.tfij.image.pandemonium.core

import java.io.File
import java.nio.file.Files

class ImageScannerBuilder(
    private val baseDir: File?,
    private var recursionSetup: RecursionSetup = NoRecursionSetup
) {

    fun scanRecursively(recursionSetup: RecursionSetup): ImageScannerBuilder {
        this.recursionSetup = recursionSetup
        return this
    }

    fun toSequence(): Sequence<File> {
        return allFileSequence()
            .filter { it.isJpg() }
            .filter { isReadable(it) }
    }

    fun toList(): List<File> {
        return toSequence().toList()
    }

    private fun allFileSequence(): Sequence<File> {
        val recursionSetupVal = recursionSetup
        return when {
            baseDir == null -> {
                emptySequence()
            }
            recursionSetupVal is RegularRecursionSetup -> {
                subDirsRecurs(baseDir, recursionSetupVal.recursionDeep)
                    .take(recursionSetupVal.recursionLimit)
            }
            recursionSetupVal is NoRecursionSetup -> {
                baseDir.listFiles()?.asSequence() ?: emptySequence()
            }
            else -> throw IllegalStateException("Unexpected ImageScannerBuilder state: $baseDir, $recursionSetup.")
        }
    }

    private fun subDirsRecurs(dir: File, deep: Int): Sequence<File> {
        return if (dir.isDirectory && deep > 0 && isReadable(dir)) {
            dir.listFiles()?.asSequence()
                ?.flatMap { subDirsRecurs(it, deep - 1) }
                ?: emptySequence()
        } else {
            sequenceOf(dir)
        }
    }

    /**
     * method is slow so should be executed as last in pipeline
     */
    private fun isReadable(dir: File) = Files.isReadable(dir.toPath())

    sealed interface RecursionSetup

    object NoRecursionSetup : RecursionSetup

    data class RegularRecursionSetup(val recursionDeep: Int, val recursionLimit: Int) : RecursionSetup {
        init {
            check(recursionDeep > 0) { "`deep` argument must be a positive number." }
            check(recursionLimit > 0) { "`limit` argument must be a positive number." }
        }
    }

    companion object {
        @JvmStatic
        fun baseDir(baseDir: File?): ImageScannerBuilder {
            return ImageScannerBuilder(baseDir)
        }
    }
}

private val jpgExtensions = listOf(
    "jpg",
    "jpeg"
)

fun File.isJpg(): Boolean {
    return isFile && jpgExtensions.contains(extension.lowercase())
}
