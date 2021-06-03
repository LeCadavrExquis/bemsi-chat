import declarations.*
import kotlinx.css.*
import model.Message
import model.User
import react.*
import react.dom.div
import react.dom.h1
import react.dom.h2
import styled.css
import styled.styledDiv
import kotlin.js.json

class App : RComponent<RProps, RState>() {
    val styles = kotlinext.js.require("@chatscope/chat-ui-kit-styles/dist/default/styles.min.css")
    override fun RBuilder.render() {
        h1 { +"Bemsi App" }
        styledDiv {
            css.display = Display.flex
            styledDiv {
                css {
                    width = 500.px
                }
                h2 {+"Friends List: "}
                conversationList {
                    conversation {
                        attrs {
                            name = "Filip"
                            lastSenderName = "Ty"
                            info = "elo elo 320"
                        }
                    }
                    conversation {
                        attrs {
                            name = "Paweł"
                            lastSenderName = "Paweł"
                            info = "test 123"
                        }
                    }
                    conversation {
                        attrs {
                            name = "Twój stary"
                            lastSenderName = "Twój stary"
                            info = " pijany"
                        }
                    }
                }
            }
            styledDiv {
                css.width = 500.px
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
}
fun RBuilder.app(handler: RProps.() -> Unit) : ReactElement {
    return child(App::class) {
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
