package pl.tfij.image.pandemonium.gui.imagemetadata

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage

class AddKeywordModal(
    parent: Parent,
    private val onKeywordAdd: (String) -> Unit,
    private val lastUsedKeywordProvider: () -> List<String>,
    private val standardKeywordProvider: () -> List<String>
) : Stage() {
    init {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(parent.scene.window as Stage)
        title = "Insert a keyword"
        val content = HBox()
            .apply { spacing = 5.0 }
            .apply { children.add(selectStandardKeywordPane()) }
            .apply { children.add(selectLastUseKeywordPane()) }
            .apply { children.add(customKeywordPane()) }
            .apply { padding = Insets(5.0) }
        scene = Scene(content, 300.0, 200.0)
    }

    private fun selectStandardKeywordPane(): Pane {
        val standardKeywordsChoiceBox = ChoiceBox(FXCollections.observableArrayList(standardKeywordProvider()))
        return VBox()
            .apply { spacing = 5.0 }
            .apply { children.add(Text("Standard")) }
            .apply { children.add(standardKeywordsChoiceBox) }
            .apply {
                Button("Add", ImageView(Image("icons/plus16.png")))
                    .apply { isDisable = true }
                    .apply { setOnAction { onKeywordAdd(standardKeywordsChoiceBox.value) } }
                    .also { button -> standardKeywordsChoiceBox.setOnAction { button.isDisable = standardKeywordsChoiceBox.value.isBlank() } }
                    .also { children.add(it) }
            }
    }

    private fun selectLastUseKeywordPane(): Pane {
        val lastUsedKeywordsChoiceBox = ChoiceBox(FXCollections.observableArrayList(lastUsedKeywordProvider()))
        return VBox()
            .apply { spacing = 5.0 }
            .apply { children.add(Text("Last used")) }
            .apply { children.add(lastUsedKeywordsChoiceBox) }
            .apply {
                Button("Add", ImageView(Image("icons/plus16.png")))
                    .apply { isDisable = true }
                    .apply { setOnAction { onKeywordAdd(lastUsedKeywordsChoiceBox.value) } }
                    .also { button -> lastUsedKeywordsChoiceBox.setOnAction { button.isDisable = lastUsedKeywordsChoiceBox.value.isBlank() } }
                    .also { children.add(it) }
            }
    }

    private fun customKeywordPane(): Pane {
        val customKeywordTextField = TextField()
        return VBox()
            .apply { spacing = 5.0 }
            .apply { children.add(Text("Custom keyword")) }
            .apply { children.add(customKeywordTextField) }
            .apply {
                Button("Add", ImageView(Image("icons/plus16.png")))
                    .apply { setOnAction { onKeywordAdd(customKeywordTextField.text) } }
                    .apply { isDisable = true }
                    .also { customKeywordTextField.apply { textProperty().addListener { _, _, newValue -> it.isDisable = newValue.isBlank() } } }
                    .also { children.add(it) }
            }
    }
}
