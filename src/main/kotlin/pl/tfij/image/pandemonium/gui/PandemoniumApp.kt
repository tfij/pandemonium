package pl.tfij.image.pandemonium.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import pl.tfij.image.pandemonium.core.InMemoryKeywordRepository
import pl.tfij.image.pandemonium.core.JpgMetadataService
import java.io.File

class PandemoniumApp : Application() {

    companion object {
        fun main() {
            launch(PandemoniumApp::class.java)
        }
    }

    override fun start(stage: Stage) {
        val jpgMetadataService = JpgMetadataService()
        val keywordRepository = InMemoryKeywordRepository() //TODO service delegate
        stage.title = "Pandemonium"
        stage.icons.add(Image("icons/camera64.png"))
        stage.scene = Scene(rootContent(jpgMetadataService, keywordRepository), 600.0, 400.0)
        stage.show()

    }

    private fun rootContent(
        jpgMetadataService: JpgMetadataService,
        keywordRepository: InMemoryKeywordRepository
    ): HBox {
        val root = HBox()
        val loadedImageGroup = HBox()
            .apply { spacing = 5.0 }
        root.children.add(loadImageButton(loadedImageGroup, jpgMetadataService, keywordRepository))
        root.children.add(loadedImageGroup)
        return root
    }

    private fun loadImageButton(
        imageDataTargetPane: Pane,
        jpgMetadataService: JpgMetadataService,
        keywordRepository: InMemoryKeywordRepository
    ): Button {
        val fileChooser = FileChooser()
        fileChooser.title = "Open Resource File"
        val extFilter = FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg", "*.jpeg", "*.JPG", "*.JPEG")
        fileChooser.extensionFilters.add(extFilter)
        val button = Button("Load image", ImageView(Image("icons/loadImage24.png")))
        button.setOnAction {
            val file = fileChooser.showOpenDialog(imageDataTargetPane.scene.window as Stage)
            file?.let {
                imageDataTargetPane.children.clear()
                imageDataTargetPane.children.add(imageThumb(file))
                imageDataTargetPane.children.add(JpgMetadataPanel(jpgMetadataService.load(file), jpgMetadataService, keywordRepository))
            }
        }
        return button
    }

    private fun imageThumb(file: File): ImageView {
        val image = Image(file.inputStream())
        val imageView = ImageView(image)
        if (image.width > image.height) {
            imageView.fitWidth = 100.0
            imageView.fitHeight = 100.0 / (image.width/image.height)
        } else {
            imageView.fitWidth = 100.0 * image.width/image.height
            imageView.fitHeight = 100.0
        }
        return imageView
    }

}