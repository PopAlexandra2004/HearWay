package ro.utcn.uid.hearway

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import ro.utcn.uid.hearway.common.HearwayAppState
import ro.utcn.uid.hearway.common.UserProfile
import ro.utcn.uid.hearway.tts.TtsManager
import ro.utcn.uid.hearway.ui.composables.communicate.Communicate
import ro.utcn.uid.hearway.ui.composables.dashboard.Dashboard
import ro.utcn.uid.hearway.ui.composables.Exit
import ro.utcn.uid.hearway.ui.composables.Error
import ro.utcn.uid.hearway.ui.composables.profile.AppLoading


@Composable
fun HearwayApp() {
    var userProfile by rememberSaveable { mutableStateOf<UserProfile?>(null) }
    var error       by remember { mutableStateOf<String?>(null) }
    var fromState   by rememberSaveable { mutableStateOf(HearwayAppState.LOAD_TTS) }
    var nextState   by rememberSaveable { mutableStateOf(HearwayAppState.LOAD_TTS) }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        TtsManager.initialize(context)
        onDispose {
            TtsManager.shutdown()
        }
    }

    when (nextState) {
        HearwayAppState.LOAD_TTS -> {
            val ttsInitialized by TtsManager.isInitialized
            LaunchedEffect(ttsInitialized) {
                if (ttsInitialized) {
                    nextState = HearwayAppState.INIT
                }
            }
        }

        HearwayAppState.INIT -> {
            AppLoading(onProfileSelected = { selectedUserType ->
                userProfile = UserProfile(name = "User", userType = selectedUserType)
                nextState = HearwayAppState.DASHBOARD
            },
                onError = { it ->
                    error = it
                    fromState = HearwayAppState.INIT
                    nextState = HearwayAppState.ERROR
                }
            )
        }

        HearwayAppState.DASHBOARD -> {
            Dashboard(
                onNavigate = { Log.d("HearwayApp", "Navigate clicked") },
                onCommunicate = {
                    nextState = HearwayAppState.COMMUNICATE
                    Log.d("HearwayApp", "Communicate clicked") },
                onEmergency = { Log.d("HearwayApp", "Emergency clicked") }
            )
        }

        HearwayAppState.COMMUNICATE -> {
            Communicate()
        }

        HearwayAppState.ERROR -> {
            Error(
                error = error!!,
                onRetry = {
                    nextState = fromState
                }
            )
        }

        HearwayAppState.EXIT -> {
            Exit(
                onDismissRequest = {
                    nextState = HearwayAppState.INIT
                }
            )
        }
    }
}
