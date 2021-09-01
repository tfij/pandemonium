package pl.tfij.image.pandemonium.gui.menu

import com.google.inject.Inject
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.stage.Modality
import javafx.stage.Stage

class AppMenuBar @Inject constructor(private val standardKeywordsConfigurationPanel: StandardKeywordsConfigurationPanel) : MenuBar() {

    private val toolsMenu = Menu("Tools")
        .also { toolsMenu ->
            toolsMenu.items.add(standardKeywordsMenuItem())
        }

    private fun standardKeywordsMenuItem(): MenuItem {
        return MenuItem("Standard keywords configuration")
            .also { menuItem ->
                menuItem.setOnAction {
                    Stage()
                        .apply { initModality(Modality.APPLICATION_MODAL) }
                        .apply { initOwner(parent.scene.window as Stage) }
                        .apply { title = "Standard keywords configuration" }
                        .apply { scene = Scene(standardKeywordsConfigurationPanel, 440.0, 400.0) }
                        .apply { show() }
                }
            }
    }

    init {
        menus.add(toolsMenu)
    }
}
