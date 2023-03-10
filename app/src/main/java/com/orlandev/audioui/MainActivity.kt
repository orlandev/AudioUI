package com.orlandev.audioui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.orlandev.audioui.ui.theme.AudioUITheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AudioUITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Greeting("Android")
                            TtsAudioUI()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val cantAudioToSpeech = TtsAudioManager.cantDataToSpeech.value
    val currentAudioPlaying = TtsAudioManager.currentTTSPlaying.value

    val context = LocalContext.current
    val locale = java.util.Locale.getDefault()

    TtsAudioManager.initialize(context, locale)

    LaunchedEffect(Unit) {
        generateTTS()
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        if (TtsAudioManager.finish.value) {
            Text(text = "FINISH")
        }


        Button(onClick = {
            TtsAudioManager.nextTTS()
        }) {

            Text(text = "NEXT")

        }

        Button(onClick = {
            generateTTS()
        }) {

            Text(text = "NEW")

        }

        Button(onClick = {
            TtsAudioManager.stop()
        }) {

            Text(text = "STOP")

        }


        Button(onClick = {

            TtsAudioManager.start()

        }) {

            Text(text = "START PLAYING TTS")

        }
        Text(text = "$cantAudioToSpeech")

        Text(text = currentAudioPlaying.toString())


    }

}

fun generateTTS(){
    TtsAudioManager.addTTS(
        AudioContent(
            "https://cdn.pixabay.com/photo/2023/01/02/04/13/dog-7691238_640.jpg",
            "GraphQL es un lenguaje de consulta y manipulaci??n de datos para APIs, y un entorno de ejecuci??n para realizar consultas con datos existentes.\u200B GraphQL fue desarrollado internamente por Facebook en 2012 antes de ser liberado p??blicamente en 2015.\u200B",
            "title  ${Random.nextInt()}"
        )
    )

    TtsAudioManager.addTTS(
        AudioContent(
            "https://cdn.pixabay.com/photo/2022/08/20/09/16/nature-7398655__340.jpg",
            "Kotlin es un lenguaje de programaci??n de tipado est??tico que corre sobre la m??quina virtual de Java y que tambi??n puede ser compilado a c??digo fuente de JavaScript. Es desarrollado principalmente por JetBrains en sus oficinas de San Petersburgo.",
            "title  ${Random.nextInt()}"
        )
    )
    TtsAudioManager.addTTS(
        AudioContent(
            "https://cdn.pixabay.com/photo/2022/11/14/20/14/compass-7592447__340.jpg",
            "Jetpack Compose es un framework (estructura o marco de trabajo que, bajo par??metros estandarizados, ejecutan tareas espec??ficas en el desarrollo de un software) con la particularidad de ejecutar pr??cticas modernas en los desarrolladores de software a partir de la reutilizaci??n de componentes, as?? como tambi??n contando ",
            "title  ${Random.nextInt()}"
        )
    )
    TtsAudioManager.addTTS(
        AudioContent(
            "https://cdn.pixabay.com/photo/2022/11/28/14/07/skyline-7622147_640.jpg",
            "Microsoft Corporation es una empresa tecnol??gica multinacional estadounidense que produce software de computadora, productos electr??nicos de consumo, computadoras personales y servicios relacionados, con sede en el campus de Microsoft ubicado en Redmond, Washington, Estados Unidos",
            "title  ${Random.nextInt()}"
        )
    )
}