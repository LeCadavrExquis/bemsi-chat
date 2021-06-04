@file:JsModule("@chatscope/chat-ui-kit-react")
@file:JsNonModule
package declarations

import kotlinx.coroutines.CompletionHandler
import model.Message
import react.*

external interface MessageProps: RProps {
    var model: dynamic
}

@JsName("Message")
external val messageBubble: RClass<MessageProps>