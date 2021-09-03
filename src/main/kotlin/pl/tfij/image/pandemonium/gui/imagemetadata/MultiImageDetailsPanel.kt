package pl.tfij.image.pandemonium.gui.imagemetadata

import com.google.inject.Inject
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import pl.tfij.image.pandemonium.core.JpgMetadataService
import pl.tfij.image.pandemonium.gui.Message
import pl.tfij.image.pandemonium.gui.StatusBar
import java.io.File

class MultiImageDetailsPanel @Inject constructor(
    private val jpgMetadataService: JpgMetadataService,
    private val statusBar: StatusBar
) : VBox() {

    private var files: ObservableList<File> = FXCollections.observableArrayList()

    init {
        children.add(Text("Multi file changes mode").also { it.style = "-fx-font: 24 arial;" })
        val numberOfSelectedFilesTextComponent = Text("Selected images: ${files.size}")
        files.addListener(ListChangeListener { numberOfSelectedFilesTextComponent.text = "Number of selected images: ${files.size}" })
        children.add(numberOfSelectedFilesTextComponent)
        children.add(
            Button("Add keyword to all images", ImageView(Image("icons/plus16.png")))
                .apply { setOnAction { initKeywordModal() } }
        )
    }

    fun setFiles(files: List<File>) {
        this.files.setAll(files)
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
        jpgMetadataService.addLastUsedKeyword(keyWord)
        files.forEach { file ->
            jpgMetadataService.load(file)
                .let { it.addKeyword(keyWord) }
                .also { jpgMetadataService.save(it) }
        }
        statusBar.push(Message("Key word '$keyWord' was added to ${files.size} images"))
    }
}
