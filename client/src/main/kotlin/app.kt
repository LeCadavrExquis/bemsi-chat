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
    var e2eActive: Boolean
}

class App : RComponent<RProps, AppState>() {
    @Suppress("unused")
    val styles = require("@chatscope/chat-ui-kit-styles/dist/default/styles.min.css")
    var stomp: dynamic = null
    var symmetricKey: dynamic = null
    var asymKeyPair: dynamic = null
    var sharedKey: dynamic = null

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
                    onEncrypt = {
                        openE2E()
                    }
                    encryptionActive = state.e2eActive
                }
            }
        }
    }

    override fun AppState.init() {
        user = User(id = "", name = "guest", friends = emptyList())
        friend = "" to ""
        messages = emptyList()
        e2eActive = false
    }

    private fun onLogIn(username: String): Boolean{
        if(userList.filter { it.name == username }.isNotEmpty()) {
            val tmpUser = userList.filter { it.name == username }.take(1)[0]
            var socket = js("new SockJS(\"http://localhost:8080/ws\")")
            val tmpstomp = js("Stomp").over(socket)
            tmpstomp.connect(js("{}"), this@App::connectionSuccess, {_-> println("connection failed")})
            stomp = tmpstomp
            setState {
                user = tmpUser
                friend = tmpUser.friends.take(1)[0]
            }
        } else {
            return false
        }
        return true
    }
    private fun connectionSuccess() {
        stomp.subscribe("/user/${state.user.id}/queue/messages", this@App::onMessageReceived)
    }
    private fun initKeys() {
        val asymKeys = js("nacl.box").keyPair()
        println("private key:")
        println(asymKeys.secretKey)
        println("public key:")
        println(asymKeys.publicKey)
        val symKey = js("nacl.util.encodeBase64(nacl.randomBytes(nacl.secretbox.keyLength))")
        println("sym key: $symKey")
        asymKeyPair = asymKeys
        symmetricKey = symKey
    }
    private fun openE2E() {
        initKeys()
        sendMessage(
            Message(
                senderId = state.user.id,
                senderName = state.user.name,
                recipientId = state.friend.first,
                recipientName = state.friend.second,
                status = "PUBLICKEY",
                content = cryptoToString(asymKeyPair.publicKey) ?: "error casting key :/"
            ))
    }

    private fun onMessageReceived(payload: dynamic) {
        val message: Message = JSON.parse(payload.body as String)
        println("message recived: ${message.id}")
        handleUserCom(message)
    }
    private fun sendMessage(msg: Message) {
        if (msg.status == "CHAT") {
            setState {
                messages = messages + msg
            }
        }
        val msgToSend = if (state.e2eActive) encrypt(msg, symmetricKey) else msg

        stomp.send("/app/chat", js{"{}"},
            JSON.stringify(msgToSend))
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
        val messageText = toCrypto(msg.content)
        println("raw")
        println(messageText)
        println("nonce raw:")
        println(toCrypto(msg.nonce))

        val decrypted = js("nacl.secretbox").open(messageText, toCrypto(msg.nonce), js("nacl.util").decodeBase64(key))

        val msgStatus = if (!decrypted) {
            println("Failed to decypher ...")
            "CRYPT_FAIL"
        } else {
            println("msg = ${cryptoToString(decrypted)}")
            msg.status
        }

        return Message(nonce = msg.nonce,
            senderId = msg.senderId,
            recipientId = msg.recipientId,
            senderName = msg.senderName,
            recipientName = msg.recipientName,
            content = cryptoToString(decrypted) ?: "crypto error",
            status = msgStatus)
    }
    private fun handleUserCom(msg: Message) {
        when(msg.status) {
            "CHAT" -> {
                setState {
                    messages = state.messages + if (state.e2eActive) decrypt(msg, symmetricKey) else msg
                }
            }
            "PUBLICKEY" -> {
                initKeys()
                sharedKey = toCrypto(msg.content)
                println("sending public key: ")
                println(asymKeyPair.publickey)
                sendMessage(Message(
                    content = cryptoToString(asymKeyPair.publicKey) ?: "codec error",
                    status = "PUBLICKEYRETURN",
                    senderId = state.user.id,
                    senderName = state.user.name,
                    recipientId = state.friend.first,
                    recipientName = state.friend.second)
                )
            }
            "PUBLICKEYRETURN" -> {
                println("public key returned: ${msg.content}")

                sharedKey = toCrypto(msg.content)

                val newNonce = js("nacl.randomBytes(nacl.box.nonceLength)")
                println("newNonce:")
                println(newNonce)
                val messageUint8 = js("nacl.util").decodeBase64(symmetricKey)
                val encrypted = js("nacl").box(messageUint8, newNonce, sharedKey, asymKeyPair.secretKey)

                sendMessage(Message(
                    status = "SECRET",
                    content = cryptoToString(encrypted) ?: "asym encryption fail",
                    nonce = cryptoToString(newNonce) ?: "fail converting/creating nonce ?",
                    senderId = state.user.id,
                    senderName = state.user.name,
                    recipientId = state.friend.first,
                    recipientName = state.friend.second)
                )
                setState{
                    e2eActive = true
                }
            }
            "SECRET" -> {
                println("secret recived: ${msg.content}")
                println("shared key: $sharedKey")
                println("my public: ${cryptoToString(asymKeyPair.publicKey)}")
                val decrypted = js("nacl.box").open(
                    toCrypto(msg.content),
                    toCrypto(msg.nonce),
                    sharedKey,
                    asymKeyPair.secretKey)

                if (!decrypted) {
                    println("decryption failed ...")
                } else {
                    setState {
                        symmetricKey = js("nacl.util").encodeBase64(decrypted)
                        e2eActive = true
                    }
                }
            }
        }
    }
}

fun RBuilder.app(handler: RProps.() -> Unit) : ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}

fun toCrypto(string: String) : dynamic {
    val toBase64 = js("btoa")(string)
    return js("nacl.util").decodeBase64(toBase64)
}

fun cryptoToString(crypto: dynamic) : String? {
    val base64 = js("nacl.util").encodeBase64(crypto)
    return js("atob")(base64)
}