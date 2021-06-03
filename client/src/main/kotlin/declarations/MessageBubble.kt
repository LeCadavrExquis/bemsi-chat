@file:JsModule("@chatscope/chat-ui-kit-react")
@file:JsNonModule
package declarations

import kotlinx.coroutines.CompletionHandler
import model.Message
import react.*

external interface MessageProps: RProps {
    var model: dynamic
}

external interface FooterProps: RProps {
    var sender: String
    var sentTime: String
}

@JsName("Message")
external class MessageBubble: Component<MessageProps, RState> {
    override fun render(): ReactElement?
    class Footer: Component<FooterProps, RState> {
        override fun render(): ReactElement?
    }
}