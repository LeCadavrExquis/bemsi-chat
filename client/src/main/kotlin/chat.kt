import declarations.*
import kotlinx.css.*
import model.Message
import react.*
import react.dom.render
import styled.css
import styled.styledDiv
import kotlin.js.json

interface ChatProps: RProps {
    //TODO: implement
    var messages: List<Message>
}

class Chat : RComponent<RProps, RState>() {
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
                        messageBubble {
                            model = json(
                                "message" to "Elooooooooooooooooooooooooo",
                                "sentTime" to "just now",
                                "sender" to "Twoja stara"
                            )
                        }
                        messageBubble {
                            model = json(
                                "message" to "Elooooooooooooooooooooooooo",
                                "sentTime" to "just now",
                                "sender" to "Twoja stara"
                            )
                        }
                        messageBubble {
                            model = json(
                                "message" to "Elooooooooooooooooooooooooo",
                                "sentTime" to "just now",
                                "sender" to "Twoja stara"
                            )
                        }
                        messageFooter {
                            sender = "Twoja stara"
                            sentTime = "teraz i zawsze"
                        }
                    }
                    messageInput {
                        attrs.placeholder = "type msg"
                    }
                }
            }
        }
    }
}

fun RBuilder.chat(handler: RProps.() -> Unit): ReactElement {
    return child(Chat::class) {
        this.attrs(handler)
    }
}

fun RBuilder.messageBubble(handler: MessageProps.() -> Unit): ReactElement {
    return child(MessageBubble::class) {
        this.attrs(handler)
    }
}

fun RBuilder.messageFooter(handler: FooterProps.() -> Unit): ReactElement {
    return child(MessageBubble.Footer::class) {
        this.attrs(handler)
    }
}
