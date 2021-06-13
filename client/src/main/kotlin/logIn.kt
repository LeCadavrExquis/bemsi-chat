import kotlinx.browser.window
import kotlinx.css.*
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.button
import react.dom.div
import react.dom.input
import styled.css
import styled.styledDiv

external interface LogInProps: RProps {
    var onLogIn: (String) -> Boolean
}

external interface LogInState: RState {
    var username: String
}

class LogIn : RComponent<LogInProps, LogInState>() {
    override fun LogInState.init() {
        username = "type username"
    }
    override fun RBuilder.render() {
        styledDiv {
            css {
                position = Position.absolute
                top = LinearDimension("50%")
                left = LinearDimension("50%")
            }
            input {
                attrs{
                    type = InputType.text
                    value = state.username
                    onChangeFunction = { event ->
                        val target = event.target as HTMLInputElement
                        setState {
                            username = target.value
                        }
                    }
                }
            }
            button {
                attrs.onClickFunction = {
                    if(!props.onLogIn(state.username)) {
                        window.alert("Wrong username")
                    }
                }
                +"Log in"
            }
        }
    }
}

fun RBuilder.logIn(handler: LogInProps.() -> Unit): ReactElement {
    return child(LogIn::class) {
        this.attrs(handler)
    }
}