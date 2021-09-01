package pl.tfij.image.pandemonium.gui.imageselection

import com.google.inject.Inject
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.CheckBox
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import pl.tfij.image.pandemonium.core.isJpg
import pl.tfij.image.pandemonium.gui.ImageDetailsPanel
import pl.tfij.image.pandemonium.gui.Message
import pl.tfij.image.pandemonium.gui.StatusBar
import java.io.File
import java.nio.file.Files
import java.util.concurrent.ExecutorService

open class GenericImageSelectionPanel(
    executorService: ExecutorService,
    private val onImageSelected: (File) -> Unit
) : HBox() {
    private val initDirLocation = File(System.getProperty("user.home"))

    private val scanRecursivelyCheckBox = CheckBox("Scan recursively (deep = $RECURSION_SCAN_DEEP, files limit = $MAX_NUMBER_OF_FILES_SCANED_ON_RECURSION_SCAN)")

    private val directoryTreeView = DirectoryTreeView(initDirLocation)
        .apply {
            selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                onDirectoryChanged(newValue?.value, scanRecursivelyCheckBox.isSelected)
            }
        }

    private fun onDirectoryChanged(selectedDir: File?, scanRecursively: Boolean) {
        Platform.runLater {
            val allFiles = if (scanRecursively && selectedDir != null) {
                subDirsRecurs(selectedDir, RECURSION_SCAN_DEEP)
                    .take(MAX_NUMBER_OF_FILES_SCANED_ON_RECURSION_SCAN)
            } else {
                selectedDir?.listFiles()?.asSequence() ?: emptySequence<File>()
            }
            val files = allFiles
                .filter { Files.isReadable(it.toPath()) }
                .filter { it.isJpg() }
                .sortedBy { it.name.lowercase() }
                .toList()
            imageListView.items = FXCollections.observableArrayList(files)
        }
    }

    private fun subDirsRecurs(dir: File, deep: Int): Sequence<File> {
        return if (dir.isDirectory && deep > 0) {
            dir.listFiles()?.asSequence()
                ?.filter { Files.isReadable(it.toPath()) }
                ?.flatMap { subDirsRecurs(it, deep - 1) }
                ?: emptySequence()
        } else {
            sequenceOf(dir)
        }
    }

    private val imageListView = ImageListView(100.0, executorService)
        .apply {
            selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                if (newValue != null) {
                    onImageSelected(newValue)
                }
            }
        }

    init {
        children.add(
            BorderPane()
                .also { it.center = directoryTreeView }
                .also {
                    it.bottom = scanRecursivelyCheckBox.apply {
                        selectedProperty().addListener { _, _, newValue ->
                            onDirectoryChanged(directoryTreeView.selectionModel.selectedItem?.value, newValue)
                        }
                    }
                }
        )
        children.add(imageListView)
    }

    companion object {
        private val RECURSION_SCAN_DEEP = 5 // TODO move deep of recursion to configuration
        private val MAX_NUMBER_OF_FILES_SCANED_ON_RECURSION_SCAN = 2000 // TODO move to configuration
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
    }
)
