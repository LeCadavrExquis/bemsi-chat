import kotlinext.js.js
import kotlinext.js.require
import kotlinx.css.*
import model.Message
import model.User
import org.w3c.dom.WebSocket
import react.*
import styled.css
import styled.styledDiv
import styled.styledH1
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

    val userList = listOf<User>(
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
            +"Bemsi Chat App"
            css {
                fontSize = 4.em
                fontWeight = FontWeight.bold
                textAlign = TextAlign.center
            }
        }
        if(state.user.name == "guest"){
            logIn {
                onLogIn = { username ->
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
                        true
                    } else {
                        false
                    }
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
                        val tmpFriend = state.user.friends.filter { it.second == pickedFriend }.take(1)[0]
                        setState{
                            friend = tmpFriend
                        }
                    }
                }
                chat {
                    friendName = state.friend.second
                    messages = state.messages
                    onSend = { msg ->
                        val message = Message(
                            status = "CHAT",
                            content = msg,
                            senderName = state.user.name,
                            senderId = state.user.id,
                            recipientId = state.friend.first,
                            recipientName = state.friend.second,
                            chatId = "",
                            id = abs(Random.nextInt()).toString())
                        sendMessage(message)
                        setState {
                            messages = messages + message
                        }
                    }
                }
            }
        }
    }

    override fun AppState.init() {
        user = User(id = "", name = "guest", friends = emptyList())
        friend = "" to ""
        messages = emptyList()
    }
//
//    fun connectToWS() {
//        var socket = js("new SockJS(\"http://localhost:8080/ws\")")
//        console.log(socket)
//        val tmpstomp = js("Stomp").over(socket)
//        console.log(tmpstomp)
//        tmpstomp.connect(js("{}"), this@App::connectionSuccess, {_-> println("connection failed")})
//        setState{
//            stomp = tmpstomp
//        }
//    }

    fun connectionSuccess() {
        println("connected !")
        state.stomp.subscribe("/user/${state.user.id}/queue/messages", this@App::onMessageReceived)
    }

    fun onMessageReceived(payload: dynamic) {
        console.log("message received !")
        console.log(payload)
    }

    fun sendMessage(message: Message) {
        state.stomp.send("/app/chat", js{"{}"},JSON.stringify(message))
    }
}

fun RBuilder.app(handler: RProps.() -> Unit) : ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}

