import declarations.conversation
import declarations.conversationList
import kotlinx.css.*
import react.*
import react.dom.h2
import react.dom.h3
import styled.css
import styled.styledDiv

class FriendsList : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                position = Position.relative
                width = 400.px
                margin = "30px"
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
    }
}

fun RBuilder.friendsList(handler: RProps.() -> Unit): ReactElement {
    return child(FriendsList::class) {
        this.attrs(handler)
    }
}