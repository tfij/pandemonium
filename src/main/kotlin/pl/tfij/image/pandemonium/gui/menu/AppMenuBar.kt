package pl.tfij.image.pandemonium.gui.menu

import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.stage.Modality
import javafx.stage.Stage
import pl.tfij.image.pandemonium.core.JpgMetadataService

class AppMenuBar(private val jpgMetadataService: JpgMetadataService) : MenuBar() {

    private val toolsMenu = Menu("Tools")
        .also { toolsMenu ->
            val standardKeywordsMenuItem = MenuItem("Standard keywords configuration")
                .also { menuItem -> menuItem.setOnAction {
                    Stage()
                        .apply { initModality(Modality.APPLICATION_MODAL) }
                        .apply { initOwner(parent.scene.window as Stage) }
                        .apply { title = "Standard keywords configuration" }
                        .apply { scene = Scene(StandardKeywordsConfigurationPanel(jpgMetadataService), 440.0, 400.0) }
                        .apply { show() }
                } }
            toolsMenu.items.add(standardKeywordsMenuItem) }

    init {
        menus.add(toolsMenu)
    }

}