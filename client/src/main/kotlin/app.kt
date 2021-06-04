import kotlinx.css.*
import model.Message
import model.User
import react.*
import styled.css
import styled.styledDiv
import styled.styledH1

external interface AppState : RState {
    var user: User
    var messages: List<Message>
    var friendName: String
}

class App : RComponent<RProps, AppState>() {
    @Suppress("unused")
    val styles = kotlinext.js.require("@chatscope/chat-ui-kit-styles/dist/default/styles.min.css")

    override fun RBuilder.render() {
        styledH1 {
            +"Bemsi Chat App"
            css {
                fontSize = 4.em
                fontWeight = FontWeight.bold
                textAlign = TextAlign.center
            }
        }
        styledDiv {
            css {
                display = Display.flow
            }
            friendsList {
                channels = state.user.getChannels()
            }
            chat {
                friendName = state.friendName
                messages = state.messages
                onSend = { msg ->
                    //TODO: send to backend
                    setState {
                        messages = messages + Message(
                                    message = msg,
                                    direction = "outgoing",
                                    sender = user.name)
                    }
                }
            }
        }
    }

    override fun AppState.init() {
        user = User("12345", "Alicja", listOf(
            "Paweł",
            "Michał",
            "Marysia",
            "Filip"
        ))
        messages = listOf(
            Message("hejka", "incoming", "Michał"),
            Message("elko", "outgoing", "Michał"),
            Message("test1", "incoming", "Michał"),
            Message("test2", "incoming", "Michał"),
            Message("test3", "incoming", "Michał"),
            Message("oki", "outgoing", "Michał"),
            Message("doki", "outgoing", "Michał")
        )
        friendName = "Michał"
    }
}

fun RBuilder.app(handler: RProps.() -> Unit) : ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}

