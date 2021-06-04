import declarations.*
import kotlinx.css.*
import model.Message
import org.w3c.dom.NodeList
import react.*
import react.dom.render
import styled.css
import styled.styledDiv
import kotlin.js.json

external interface ChatProps: RProps {
    var friendName: String
    var messages: List<Message>
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
                                    "sentTime" to "",
                                    "direction" to it.direction,
                                    "sender" to it.sender,
                                    "position" to "single"
                                )
                            }
                        }
                    }
                    messageInput {
                        attrs.placeholder = "type message"
//                        attrs.onSend = { event: dynamic ->
//                            console.log(event)
////                            props.sendMessage(
////                                Message(event["textContent"] as String, "outgoing", "You")
////                            )
//                        }
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
