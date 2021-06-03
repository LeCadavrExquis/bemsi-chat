@file:JsModule("@chatscope/chat-ui-kit-react")
@file:JsNonModule
package declarations

import react.RClass
import react.RProps

external interface MessageInputProps: RProps {
    var placeholder: String
}

@JsName("MessageInput")
external val messageInput: RClass<MessageInputProps>

