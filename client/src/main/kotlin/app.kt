import declarations.*
import kotlinx.css.*
import model.Message
import model.User
import react.*
import react.dom.div
import react.dom.h1
import react.dom.h2
import styled.css
import styled.styledDiv
import styled.styledH1
import kotlin.js.json

class App : RComponent<RProps, RState>() {
    val styles = kotlinext.js.require("@chatscope/chat-ui-kit-styles/dist/default/styles.min.css")
    override fun RBuilder.render() {
        styledH1 {
            +"Bemsi Chat App"
            css {
                fontSize = 4.em
                fontWeight = FontWeight.bold
                textAlign = TextAlign.center
            }
        }
        styledDiv {
            css {
                display = Display.flow
            }
            friendsList {  }
            chat {  }
        }
//        Container {
//            Row {
//                Col {
//
//                }
//                Col {
//
//                }
//            }
//        }
    }
}
fun RBuilder.app(handler: RProps.() -> Unit) : ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}

