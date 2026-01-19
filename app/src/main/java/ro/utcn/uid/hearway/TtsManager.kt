package ro.utcn.uid.hearway

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

object TtsManager : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val _isInitialized = mutableStateOf(false)
    val isInitialized: State<Boolean> = _isInitialized

    fun initialize(context: Context) {
        if (_isInitialized.value) return
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            _isInitialized.value = true
            Log.d("TtsManager", "TTS Initialized.")
        } else {
            Log.e("TtsManager", "TTS Initialization failed.")
        }
    }

    fun speak(text: String) {
        if (_isInitialized.value) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.e("TtsManager", "TTS not initialized, cannot speak.")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        _isInitialized.value = false
    }
}
