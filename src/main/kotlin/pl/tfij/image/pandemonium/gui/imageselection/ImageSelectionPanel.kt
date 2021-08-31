package pl.tfij.image.pandemonium.gui.imageselection

import com.google.inject.Inject
import javafx.collections.FXCollections
import javafx.scene.layout.HBox
import pl.tfij.image.pandemonium.core.isJpg
import pl.tfij.image.pandemonium.gui.ImageDetailsPanel
import pl.tfij.image.pandemonium.gui.Message
import pl.tfij.image.pandemonium.gui.StatusBar
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class GenericImageSelectionPanel(executorService: ExecutorService, private val onImageSelected: (File) -> Unit) : HBox() {
    private val initDirLocation = File(System.getProperty("user.home"))

    private val directoryTreeView = DirectoryTreeView(initDirLocation)
        .apply { selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            val files = newValue?.value
                ?.listFiles()
                ?.filter { it.isJpg() }
                ?.sortedBy { it.name.lowercase() }
                ?: emptyList()
            imageListView.items = FXCollections.observableArrayList(files)
        } }

    private val imageListView = ImageListView(100.0, executorService)
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

class ImageSelectionPanel @Inject constructor(
    statusBar: StatusBar,
    imageDetailsPanel: ImageDetailsPanel,
    executorService: ExecutorService
) : GenericImageSelectionPanel(
    executorService = executorService,
    onImageSelected = { file ->
        statusBar.push(Message("Loaded ${file.name}"))
        imageDetailsPanel.setFile(file)
    })
