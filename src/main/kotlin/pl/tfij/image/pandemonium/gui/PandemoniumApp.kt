package pl.tfij.image.pandemonium.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
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
        stage.scene = Scene(rootContent(jpgMetadataService), 600.0, 400.0)
        stage.show()
    }

    private fun rootContent(
        jpgMetadataService: JpgMetadataService
    ): HBox {
        val root = HBox()
        val loadedImageGroup = HBox()
            .apply { spacing = 5.0 }
        root.children.add(loadImagePanel(loadedImageGroup, jpgMetadataService))
        root.children.add(loadedImageGroup)
        return root
    }

    private fun loadImagePanel(
        loadedImageGroup: HBox,
        jpgMetadataService: JpgMetadataService
    ): LoadImagePanel {
        return LoadImagePanel(
            { loadedImageGroup.scene.window },
            { file ->
                loadedImageGroup.children.clear()
                loadedImageGroup.children.add(imageThumb(file))
                loadedImageGroup.children.add(JpgMetadataPanel(jpgMetadataService.load(file), jpgMetadataService))
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