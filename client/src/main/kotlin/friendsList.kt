import declarations.avatar
import declarations.conversation
import declarations.conversationList
import kotlinx.css.*
import model.MessagingChannel
import react.*
import react.dom.h2
import styled.css
import styled.styledDiv

external interface FriendsListProps : RProps {
    var channels: List<MessagingChannel>
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
            conversationList {
                props.channels.forEach {
                    avatar{
                        attrs.name = it.name
                        attrs.src = it.iconSrc
                    }
                    conversation {
                        attrs.name = it.name
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