package pl.tfij.image.pandemonium.gui.imageselection

import javafx.collections.FXCollections
import javafx.scene.layout.HBox
import pl.tfij.image.pandemonium.core.isJpg
import java.io.File
import java.util.concurrent.Executors

class ImageSelectionPanel(private val onImageSelected: (File) -> Unit) : HBox() {
    private val threadPool = Executors.newFixedThreadPool(10)
    private val directoryTreeView = DirectoryTreeView(
        File(System.getProperty("user.home"))
    )
        .apply { selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            val files = newValue?.value
                ?.listFiles()
                ?.filter { it.isJpg() }
                ?.sortedBy { it.name.toLowerCase() }
                ?: emptyList()
            imageListView.items = FXCollections.observableArrayList(files)
        } }
    private val imageListView = ImageListView(
        100.0,
        threadPool
    )
        .apply { selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                onImageSelected(newValue)
            }
        } }

    init {
        children.add(directoryTreeView)
        children.add(imageListView)
    }
}