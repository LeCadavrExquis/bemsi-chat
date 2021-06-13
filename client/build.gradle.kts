import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
plugins {
    kotlin("js") version "1.5.10"
}

group = "pl.pw"
version = "0.0.1-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
}

kotlin {
    js(LEGACY) {
        browser {
            testTask {
                enabled = false
            }
            commonWebpackConfig {
                cssSupport.enabled = true

            }
            binaries.executable()
        }
    }
    sourceSets {
        val main by getting {
            dependencies {
                implementation("org.jetbrains:kotlin-react:17.0.1-pre.148-kotlin-1.4.30")
                implementation("org.jetbrains:kotlin-react-dom:17.0.1-pre.148-kotlin-1.4.30")
                implementation("org.jetbrains:kotlin-styled:5.2.1-pre.148-kotlin-1.4.30")
                implementation("org.jetbrains:kotlin-react-router-dom:5.2.0-pre.148-kotlin-1.4.30")
                implementation("org.jetbrains:kotlin-redux:4.0.5-pre.148-kotlin-1.4.30")
                implementation("org.jetbrains:kotlin-react-redux:7.2.2-pre.148-kotlin-1.4.30")

//                implementation(npm("sockjs-client", "1.5.1"))
//                implementation(npm("@stomp/stompjs", "6.1.0"))

                implementation(npm("jquery", "3.6.0"))
                implementation(npm("react-bootstrap", "1.6.0"))
                implementation(npm("@chatscope/chat-ui-kit-react", "1.8.1"))
                implementation(npm("@chatscope/chat-ui-kit-styles", "1.2.0"))
            }
        }
    }
}
//
//tasks.named<KotlinJsCompile>("compileKotlinJs").configure {
//    kotlinOptions.moduleKind = "commonjs"
//}