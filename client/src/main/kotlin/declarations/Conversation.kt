@file:JsModule("@chatscope/chat-ui-kit-react")
@file:JsNonModule
package declarations

import react.Component
import react.RClass
import react.RProps
import react.RState

@JsName("ConversationList")
external val conversationList: RClass<dynamic>

@JsName("Conversation")
external val conversation: RClass<ConversationProps>
external interface ConversationProps: RProps {
    var name: String
    var lastSenderName: String
    var info: String
}

@JsName("ConversationHeader")
external class ConversationHeader : Component<RProps, RState> {
    override fun render()
    class Content : Component<ContentProps, RState> {
        override fun render()
    }
}
external interface ContentProps: RProps {
    var userName: String
    var info: String
}