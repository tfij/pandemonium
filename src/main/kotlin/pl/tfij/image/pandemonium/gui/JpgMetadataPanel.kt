package pl.tfij.image.pandemonium.gui

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Modality
import javafx.stage.Stage
import pl.tfij.image.pandemonium.core.JpgMetadata
import pl.tfij.image.pandemonium.core.JpgMetadataService


class JpgMetadataPanel(
    private var image: JpgMetadata,
    private val jpgMetadataService: JpgMetadataService,
    private val statusBar: StatusBar
) : GridPane() {

    val keywordsPane = VBox()

    init {
        padding = Insets(5.0)
        addRow(0, "File name", image.file.name)
        addRow(1, "Width", image.width)
        addRow(2, "Height", image.height)
        addEditableRow(3, "Title", image.title) {
                newTitle -> image = image.setTitle(newTitle)
        }
        addKeywordsRow(4, "Keywords")
        addEditableRow(5, "Comment", image.comment) {
                newComment -> image = image.setComment(newComment)
        }
        addRow(6, "Size", "${image.size.mb(2)} mb")
        addRow(7, "Camera model", image.cameraModel)
        addRow(8, "Software", image.software)
        addRow(9, "F-number", image.fNumber?.toText())
        addRow(10, "Exposure time", image.exposureTime?.toText())
        addRow(11, "ISO", image.iso)
        addRow(12, "Focus length", image.focusLength)
        addRow(13, "Focus length in 35mm", image.focusLengthIn35mmFormat)
        addRow(14, "Flash", image.flash)
        addActionRow(15)
    }

    private fun GridPane.addRow(rowIndex: Int, name: String, value: String?) {
        Text(name)
            .apply { padding = Insets(5.0, 5.0, 5.0, 0.0) }
            .also { add(it, 0, rowIndex) }
        Text(value)
            .apply { padding = Insets(1.0, 1.0, 1.0, 0.0) }
            .also { add(it, 1, rowIndex) }
    }

    private fun GridPane.addRow(rowIndex: Int, name: String, value: Int?) {
        addRow(rowIndex, name, value?.toString())
    }

    private fun GridPane.addEditableRow(rowIndex: Int, name: String, value: String?, callback: (String) -> Unit) {
        Text(name)
            .apply { padding = Insets(5.0, 5.0, 5.0, 0.0) }
            .also { add(it, 0, rowIndex) }
        TextField(value)
            .apply { padding = Insets(1.0, 0.0, 1.0, 0.0) }
            .apply { textProperty().addListener { _, _, newValue -> callback(newValue) } }
            .also { add(it, 1, rowIndex) }
    }

    private fun GridPane.addActionRow(rowIndex: Int) {
        HBox()
            .apply { spacing = 3.0 }
            .apply {
                Button("Save", ImageView(Image("icons/accept16.png")))
                    .also { button -> button.setOnAction {
                        jpgMetadataService.save(image)
                        statusBar.push(Message("Image saved"))
                    } }
                    .also { button -> children.add(button) }
            }
            .apply {
                Button("Save as", ImageView(Image("icons/plus16.png")))
                    .also { button -> button.setOnAction {
                        val fileChooser = FileChooser()
                        fileChooser.extensionFilters.add(ExtensionFilter("JPG files (*.jpg)", "*.jpg", "*.jpeg", "*.JPG", "*.JPEG"))
                        fileChooser.showSaveDialog(parent.scene.window)
                            ?.also {
                                    file -> jpgMetadataService.saveAs(image, file)
                                    statusBar.push(Message("Image saved as ${file.name}"))
                            }
                    } }
                    .also { button -> children.add(button) }
            }
            .also { hbox ->
                addRow(rowIndex, hbox)
                setColumnSpan(hbox, 2)
            }
    }

    private fun GridPane.addKeywordsRow(rowIndex: Int, name: String) {
        Text(name)
            .apply { padding = Insets(1.0, 5.0, 1.0, 0.0) }
            .also { add(it, 0, rowIndex) }
        refreshKeywordRow()
        keywordsPane.apply { padding = Insets(10.0, 0.0, 10.0, 0.0) }
            .apply { spacing = 3.0 }
            .also { add(it, 1, rowIndex) }
    }

    private fun refreshKeywordRow() {
        val keywordsPanes = image.keywords.map { keyword ->
            val keywordLabel = Label(keyword)
            val deleteButton = Button()
                .apply { graphic = ImageView(Image("icons/trash16.png")) }
                .apply { setOnAction {
                    image = image.removeKeyword(keyword)
                    statusBar.push(Message("Key word '$keyword' was removed"))
                    refreshKeywordRow()
                } }
            HBox(keywordLabel, deleteButton)
                .apply { spacing = 3.0 }
                .apply { id = "keyword-$keyword" }
                .apply { alignment = Pos.CENTER_LEFT }
        }
        keywordsPane
            .apply { children.clear() }
            .apply { children.addAll(keywordsPanes) }
            .apply {
                Button("Add", ImageView(Image("icons/plus16.png")))
                    .apply {
                        setOnAction {
                            Stage()
                                .apply { initModality(Modality.APPLICATION_MODAL) }
                                .apply { initOwner(parent.scene.window as Stage) }
                                .apply { title = "Insert a keyword" }
                                .apply {
                                    HBox()
                                        .apply { spacing = 5.0 }
                                        .apply { children.add(selectStandardKeywordPane()) }
                                        .apply { children.add(selectLastUseKeywordPane()) }
                                        .apply { children.add(customKeywordPane()) }
                                        .apply { padding = Insets(5.0) }
                                        .also { scene = Scene(it, 300.0, 200.0) }
                                }
                                .apply { show() }
                        }
                    }
                    .let { children.add(it) }
            }
    }

    private fun selectStandardKeywordPane(): Pane {
        val standardKeywordsChoiceBox = ChoiceBox(FXCollections.observableArrayList(jpgMetadataService.standardKeywords()))
        return VBox()
            .apply { spacing = 5.0 }
            .apply { children.add(Text("Standard")) }
            .apply { children.add(standardKeywordsChoiceBox) }
            .apply {
                Button("Add", ImageView(Image("icons/plus16.png")))
                    .apply { isDisable = true }
                    .apply { setOnAction { addKeyword(standardKeywordsChoiceBox.value) } }
                    .also { button -> standardKeywordsChoiceBox.setOnAction { button.isDisable = standardKeywordsChoiceBox.value.isBlank() } }
                    .also { children.add(it) }
            }
    }

    private fun selectLastUseKeywordPane(): Pane {
        val lastUsedKeywordsChoiceBox = ChoiceBox(FXCollections.observableArrayList(jpgMetadataService.lastUsedKeywords()))
        return VBox()
            .apply { spacing = 5.0 }
            .apply { children.add(Text("Last used")) }
            .apply { children.add(lastUsedKeywordsChoiceBox) }
            .apply {
                Button("Add", ImageView(Image("icons/plus16.png")))
                    .apply { isDisable = true }
                    .apply { setOnAction { addKeyword(lastUsedKeywordsChoiceBox.value) } }
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
                    .apply { setOnAction { addKeyword(customKeywordTextField.text) } }
                    .apply { isDisable = true }
                    .also { customKeywordTextField.apply { textProperty().addListener { _, _, newValue -> it.isDisable = newValue.isBlank() } } }
                    .also { children.add(it) }

            }
    }

    private fun addKeyword(keyWord: String) {
        image = image.addKeyword(keyWord)
        jpgMetadataService.addLastUsedKeyword(keyWord)
        refreshKeywordRow()
        statusBar.push(Message("Key word '$keyWord' was added"))
    }
}