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
                        AnimatedVisibility(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { 200 }) + fadeOut(),
                            visible = TtsAudioManager.showUi.value
                        ) {
                            TtsAudioUI()
                        }
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
        TtsAudioManager.addTTS(
            AudioContent(
                "url image",
                "Esto es una prueba",
                "title  ${Random.nextInt()}"
            )
        )

        TtsAudioManager.addTTS(
            AudioContent(
                "url image",
                "Esta es la segunda prueba",
                "title  ${Random.nextInt()}"
            )
        )
        TtsAudioManager.addTTS(
            AudioContent(
                "url image",
                "Una tercera",
                "title  ${Random.nextInt()}"
            )
        )
        TtsAudioManager.addTTS(
            AudioContent(
                "url image",
                "La ultima",
                "title  ${Random.nextInt()}"
            )
        )
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
