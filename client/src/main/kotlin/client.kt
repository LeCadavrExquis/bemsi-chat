import react.dom.render
import kotlinx.browser.document
import kotlinx.browser.window
import react.dom.button
import react.dom.h1

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            app { }
        }
    }
}
