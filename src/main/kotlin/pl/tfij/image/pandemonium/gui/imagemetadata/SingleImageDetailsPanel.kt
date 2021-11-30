package pl.tfij.image.pandemonium.gui.imagemetadata

import com.google.inject.Inject
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import mu.KotlinLogging
import pl.tfij.image.pandemonium.core.JpgMetadataService
import java.io.File

class SingleImageDetailsPanel @Inject constructor(
    private val metadataPanel: JpgMetadataPanel,
    private val jpgMetadataService: JpgMetadataService
) : VBox() {

    private val imagePreview: ImageView = ImageView()

    init {
        children.add(imagePreview)
        children.add(metadataPanel)
    }

    fun setFile(file: File) {
        metadataPanel.setJpgMetadata(jpgMetadataService.load(file))
        imagePreview.image = Image(file.inputStream(), 300.0, 300.0, true, false)
        if (imagePreview.image.isError) {
            logger.error { "Error occur on loading ${file.absolutePath}. ${imagePreview.image.exception}" }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
