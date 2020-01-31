package pl.tfij.image.pandemonium.gui

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Window
import java.io.File

class LoadImagePanel(private val windowProvider: () -> Window, private val onLoadAction: (File) -> Unit) : Pane() {

    private val lastKnownDirectoryProperty: SimpleObjectProperty<File> = SimpleObjectProperty()

    init {
        children.add(loadImageButton())
    }

    private fun loadImageButton(): Button {
        val fileChooser = FileChooser()
        fileChooser.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty)
        fileChooser.title = "Open Resource File"
        val extFilter = FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg", "*.jpeg", "*.JPG", "*.JPEG")
        fileChooser.extensionFilters.add(extFilter)
        val button = Button("Load image", ImageView(Image("icons/loadImage24.png")))
        button.setOnAction {
            val file = fileChooser.showOpenDialog(windowProvider())
            file?.let {
                lastKnownDirectoryProperty.value = file.parentFile
                onLoadAction(file)
            }
        }
        return button
    }

}