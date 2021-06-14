import kotlinext.js.js
import kotlinext.js.require
import kotlinx.css.*
import model.Message
import model.User
import org.khronos.webgl.Uint8Array
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
    var symmetricKey: String
    var asymKeyPair: dynamic
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
            +"Bemsi Chat App"
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
                    openChannel = this@App::openChannel
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
                        openChannel()
                    }
                }
                chat {
                    friendName = state.friend.second
                    messages = state.messages
                    onSend = {
                        sendMessage(encrypt(it, state.symmetricKey), "CHAT")
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

    private fun onLogIn(username: String): Boolean{
        if(userList.filter { it.name == username }.isNotEmpty()) {
            val tmpUser = userList.filter { it.name == username }.take(1)[0]
            var socket = js("new SockJS(\"http://localhost:8080/ws\")")
            val tmpstomp = js("Stomp").over(socket)
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
        state.stomp.subscribe("/user/${state.user.id}/queue/messages", this@App::onMessageReceived)
        openChannel()
    }
    private fun openChannel() {
        val asymKeys = js("nacl.box").keyPair()
        val symKey = js("nacl.util.encodeBase64(nacl.randomBytes(nacl.secretbox.keyLength))")
        setState{
            symmetricKey = symKey
            asymKeyPair = asymKeys
        }
        sendMessage("publicKey", "KEY")
        println(state.symmetricKey)
        println(state.asymKeyPair)
    }
    private fun encrypt(msg: String, key: dynamic): String {
        val keyUint8Array = js("nacl.util").decodeBase64(key)
        val nonce = js("nacl.randomBytes(nacl.secretbox.nonceLength)")
        val messageUint8 = js("nacl.util.decodeUTF8(msg)")
        val box = js("nacl").secretbox(messageUint8, nonce, keyUint8Array)

        var encryptedMessage = js("new Uint8Array(nonce.length + box.length)")
        encryptedMessage.set(nonce)
        encryptedMessage.set(box, nonce.length)

        return js("nacl.util.encodeBase64(encryptedMessage)")
    }
    private fun decrypt(msg: String, key: dynamic): String {
        val keyUint8Array = js("nacl.util").decodeBase64(key)
        val messageWithNonceAsUint8Array = js("nacl.util").decodeBase64(msg);
        val nonce = js("messageWithNonceAsUint8Array.slice(0, nacl.secretbox.nonceLength)")
        val message = js("messageWithNonceAsUint8Array.slice(nacl.secretbox.nonceLength, msg.length)")

        val decrypted = js("nacl.secretbox").open(message, nonce, keyUint8Array)

        if (!decrypted) {
            println("Failed to decrypt ...")
        }

        return js("nacl.util").encodeUTF8(decrypted)
    }
    private fun onMessageReceived(payload: dynamic) {
        console.log("message received !")
        console.log(payload)
        val message: Message = JSON.parse(payload.body)
        println(message)
        if(message.status == "CHAT") {
            val decryptedMessage = decrypt(message.content, state.symmetricKey)
            println(decryptedMessage)
        }
    }
    private fun sendMessage(msg: String, status: String) {
        val message = Message(
            status = status,
            content = msg,
            senderName = state.user.name,
            senderId = state.user.id,
            recipientId = state.friend.first,
            recipientName = state.friend.second,
            chatId = "",
            id = abs(Random.nextInt()).toString())
        if(status == "CHAT"){
            setState {
                messages = messages + message
            }
        }
        state.stomp.send("/app/chat", js{"{}"},JSON.stringify(message))
    }
}

fun RBuilder.app(handler: RProps.() -> Unit) : ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}

