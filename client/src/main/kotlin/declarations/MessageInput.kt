@file:JsModule("@chatscope/chat-ui-kit-react")
@file:JsNonModule
package declarations

import org.w3c.dom.NodeList
import react.RClass
import react.RProps

external interface MessageInputProps: RProps {
    var value: String
    var placeholder: String
    var onSend: (innerHtml: String, textContent: String, innerText: String, nodes: NodeList) -> Unit
}

@JsName("MessageInput")
external val messageInput: RClass<MessageInputProps>

