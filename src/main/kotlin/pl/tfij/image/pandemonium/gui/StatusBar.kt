package pl.tfij.image.pandemonium.gui

import com.google.inject.Inject
import javafx.geometry.Insets
import javafx.scene.layout.HBox
import javafx.scene.text.Font
import javafx.scene.text.Text

class StatusBar @Inject constructor() : HBox() {

    private val messageText = Text()

    init {
        messageText.apply { padding = Insets(3.0, 3.0, 3.0, 3.0) }
            .apply { font = Font.font(10.0) }
        children.add(messageText)
    }

    fun push(message: Message) {
        messageText.text = message.text
    }
}

data class Message(val text: String)
