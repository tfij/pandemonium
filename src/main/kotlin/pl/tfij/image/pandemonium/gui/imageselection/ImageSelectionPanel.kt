package pl.tfij.image.pandemonium.gui.imageselection

import com.google.inject.Inject
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.scene.control.CheckBox
import javafx.scene.control.SelectionMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import pl.tfij.image.pandemonium.core.ImageScannerBuilder
import pl.tfij.image.pandemonium.core.ImageScannerBuilder.NoRecursionSetup
import pl.tfij.image.pandemonium.core.ImageScannerBuilder.RecursionSetup
import pl.tfij.image.pandemonium.core.ImageScannerBuilder.RegularRecursionSetup
import pl.tfij.image.pandemonium.core.InitDirectoryRepository
import pl.tfij.image.pandemonium.gui.imagemetadata.ImageDetailsPanel
import java.io.File
import java.util.concurrent.ExecutorService

open class GenericImageSelectionPanel(
    executorService: ExecutorService,
    private val onImageSelected: (List<File>) -> Unit,
    initDirectoryRepository: InitDirectoryRepository,
) : HBox() {
    private val scanRecursivelyCheckBox = CheckBox("Scan recursively (deep = $RECURSION_SCAN_DEEP, files limit = $MAX_NUMBER_OF_FILES_SCANED_ON_RECURSION_SCAN)")

    private val directoryTreeView = DirectoryTreeView(File(initDirectoryRepository.getInitDirectory()))
        .apply {
            selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                onDirectoryChanged(newValue?.value, scanRecursivelyCheckBox.isSelected)
            }
        }

    private val imageListView = ImageListView(100.0, executorService)
        .apply {
            selectionModel.selectionMode = SelectionMode.MULTIPLE
            selectionModel.selectedItems.addListener(
                ListChangeListener { files -> onImageSelected(files?.list ?: emptyList()) }
            )
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

    private fun onDirectoryChanged(selectedDir: File?, scanRecursively: Boolean) {
        Platform.runLater {
            val files = ImageScannerBuilder.baseDir(selectedDir)
                .scanRecursively(recursionSetup(scanRecursively))
                .toSequence()
                .sortedBy { it.name.lowercase() }
                .toList()
            imageListView.items = FXCollections.observableArrayList(files)
        }
    }

    private fun recursionSetup(scanRecursively: Boolean): RecursionSetup {
        return if (scanRecursively) {
            RegularRecursionSetup(
                RECURSION_SCAN_DEEP,
                MAX_NUMBER_OF_FILES_SCANED_ON_RECURSION_SCAN
            )
        } else {
            NoRecursionSetup
        }
    }

    companion object {
        private const val RECURSION_SCAN_DEEP = 5 // TODO move deep of recursion to configuration
        private const val MAX_NUMBER_OF_FILES_SCANED_ON_RECURSION_SCAN = 2000 // TODO move to configuration
    }
}

class ImageSelectionPanel @Inject constructor(
    imageDetailsPanel: ImageDetailsPanel,
    executorService: ExecutorService,
    initDirectoryRepository: InitDirectoryRepository
) : GenericImageSelectionPanel(
    executorService = executorService,
    onImageSelected = { files -> imageDetailsPanel.selectedImages(files) },
    initDirectoryRepository = initDirectoryRepository
)
