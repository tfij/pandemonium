package pl.tfij.image.pandemonium.gui.menu

import com.google.inject.Inject
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.stage.Modality
import javafx.stage.Stage

class AppMenuBar @Inject constructor(
    private val standardKeywordsConfigurationPanel: StandardKeywordsConfigurationPanel,
    private val startingDirectoryConfigurationPanel: StartingDirectoryConfigurationPanel,
) : MenuBar() {

    private val toolsMenu = Menu("Tools")
        .also { toolsMenu ->
            toolsMenu.items.add(standardKeywordsMenuItem())
            toolsMenu.items.add(startingDirectoryMenuItem())
        }

    private fun standardKeywordsMenuItem(): MenuItem {
        val scene = Scene(standardKeywordsConfigurationPanel, 440.0, 400.0)
        return MenuItem("Standard keywords configuration")
            .also { menuItem ->
                menuItem.setOnAction {
                    Stage()
                        .apply { initModality(Modality.APPLICATION_MODAL) }
                        .apply { title = "Standard keywords configuration" }
                        .apply { this.scene = scene }
                        .apply { show() }
                }
            }
    }

    private fun startingDirectoryMenuItem(): MenuItem {
        val scene = Scene(startingDirectoryConfigurationPanel, 440.0, 400.0)
        return MenuItem("Starting directory configuration")
            .also { menuItem ->
                menuItem.setOnAction {
                    Stage()
                        .apply { initModality(Modality.APPLICATION_MODAL) }
                        .apply { title = "Starting directory configuration" }
                        .apply { this.scene = scene }
                        .apply { show() }
                }
            }
    }

    init {
        menus.add(toolsMenu)
    }
}
