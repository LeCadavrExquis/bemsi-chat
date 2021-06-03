@file:JsModule("@chatscope/chat-ui-kit-react")
@file:JsNonModule
package declarations

import react.RClass
import react.RProps

@JsName("ConversationList")
external val conversationList: RClass<dynamic>

@JsName("Conversation")
external val conversation: RClass<ConversationProps>

external interface ConversationProps: RProps {
    var name: String
    var lastSenderName: String
    var info: String
}
