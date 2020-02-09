package pl.tfij.image.pandemonium.gui.imageselection

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.util.Callback
import java.io.File

class DirectoryTreeView(private val file: File) : TreeView<File>(
    DirectoryTreeItem(
        file
    )
) {
    init {
        cellFactory = Callback<TreeView<File>, TreeCell<File>> {
            FileNameTreeCell(
                file
            )
        }
    }
}

private class FileNameTreeCell(private val file: File) : TreeCell<File>() {
    override fun updateItem(item: File?, empty: Boolean) {
        super.updateItem(item, empty)
        text = when {
            item?.exists() == false -> {
                "${item.name} (file not exists)"
            }
            item?.absolutePath == file.absolutePath -> {
                file.absolutePath
            }
            else -> {
                item?.name
            }
        }
    }
}

private class DirectoryTreeItem(file: File) : TreeItem<File>(file) {
    private var isFirstTimeChildren = true
    private var isFirstTimeLeaf = true
    private var isLeaf = false

    override fun getChildren(): ObservableList<TreeItem<File>> {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false
            super.getChildren().setAll(buildChildren(this))
        }
        return super.getChildren()
    }

    override fun isLeaf(): Boolean {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false
            isLeaf = value?.listFiles()?.filter { it.isDirectory }?.isEmpty() ?: true
        }
        return isLeaf
    }

    private fun buildChildren(TreeItem: TreeItem<File>): ObservableList<TreeItem<File>> {
        return TreeItem.value.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name.toLowerCase() }
            ?.map { childFile ->
                DirectoryTreeItem(
                    childFile
                )
            }
            ?.let { FXCollections.observableArrayList<TreeItem<File>>().apply { addAll(it) } }
            ?: FXCollections.emptyObservableList()

    }
}