package pl.tfij.image.pandemonium.gui

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.concurrent.Task
import javafx.concurrent.Worker
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.util.Callback
import java.io.File
import java.util.concurrent.ExecutorService

class ImageListView(private val imageSize: Double, private val executorService: ExecutorService) : ListView<File>() {

    init {
        cellFactory = Callback<ListView<File>, ListCell<File>> {
            val imageView = ImageView()
            ImageListCell(imageView, imageSize, executorService)
                .apply { graphic = imageView }
        }
    }
}

private class ImageListCell(
    private val imageView: ImageView,
    private val imageSize: Double,
    private val executorService: ExecutorService
) : ListCell<File>() {
    val loadingTask: ObjectProperty<Task<Image>> = SimpleObjectProperty()

    override fun updateItem(item: File?, empty: Boolean) {
        super.updateItem(item, empty)
        resetTask()
        if (empty || item == null) {
            imageView.isVisible = false
            text = ""
        } else {
            imageView.isVisible = true
            text = item.name
            val task: Task<Image> = createTask(item)
            executorService.submit(task)
            loadingTask.set(task)
        }
    }

    private fun resetTask() {
        if (loadingTaskNotFinished()) {
            loadingTask.get().cancel();
        }
        loadingTask.set(null);
    }

    private fun loadingTaskNotFinished(): Boolean {
        return loadingTask.get() != null &&
                loadingTask.get().state != Worker.State.SUCCEEDED &&
                loadingTask.get().state != Worker.State.FAILED
    }

    private fun createTask(item: File): Task<Image> {
        val task: Task<Image> = object : Task<Image>() {
            @Throws(Exception::class)
            override fun call(): Image {
                return Image(item.inputStream(), imageSize, imageSize, true, false)
            }
        }
        task.setOnSucceeded {
            imageView.image = task.value
            imageView.maxWidth(imageSize)
        }
        return task
    }
}