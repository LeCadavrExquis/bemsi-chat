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
    var sessionId: dynamic
}

class App : RComponent<RProps, AppState>() {
    @Suppress("unused")
    val styles = require("@chatscope/chat-ui-kit-styles/dist/default/styles.min.css")

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
                    state.stomp.send("/app/chat", js{"{}"},JSON.stringify(message))
                    setState {
                        messages = messages + message
                    }
                }
            }
        }
    }

    override fun AppState.init() {
        user = User("username", "Alicja", listOf(
            "Paweł",
            "Michał",
            "Marysia",
            "Filip"
        ))
        messages = emptyList()
        friend = "mID" to "Michał"
        var socket = js("new SockJS(\"http://localhost:8080/ws\")")
        console.log(socket)
        val tmpstomp = js("Stomp").over(socket)
        console.log(tmpstomp)
        tmpstomp.connect(js("{}"), this@App::connectionSuccess, {_-> println("connection failed")})
        stomp = tmpstomp
    }

    fun connectionSuccess() {
//        var tmpurl = state.stomp.ws._transport.url.toString().split("/").takeLast(2)
//
//        console.log("Your current session is: /${tmpurl[0]}/");
//        setState{
//            sessionId = "/${tmpurl[0]}"
//        }
        println("connected !")
        state.stomp.subscribe("/user/${state.user.id}/queue/messages", this@App::onMessageReceived)
    }

    fun onMessageReceived(payload: dynamic) {
        console.log("message received !")
        console.log(payload)
    }
}

fun RBuilder.app(handler: RProps.() -> Unit) : ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}

