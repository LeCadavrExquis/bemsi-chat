import declarations.ConversationHeader
import declarations.avatar
import declarations.conversation
import declarations.conversationList
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import model.MessagingChannel
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.h2
import styled.css
import styled.styledDiv

external interface FriendsListProps : RProps {
    var channels: List<MessagingChannel>
    var setActiveFriend: (String) -> Unit
}

class FriendsList : RComponent<FriendsListProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                position = Position.relative
                width = 400.px
                margin = "30px"
            }
            h2 {+"Friends List: "}
            props.channels.forEach { channel ->
                div {
                    attrs.onClickFunction = {
                        props.setActiveFriend(channel.name)
                    }
                    child(ConversationHeader::class) {
                        avatar {
                            attrs.name = channel.name
                            attrs.src = channel.iconSrc
                        }
                        child(ConversationHeader.Content::class) {
                            attrs {
                                userName = channel.name
                            }
                        }
                    }
                }
            }
        }
    }
}

fun RBuilder.friendsList(handler: FriendsListProps.() -> Unit): ReactElement {
    return child(FriendsList::class) {
        this.attrs(handler)
    }
}
