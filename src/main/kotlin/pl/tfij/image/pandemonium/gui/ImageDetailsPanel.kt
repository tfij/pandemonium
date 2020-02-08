package pl.tfij.image.pandemonium.gui

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import pl.tfij.image.pandemonium.core.JpgMetadataService
import java.io.File


class ImageDetailsPanel(private val jpgMetadataService: JpgMetadataService,
                        statusBar: StatusBar) : VBox() {

    private val imagePreview: ImageView = ImageView().apply { isVisible = false }
    private val metadataPanel: JpgMetadataPanel = JpgMetadataPanel(jpgMetadataService, statusBar).apply { isVisible = false }

    init {
        children.add(imagePreview)
        children.add(metadataPanel)
    }

    fun setFile(file: File) {
        metadataPanel.setJpgMetadata(jpgMetadataService.load(file))
        metadataPanel.isVisible = true
        imagePreview.image = Image(file.inputStream(), 300.0, 300.0, true, false)
        imagePreview.isVisible = true
    }

}