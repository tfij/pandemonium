package pl.tfij.image.pandemonium.gui.menu

import com.google.inject.Inject
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextInputDialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import pl.tfij.image.pandemonium.core.JpgMetadataService


class StandardKeywordsConfigurationPanel @Inject constructor(private val jpgMetadataService: JpgMetadataService) : VBox() {

    private val observableList = FXCollections.observableList(jpgMetadataService.standardKeywords().map { LabelAndButtonCell(it, handleAddKeywordAction()) })

    private val keywordInputDialog = TextInputDialog()
        .apply { title = "New standard keyword" }
        .apply { graphic = ImageView(Image("icons/plus32.png")) }
        .apply { headerText = "Enter new standard keyword" }

    private val keywordListView = ListView<LabelAndButtonCell>()
        .apply { items = observableList }

    private val addKeywordButton = Button("Add keyword", ImageView("icons/plus16.png")).apply {
        setOnAction {
            keywordInputDialog.editor.text = ""
            keywordInputDialog.showAndWait().ifPresent { keywordToAdd ->
                jpgMetadataService.addStandardKeyword(keywordToAdd)
                observableList.add(LabelAndButtonCell(keywordToAdd, handleAddKeywordAction()))
                observableList.sortBy { it.labelText.toLowerCase() }
            }
        }
    }

    init {
        children.add(addKeywordButton)
        children.add(keywordListView)
    }

    private fun handleAddKeywordAction(): (String) -> Unit = { keyword ->
        jpgMetadataService.deleteStandardKeyword(keyword)
        observableList.removeIf { it.labelText == keyword }
    }


}

private class LabelAndButtonCell(val labelText: String, onAction: (keyword: String) -> Unit) : HBox() {
    private val label = Label(labelText)
        .apply { maxWidth = Double.MAX_VALUE; }
    private val button = Button()
        .apply { graphic = ImageView(Image("icons/trash16.png")) }
        .apply { setOnAction { onAction(labelText) } }

    init {
        setHgrow(label, Priority.ALWAYS);
        children.addAll(label, button)
    }
}