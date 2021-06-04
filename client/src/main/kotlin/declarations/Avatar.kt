@file:JsModule("@chatscope/chat-ui-kit-react")
@file:JsNonModule
package declarations

import react.RClass
import react.RProps

@JsName("Avatar")
external val avatar: RClass<AvatarProps>

external interface AvatarProps : RProps {
    var src: String
    var name: String
    var status: String
}