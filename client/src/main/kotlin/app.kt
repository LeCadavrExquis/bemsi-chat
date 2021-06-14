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

external interface AppState : RState {
    var user: User
    var messages: List<Message>
    var friend: Pair<String, String>
    var stomp: dynamic
    var symmetricKey: dynamic
    var asymKeyPair: dynamic
    var sharedKey: dynamic
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
                        sendMessage(Message(
                            senderId = state.user.id,
                            senderName = state.user.name,
                            recipientId = state.friend.first,
                            recipientName = state.friend.second,
                            status = "CHAT",
                            content = it
                        ))
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
        println("private key:")
        println(asymKeys.secretKey)
        println("public key:")
        println(asymKeys.publicKey)
        val symKey = js("nacl.util.encodeBase64(nacl.randomBytes(nacl.secretbox.keyLength))")
        println("sym key: $symKey")
        setState{
            asymKeyPair = asymKeys
            symmetricKey = symKey
        }

//
//        sendMessage(Message(
//            content = publicKey,
//            status = "PUBLICKEY",
//            senderId = state.user.id,
//            senderName = state.user.name,
//            recipientId = state.friend.first,
//            recipientName = state.friend.second)
//        )
    }

    private fun onMessageReceived(payload: dynamic) {
        val message: Message = JSON.parse(payload.body as String)
        println(message)
        when(message.status) {
            "CHAT" -> {
                setState {
                    messages = state.messages + decrypt(message, state.symmetricKey)
                }
            }
//            "PUBLICKEY" -> {
//                setState {
//                    sharedKey = message.content
//                }
//                println("public key send: ")
//                println(state.asymKeyPair.second)
//                sendMessage(Message(
//                    content = state.asymKeyPair.second,
//                    status = "PUBLICKEYRETURN",
//                    senderId = state.user.id,
//                    senderName = state.user.name,
//                    recipientId = state.friend.first,
//                    recipientName = state.friend.second)
//                )
//            }
//            "PUBLICKEYRETURN" -> {
//                println(message.content)
//                val sharedK = message.content
//                setState {
//                    sharedKey = message.content
//                }
//                val newNonce = js("nacl.randomBytes(24)")
//                val messageUint8 = js("nacl.util").decodeBase64(state.symmetricKey)
//                val encrypted = js("nacl").box(messageUint8, newNonce, js("nacl.util").decodeBase64(sharedK), js("nacl.util").decodeBase64(state.asymKeyPair.first))
//
//                var fullMessage = js("new Uint8Array(newNonce.length + encrypted.length)")
//                fullMessage.set(newNonce)
//                fullMessage.set(encrypted, newNonce.length)
//
//                val base64FullMessage = js("nacl.util").encodeBase64(fullMessage)
//                sendMessage(Message(
//                    content = base64FullMessage,
//                    status = "SECRET",
//                    nonce = js("nacl.util").encodeBase64(newNonce),
//                    senderId = state.user.id,
//                    senderName = state.user.name,
//                    recipientId = state.friend.first,
//                    recipientName = state.friend.second)
//                )
//            }
//            "SECRET" -> {
//                println("secret recived:")
//                println(message.content)
//                val messageWithNonceAsUint8Array = js("nacl.util").decodeBase64(message.content)
//                val tmpNonce = js("messageWithNonceAsUint8Array").slice(0, message.nonce.length)
//                val msg = js("messageWithNonceAsUint8Array").slice(message.nonce.length, message.content.length)
//
//                println("---------------------")
//                println(state.sharedKey)
//                println(state.asymKeyPair.second)
//                val box = js("nacl").box(msg, tmpNonce, state.sharedKey, state.asymKeyPair.second)
//                val decrypted = js("nacl.box").open(box, tmpNonce, state.sharedKey, state.asymKeyPair.second)
//
//                if (!decrypted) {
//                    println("decryption failed ...")
//                }
//
//                val base64DecryptedMessage = js("nacl.util").encodeUTF8(decrypted)
//                println(base64DecryptedMessage)
//                setState {
////                    symmetricKey = base64DecryptedMessage
//                }
//            }
        }
    }
    private fun sendMessage(msg: Message) {
        if (msg.status == "CHAT") {
            setState {
                messages = messages + msg
            }
        }

        state.stomp.send("/app/chat", js{"{}"}, JSON.stringify(encrypt(msg, state.symmetricKey)))
    }
    fun encrypt(msg: Message, key: dynamic): Message {
        println("encript method")
        println("message:  $msg")
        println(key)
        val keyUint8Array = js("nacl.util").decodeBase64(key)
        val newNonce = js("nacl.randomBytes(nacl.secretbox.nonceLength)")
        val messageUint8 = js("nacl.util").decodeUTF8(msg.content)
        val box = js("nacl").secretbox(messageUint8, newNonce, keyUint8Array)

        return Message(
            nonce = cryptoToString(newNonce)?: "crypto error",
            senderId = msg.senderId,
            recipientId = msg.recipientId,
            senderName = msg.senderName,
            recipientName = msg.recipientName,
            content = cryptoToString(box)?: "crypto error",
            status = msg.status)
    }
    private fun decrypt(msg: Message, key: dynamic): Message {
        println("message to decypher: ${msg.content}")
        val messageText = toCryptho(msg.content)
        println("raw")
        println(messageText)
//        val nonce = messageText.slice(0, js("nacl.secretbox.nonceLength"))
//        println("nonce")
//        println(nonce)
//        val message = messageText.slice(js("nacl.secretbox.nonceLength"),messageText.length)

        println("nonce raw:")
        println(toCryptho(msg.nonce))
        val decrypted = js("nacl.secretbox").open(messageText, toCryptho(msg.nonce), js("nacl.util").decodeBase64(key))

        if (!decrypted) {
            println("Failed to decypher ...")
        } else {
//            println(js("nacl.utils").encodeUTF8(decrypted))
        }

        return Message(nonce = msg.nonce,
            senderId = msg.senderId,
            recipientId = msg.recipientId,
            senderName = msg.senderName,
            recipientName = msg.recipientName,
            content = cryptoToString(decrypted) ?: "crypto error",
            status = msg.status)
    }
}

fun toCryptho(string: String) : dynamic {

    val toBase64 = js("btoa")(string)
    return js("nacl.util").decodeBase64(toBase64)
}

fun cryptoToString(crypto: dynamic) : String? {
    val base64 = js("nacl.util").encodeBase64(crypto)
    return js("atob")(base64)
}

fun RBuilder.app(handler: RProps.() -> Unit) : ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}

