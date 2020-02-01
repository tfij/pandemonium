package pl.tfij.image.pandemonium.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Control
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
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
        val jpgMetadataService = JpgMetadataService(InMemoryKeywordRepository())
        stage.title = "Pandemonium"
        stage.icons.add(Image("icons/camera64.png"))
        stage.scene = Scene(rootComponent(jpgMetadataService), 600.0, 400.0)
        stage.show()
    }

    private fun rootComponent(
        jpgMetadataService: JpgMetadataService
    ): BorderPane {
        val root = BorderPane()
        val statusBar = StatusBar()
        root.center = centerContent(jpgMetadataService, statusBar)
        root.bottom = statusBar
        return root
    }

    private fun centerContent(
        jpgMetadataService: JpgMetadataService,
        statusBar: StatusBar
    ): Control {
        val center = HBox()
        val loadedImageGroup = HBox()
            .apply { spacing = 5.0 }
        center.children.add(loadImagePanel(loadedImageGroup, jpgMetadataService, statusBar))
        center.children.add(loadedImageGroup)
        return ScrollPane(center)
            .apply { isFitToHeight = true }
    }

    private fun loadImagePanel(
        loadedImageGroup: HBox,
        jpgMetadataService: JpgMetadataService,
        statusBar: StatusBar
    ): LoadImagePanel {
        return LoadImagePanel(
            { loadedImageGroup.scene.window },
            { file ->
                statusBar.push(Message("Loaded ${file.name}"))
                loadedImageGroup.children.clear()
                loadedImageGroup.children.add(imageThumb(file))
                loadedImageGroup.children.add(JpgMetadataPanel(jpgMetadataService.load(file), jpgMetadataService, statusBar))
            }
        )
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