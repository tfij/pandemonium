package pl.tfij.image.pandemonium.gui

import com.google.inject.Inject
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import pl.tfij.image.pandemonium.core.JpgMetadataService
import java.io.File


class ImageDetailsPanel @Inject constructor(private val metadataPanel: JpgMetadataPanel, private val jpgMetadataService: JpgMetadataService) : VBox() {

    private val imagePreview: ImageView = ImageView().apply { isVisible = false }

    init {
        children.add(imagePreview)
        children.add(metadataPanel.apply { isVisible = false })
    }

    fun setFile(file: File) {
        metadataPanel.setJpgMetadata(jpgMetadataService.load(file))
        metadataPanel.isVisible = true
        imagePreview.image = Image(file.inputStream(), 300.0, 300.0, true, false)
        imagePreview.isVisible = true
    }

}