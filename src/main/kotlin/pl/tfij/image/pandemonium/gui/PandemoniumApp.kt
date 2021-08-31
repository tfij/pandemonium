package pl.tfij.image.pandemonium.gui

import com.google.inject.Guice
import com.google.inject.Injector
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import pl.tfij.image.pandemonium.gui.imageselection.ImageSelectionPanel
import pl.tfij.image.pandemonium.gui.infrastructure.GuiceModule
import pl.tfij.image.pandemonium.gui.menu.AppMenuBar
import java.util.concurrent.ExecutorService

class PandemoniumApp : Application() {

    companion object {
        fun main() {
            launch(PandemoniumApp::class.java)
        }
    }

    override fun start(stage: Stage) {
        val injector: Injector = Guice.createInjector(GuiceModule())
        stage.title = "Pandemonium"
        stage.icons.add(Image("icons/camera64.png"))
        stage.scene = Scene(rootComponent(injector), 900.0, 700.0)
        stage.show()
        stage.setOnCloseRequest {
            cleanUp(injector)
        }
    }

    private fun cleanUp(injector: Injector) {
        val executorService = injector.getInstance(ExecutorService::class.java)
        executorService.shutdown()
    }

    private fun rootComponent(injector: Injector): BorderPane {
        val appMenuBar = injector.getInstance(AppMenuBar::class.java)
        val statusBar = injector.getInstance(StatusBar::class.java)
        val imageDetailsPanel = injector.getInstance(ImageDetailsPanel::class.java)
        val imageSelectionPanel = injector.getInstance(ImageSelectionPanel::class.java)

        val root = BorderPane()
        root.top = appMenuBar
        root.left = imageSelectionPanel
        root.center = ScrollPane(imageDetailsPanel)
        root.bottom = statusBar
        return root
    }

}