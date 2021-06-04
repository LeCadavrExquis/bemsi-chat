import kotlinx.css.*
import model.Conversation
import model.Message
import model.User
import react.*
import styled.css
import styled.styledDiv
import styled.styledH1

class App : RComponent<RProps, AppState>() {
    @Suppress("unused")
    val styles = kotlinext.js.require("@chatscope/chat-ui-kit-styles/dist/default/styles.min.css")

    override fun AppState.init() {
        user = User("12345", "Alicja")
        conversations = listOf(
            Conversation("Alfred", "You", "hej"),
            Conversation("Anastazja", "Anastazja", "co tam?"),
            Conversation("Antoni", "Antoni", "serwus !")
        )
        messages = listOf(
            Message("hejka", "incoming", "Amelia"),
            Message("elko", "outgoing", "Amelia"),
            Message("test1", "incoming", "Amelia"),
            Message("test2", "incoming", "Amelia"),
            Message("test3", "incoming", "Amelia"),
            Message("oki", "outgoing", "Amelia"),
            Message("doki", "outgoing", "Amelia")
        )
    }

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
            friendsList {  }
            chat {
                friendName = "Amelia"
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
}

external interface AppState : RState {
    var user: User
    var conversations: List<Conversation>
    var messages: List<Message>
}

fun RBuilder.app(handler: RProps.() -> Unit) : ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}

