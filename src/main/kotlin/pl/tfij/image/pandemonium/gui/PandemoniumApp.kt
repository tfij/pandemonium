package pl.tfij.image.pandemonium.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import pl.tfij.image.pandemonium.core.InMemoryKeywordRepository
import pl.tfij.image.pandemonium.core.JpgMetadataService

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
        stage.scene = Scene(rootComponent(jpgMetadataService), 900.0, 700.0)
        stage.show()
    }

    private fun rootComponent(
        jpgMetadataService: JpgMetadataService
    ): BorderPane {
        val root = BorderPane()
        val statusBar = StatusBar()
        val imageDetailsPanel = ImageDetailsPanel(jpgMetadataService, statusBar)
        root.left = ImageSelectionPanel { file ->
            statusBar.push(Message("Loaded ${file.name}"))
            imageDetailsPanel.setFile(file)
        }
        root.center = ScrollPane(imageDetailsPanel)
        root.bottom = statusBar
        return root
    }

}