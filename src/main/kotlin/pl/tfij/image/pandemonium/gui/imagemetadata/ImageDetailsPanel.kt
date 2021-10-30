package pl.tfij.image.pandemonium.gui.imagemetadata

import com.google.inject.Inject
import javafx.scene.layout.StackPane
import pl.tfij.image.pandemonium.gui.Message
import pl.tfij.image.pandemonium.gui.StatusBar
import java.io.File

class ImageDetailsPanel @Inject constructor(
    private val statusBar: StatusBar,
    private val singleImageDetailsPanel: SingleImageDetailsPanel,
    private val multiImageDetailsPanel: MultiImageDetailsPanel
) : StackPane() {

    init {
        singleImageDetailsPanel.isVisible = false
        multiImageDetailsPanel.isVisible = false
        children.addAll(
            singleImageDetailsPanel,
            multiImageDetailsPanel
        )
    }

    fun selectedImages(files: List<File>) {
        if (files.isEmpty()) {
            singleImageDetailsPanel.isVisible = false
            multiImageDetailsPanel.isVisible = false
        } else if (files.size == 1) {
            statusBar.push(Message("Loaded ${files[0].name}"))
            singleImageDetailsPanel.setFile(files[0])
            singleImageDetailsPanel.isVisible = true
            multiImageDetailsPanel.isVisible = false
        } else {
            singleImageDetailsPanel.isVisible = false
            multiImageDetailsPanel.isVisible = true
            multiImageDetailsPanel.setFiles(files)
        }
    }
}
