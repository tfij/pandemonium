package pl.tfij.image.pandemonium.gui.imagemetadata

import com.google.inject.Inject
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import pl.tfij.image.pandemonium.core.JpgMetadataService
import java.io.File

class SingleImageDetailsPanel @Inject constructor(
    private val metadataPanel: JpgMetadataPanel,
    private val jpgMetadataService: JpgMetadataService
) : VBox() {

    private val imagePreview: ImageView = ImageView().apply { isVisible = true }

    init {
        children.add(imagePreview)
        children.add(metadataPanel.apply { isVisible = true })
    }

    fun setFile(file: File) {
        metadataPanel.setJpgMetadata(jpgMetadataService.load(file))
        imagePreview.image = Image(file.inputStream(), 300.0, 300.0, true, false)
    }
}
