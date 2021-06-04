import declarations.*
import kotlinx.css.*
import model.Message
import react.*
import styled.css
import styled.styledDiv
import kotlin.js.json

external interface ChatProps: RProps {
    var friendName: String
    var messages: List<Message>
    var onSend: (String) -> Unit
}

class Chat : RComponent<ChatProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                position = Position.relative
                width = 900.px
                height = 400.px
                paddingTop = 50.px
            }
            mainContainer {
                chatContainer {
                    messageList {
                        props.messages.forEach {
                            messageBubble {
                                attrs.model = json(
                                    "message" to it.message,
                                    "direction" to it.direction,
                                    "sender" to it.sender,
                                    "position" to "single"
                                )
                            }
                        }
                    }
                    messageInput {
                        attrs.placeholder = "type message"
                        attrs.onSend =  { _, _, innerText, _ ->
                            props.onSend(innerText)
                        }
                    }
                }
            }
        }
    }
}

fun RBuilder.chat(handler: ChatProps.() -> Unit): ReactElement {
    return child(Chat::class) {
        this.attrs(handler)
    }
}
