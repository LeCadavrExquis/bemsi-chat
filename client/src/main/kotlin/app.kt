import kotlinext.js.js
import kotlinext.js.require
import kotlinx.css.*
import model.Message
import model.User
import react.*
import styled.css
import styled.styledDiv
import styled.styledH1
import ui.chat
import ui.friendsList
import ui.logIn
import kotlin.math.abs
import kotlin.random.Random

external interface AppState : RState {
    var user: User
    var messages: List<Message>
    var friend: Pair<String, String>
    var stomp: dynamic
}

class App : RComponent<RProps, AppState>() {
    @Suppress("unused")
    val styles = require("@chatscope/chat-ui-kit-styles/dist/default/styles.min.css")

    private val userList = listOf(
        User(id = "id1", name = "Filip", friends = listOf(
            "id2" to "Paweł",
            "id3" to "Marysia",
            "id4" to "Michał"
        )),
        User(id = "id2", name = "Paweł", friends = listOf(
            "id1" to "Filip",
            "id3" to "Marysia",
            "id4" to "Michał"
        )),
        User(id = "id4", name = "Michał", friends = listOf(
            "id1" to "Filip"
        ))
    )

    override fun RBuilder.render() {
        styledH1 {
            +"Bemsi ui.Chat App"
            css {
                fontSize = 4.em
                fontWeight = FontWeight.bold
                textAlign = TextAlign.center
            }
        }
        if(state.user.name == "guest"){
            styledDiv {
                css {
                    height = 200.px
                    position = Position.relative
                }
                logIn {
                    onLogIn = this@App::onLogIn
                }
            }
        } else {
            styledDiv {
                css {
                    display = Display.flow
                }
                friendsList {
                    channels = state.user.getChannels()
                    setActiveFriend = { pickedFriend ->
                        setState{
                            friend = state.user.friends.filter { it.second == pickedFriend }.take(1)[0]
                        }
                    }
                }
                chat {
                    friendName = state.friend.second
                    messages = state.messages
                    onSend = this@App::sendMessage
                }
            }
        }
    }

    override fun AppState.init() {
        user = User(id = "", name = "guest", friends = emptyList())
        friend = "" to ""
        messages = emptyList()
    }

    private fun onLogIn(username: String): Boolean{
        if(userList.filter { it.name == username }.isNotEmpty()) {
            val tmpUser = userList.filter { it.name == username }.take(1)[0]
            var socket = js("new SockJS(\"http://localhost:8080/ws\")")
            console.log(socket)
            val tmpstomp = js("Stomp").over(socket)
            console.log(tmpstomp)
            tmpstomp.connect(js("{}"), this@App::connectionSuccess, {_-> println("connection failed")})
            setState {
                user = tmpUser
                friend = tmpUser.friends.take(1)[0]
                stomp = tmpstomp
            }
        } else {
            return false
        }
        return true
    }
    private fun connectionSuccess() {
        println("connected !")
        state.stomp.subscribe("/user/${state.user.id}/queue/messages", this@App::onMessageReceived)
    }
    private fun onMessageReceived(payload: dynamic) {
        console.log("message received !")
        console.log(payload)
    }
    private fun sendMessage(msg: String) {
        val message = Message(
            status = "CHAT",
            content = msg,
            senderName = state.user.name,
            senderId = state.user.id,
            recipientId = state.friend.first,
            recipientName = state.friend.second,
            chatId = "",
            id = abs(Random.nextInt()).toString())
        setState {
            messages = messages + message
        }
        state.stomp.send("/app/chat", js{"{}"},JSON.stringify(message))
    }
}

fun RBuilder.app(handler: RProps.() -> Unit) : ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}

