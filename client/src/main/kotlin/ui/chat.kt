package ui

import declarations.*
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import model.Message
import org.w3c.dom.events.Event
import react.*
import react.dom.button
import react.dom.div
import styled.css
import styled.styledDiv
import kotlin.js.json

external interface ChatProps: RProps {
    var friendName: String
    var messages: List<Message>
    var onSend: (String) -> Unit
    var onEncrypt: (Event) -> Unit
}

class Chat : RComponent<ChatProps, RState>() {
    @Suppress("unused")
    val styles = kotlinext.js.require("@chatscope/chat-ui-kit-styles/dist/default/styles.min.css")

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
                    child(ConversationHeader::class) {
                        avatar {
                            val avatarIdx = when(props.friendName) {
                                "Paweł" -> 1
                                "Michał" -> 2
                                "Marysia" -> 3
                                "Filip" -> 4
                                else -> 1
                            }
                            attrs {
                                src = "avatars/avatar$avatarIdx.svg"
                                name = props.friendName
                            }
                        }
                        child(ConversationHeader.Content::class) {
                            attrs {
                                userName = props.friendName
                            }
                        }
                    }
                    messageList {
                        props.messages.forEach {  msg ->
                            messageBubble {
                                attrs.model = json(
                                    "message" to msg.content,
                                    "direction" to if(msg.senderName == props.friendName) "incoming" else "outgoing",
                                    "sender" to msg.senderName,
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
        div{
            button {
                +"Encrypt"
                attrs.onClickFunction = props.onEncrypt
            }
        }
    }
}

fun RBuilder.chat(handler: ChatProps.() -> Unit): ReactElement {
    return child(Chat::class) {
        this.attrs(handler)
    }
}
