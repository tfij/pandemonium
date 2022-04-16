package pl.tfij.image.pandemonium.gui.menu

import com.google.inject.Inject
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import pl.tfij.image.pandemonium.core.InitDirectoryRepository
import java.io.File

class StartingDirectoryConfigurationPanel @Inject constructor(
    private val initDirectoryRepository: InitDirectoryRepository
) : VBox() {
    private var dir = SimpleStringProperty("")

    init {
        val directoryChooser = DirectoryChooser()
        directoryChooser.initialDirectory = File(System.getProperty("user.home"))

        children.add(Label("Directory"))
        val dirText = TextField().also { field -> field.isDisable = true }

        dir.addListener { _, _, newValue -> dirText.text = newValue }

        val dirSelectionButton = Button(null, ImageView(Image("icons/folder16.png")))
            .also { button ->
                button.setOnAction {
                    val stage = scene.window as Stage
                    val selectedDirectory = directoryChooser.showDialog(stage)
                    if (selectedDirectory != null) {
                        dir.set(selectedDirectory.absolutePath)
                    }
                }
            }

        children.add(
            HBox()
                .also { hbox ->
                    hbox.children.add(dirText)
                    hbox.children.add(dirSelectionButton)
                }
        )

        children.add(
            Button("Save")
                .also { button ->
                    button.setOnAction { initDirectoryRepository.setInitDirectory(dir.value) }
                }
        )
        children.add(Label("Changes will be applied after application restart."))

        dir.set(getCurrentStartingDir())
    }

    private fun getCurrentStartingDir(): String {
        return initDirectoryRepository.getInitDirectory()
    }
}
