package pl.tfij.image.pandemonium.gui.imagemetadata

import com.google.inject.Inject
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.FileChooser
import pl.tfij.image.pandemonium.core.JpgMetadata
import pl.tfij.image.pandemonium.core.JpgMetadataService
import pl.tfij.image.pandemonium.gui.Message
import pl.tfij.image.pandemonium.gui.StatusBar

class JpgMetadataPanel @Inject constructor(
    private val jpgMetadataService: JpgMetadataService,
    private val statusBar: StatusBar
) : GridPane() {
    private var image: JpgMetadata? = null
    private val filenameLabel = buildLabel("File name")
    private val filenameValue = buildSimpleTextValue()
    private val widthLabel = buildLabel("Width")
    private val widthValue = buildSimpleTextValue()
    private val heightLabel = buildLabel("Height")
    private val heightValue = buildSimpleTextValue()
    private val titleLabel = buildLabel("Title")
    private val titleValue = TextField()
        .apply { padding = Insets(1.0, 0.0, 1.0, 0.0) }
        .apply { textProperty().addListener { _, _, newTitle -> image = image?.setTitle(newTitle) } }
    private val keywordsLabel = buildLabel("Keywords")
    private val keywordsPane = VBox()
        .apply { padding = Insets(10.0, 0.0, 10.0, 0.0) }
        .apply { spacing = 3.0 }
    private val commentLabel = buildLabel("Comment")
    private val commentValue = TextField()
        .apply { padding = Insets(1.0, 0.0, 1.0, 0.0) }
        .apply { textProperty().addListener { _, _, newComment -> image = image?.setComment(newComment) } }
    private val sizeLabel = buildLabel("Size")
    private val sizeValue = buildSimpleTextValue()
    private val cameraModelLabel = buildLabel("Camera model")
    private val cameraModelValue = buildSimpleTextValue()
    private val softwareLabel = buildLabel("Software")
    private val softwareValue = buildSimpleTextValue()
    private val fNumberLabel = buildLabel("F-number")
    private val fNumberValue = buildSimpleTextValue()
    private val exposureLabel = buildLabel("Exposure")
    private val exposureValue = buildSimpleTextValue()
    private val isoLabel = buildLabel("ISO")
    private val isoValue = buildSimpleTextValue()
    private val focusLengthLabel = buildLabel("Focus length")
    private val focusLengthValue = buildSimpleTextValue()
    private val focusLengthIn35mmLabel = buildLabel("Focus length in 35mm")
    private val focusLengthIn35mmValue = buildSimpleTextValue()
    private val flashLabel = buildLabel("Flash")
    private val flashValue = buildSimpleTextValue()

    init {
        padding = Insets(5.0)
        insertRow(0, filenameLabel, filenameValue)
        insertRow(1, widthLabel, widthValue)
        insertRow(2, heightLabel, heightValue)
        insertRow(3, titleLabel, titleValue)
        insertRow(4, keywordsLabel, keywordsPane)
        insertRow(5, commentLabel, commentValue)
        insertRow(6, sizeLabel, sizeValue)
        insertRow(7, cameraModelLabel, cameraModelValue)
        insertRow(8, softwareLabel, softwareValue)
        insertRow(9, fNumberLabel, fNumberValue)
        insertRow(10, exposureLabel, exposureValue)
        insertRow(11, isoLabel, isoValue)
        insertRow(12, focusLengthLabel, focusLengthValue)
        insertRow(13, focusLengthIn35mmLabel, focusLengthIn35mmValue)
        insertRow(14, flashLabel, flashValue)
        addActionRow(15)
    }

    private fun buildLabel(text: String): Text {
        return Text(text).apply { padding = Insets(5.0, 5.0, 5.0, 0.0) }
    }

    private fun buildSimpleTextValue(): Text {
        return Text().apply { padding = Insets(1.0, 1.0, 1.0, 0.0) }
    }

    private fun insertRow(rowIndex: Int, leftElement: Node, rightElement: Node) {
        add(leftElement, 0, rowIndex)
        add(rightElement, 1, rowIndex)
    }

    fun setJpgMetadata(jpgMetadata: JpgMetadata) {
        image = jpgMetadata
        filenameValue.text = jpgMetadata.file.name
        widthValue.text = jpgMetadata.width.toString()
        heightValue.text = jpgMetadata.height.toString()
        titleValue.text = jpgMetadata.title
        refreshKeywordRow()
        commentValue.text = jpgMetadata.comment
        sizeValue.text = "${jpgMetadata.size.mb(2).toPlainString()}  mb"
        cameraModelValue.text = jpgMetadata.cameraModel
        softwareValue.text = jpgMetadata.software
        fNumberValue.text = jpgMetadata.fNumber?.toText()
        exposureValue.text = jpgMetadata.exposureTime?.toText()
        isoValue.text = jpgMetadata.iso?.toString()
        focusLengthValue.text = jpgMetadata.focusLength?.toString()
        focusLengthIn35mmValue.text = jpgMetadata.focusLengthIn35mmFormat?.toString()
        flashValue.text = jpgMetadata.flash?.toString()
    }

    private fun GridPane.addActionRow(rowIndex: Int) {
        HBox()
            .apply { spacing = 3.0 }
            .apply {
                Button("Save", ImageView(Image("icons/accept16.png")))
                    .also { button ->
                        button.setOnAction {
                            checkNotNull(image)
                            jpgMetadataService.save(image!!)
                            statusBar.push(Message("Image saved"))
                        }
                    }
                    .also { button -> children.add(button) }
            }
            .apply {
                Button("Save as", ImageView(Image("icons/plus16.png")))
                    .also { button ->
                        button.setOnAction {
                            val fileChooser = FileChooser()
                            fileChooser.extensionFilters.add(
                                FileChooser.ExtensionFilter(
                                    "JPG files (*.jpg)",
                                    "*.jpg", "*.jpeg", "*.JPG", "*.JPEG"
                                )
                            )
                            fileChooser.showSaveDialog(parent.scene.window)
                                ?.also { file ->
                                    checkNotNull(image)
                                    jpgMetadataService.saveAs(image!!, file)
                                    statusBar.push(Message("Image saved as ${file.name}"))
                                }
                        }
                    }
                    .also { button -> children.add(button) }
            }
            .also { hbox ->
                addRow(rowIndex, hbox)
                setColumnSpan(hbox, 2)
            }
    }

    private fun refreshKeywordRow() {
        checkNotNull(image)
        val keywordsPanes = image!!.keywords.map { keyword ->
            val keywordLabel = Label(keyword)
            val deleteButton = Button()
                .apply { graphic = ImageView(Image("icons/trash16.png")) }
                .apply {
                    setOnAction {
                        checkNotNull(image)
                        image = image!!.removeKeyword(keyword)
                        statusBar.push(Message("Key word '$keyword' was removed"))
                        refreshKeywordRow()
                    }
                }
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
                    .apply { setOnAction { initKeywordModal() } }
                    .let { children.add(it) }
            }
    }

    private fun initKeywordModal() {
        AddKeywordModal(
            parent = parent,
            onKeywordAdd = { keyword -> addKeyword(keyword) },
            lastUsedKeywordProvider = { jpgMetadataService.lastUsedKeywords() },
            standardKeywordProvider = { jpgMetadataService.standardKeywords() }
        ).apply { show() }
    }

    private fun addKeyword(keyWord: String) {
        checkNotNull(image)
        image = image!!.addKeyword(keyWord)
        jpgMetadataService.addLastUsedKeyword(keyWord)
        refreshKeywordRow()
        statusBar.push(Message("Key word '$keyWord' was added"))
    }
}
