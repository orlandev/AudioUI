package com.orlandev.audioui

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.delay
import java.util.*
import kotlin.random.Random

data class AudioContent(
    val imgUrl: String,
    val textToSpeech: String,
    val title: String
)


/***
 *
 *  Apps targeting Android 11 that use text-to-speech should declare INTENT_ACTION_TTS_SERVICE in the queries elements of their manifest:
 * <queries>
 * ...
 * <intent>
 * <action android:name="android.intent.action.TTS_SERVICE" />
 * </intent>
 * </queries>
 *
 */

/***
 *
 *  Use TtsAudioManager.showUi.value to Show or Hide the component in
 *  AnimatedVisibility
 *
 *
 *
 */

@Composable
fun TtsAudioUI(modifier: Modifier = Modifier) {

    val imgUrl = TtsAudioManager.currentTTSPlaying.value

    Card(
        modifier = Modifier
            .height(90.dp)
            .then(modifier)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            if (imgUrl != null) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(2f)
                ) {
                    SubcomposeAsyncImage(
                        contentDescription = "",
                        model = imgUrl,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center

                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(2f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
            //}

            Text(
                modifier = Modifier
                    .weight(6f)
                    .padding(start = 4.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                text = TtsAudioManager.currentTTSPlaying.value?.title ?: "",
                fontSize = 18.sp,
                fontWeight = FontWeight.Light
            )

            AudioFX(
                modifier = Modifier
                    .width(70.dp)
                    .height(20.dp),
                maxLines = 20,
                lineWidth = 1.dp,
                maxLineHeight = 40
            )

            IconButton(
                modifier = Modifier
                    .weight(2f),
                onClick = {
                    TtsAudioManager.stop()
                }
            )
            {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }

        }
    }
}

object TtsAudioManager {

    private var initialized = false

    /***
     *  Use this to show or not the UI     *
     */
    var showUi = TtsSystem.isPlaying
        private set

    private var contentToSpeech: MutableSet<AudioContent> = mutableSetOf()

    var cantDataToSpeech: MutableState<Int> = mutableStateOf(0)
        private set

    var currentTTSPlaying = mutableStateOf<AudioContent?>(null)
        private set

    var finish = mutableStateOf(false)

    fun addTTS(newTTS: AudioContent): Boolean {
        val result = contentToSpeech.add(newTTS)
        cantDataToSpeech.value = contentToSpeech.size
        return result
    }

    fun nextTTS() {

        if (!initialized) return

        val first = contentToSpeech.firstOrNull()

        first?.let { audioToPlay ->

            currentTTSPlaying.value = audioToPlay
            contentToSpeech.remove(audioToPlay)
            cantDataToSpeech.value = contentToSpeech.size
            play(audioToPlay.textToSpeech)

            return
        }

        finish.value = true

    }

    fun initialize(context: Context, locale: Locale) {
        TtsSystem.initializeTTS(context, locale)
        initialized = true
    }

    fun start() {
        if (!initialized) return

        nextTTS()

    }

    fun play(textToSpeech: String) {

        if (!initialized) return

        TtsSystem.speech(textToSpeech)

    }

    fun stop() {
        TtsSystem.onStopTts()
        showUi.value = false
    }
}

private object TtsSystem {

    private var ttsSystem: TextToSpeech? = null

    var isError = mutableStateOf(false)
        private set

    var isPlaying = mutableStateOf(false)
        private set

    var isDone = mutableStateOf(false)
        private set


    fun initializeTTS(context: Context, currentLocale: Locale) {
        ttsSystem = TextToSpeech(context) { status ->
            when (status) {
                TextToSpeech.SUCCESS -> {
                    Log.d("MyTextToSpeech", "INNER_FIRST")
                    ttsSystem?.setOnUtteranceProgressListener(object :
                        UtteranceProgressListener() {
                        override fun onStart(utteranceId: String) {
                            Log.d("MyTextToSpeech", "On Start")
                            isPlaying.value = true
                            isDone.value = false
                        }

                        override fun onDone(utteranceId: String) {
                            Log.d("MyTextToSpeech", "On Done")
                            isPlaying.value = false
                            isDone.value = true
                            TtsAudioManager.nextTTS()
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String) {
                            Log.d("MyTextToSpeech", "On Error")
                            isError.value = true
                        }
                    })
                }
            }
        }

    }

    fun speech(textToSpeechStr: String) {
        ttsSystem?.speak(textToSpeechStr, TextToSpeech.QUEUE_ADD, null, "tts1")
    }

    fun onPauseTts() {
        isPlaying.value = false
        ttsSystem?.stop()

    }

    fun onStopTts() {
        isPlaying.value = false
        ttsSystem?.stop()
        ttsSystem = null
    }

    fun onErrorClear() {
        isError.value = false
    }


}


@Composable
fun AudioFX(
    modifier: Modifier = Modifier,
    maxLines: Int = 20,
    maxLineHeight: Int = 100,
    lineWidth: Dp = 5.dp,
    timeMillis: Long = 100
) {
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            repeat((1..maxLines).count()) {
                LineBox(maxLineHeight, MaterialTheme.colorScheme.primary, timeMillis, lineWidth)
            }
        }
    }
}

@Composable
fun LineBox(maxHeight: Int, color: Color, timeMillis: Long = 100, lineWidth: Dp) {
    val rand = remember {
        mutableStateOf(0.dp)
    }
    val heightDp = animateDpAsState(targetValue = rand.value)

    LaunchedEffect(Unit) {
        Log.d("STARTER", "RECOMPOSE")
        while (true) {
            rand.value = Dp(Random.nextInt(0, maxHeight).toFloat())
            delay(timeMillis)
        }
    }

    Box(
        modifier = Modifier
            .height(heightDp.value)
            .width(lineWidth)
            .clip(CircleShape)
            .background(color)
    )

}
