package ro.utcn.uid.hearway

import android.app.Activity
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
import ro.utcn.uid.hearway.ui.composables.Error
import ro.utcn.uid.hearway.ui.composables.communicate.Communicate
import ro.utcn.uid.hearway.ui.composables.dashboard.Dashboard
import ro.utcn.uid.hearway.ui.composables.profile.AppLoading


@Composable
fun HearwayApp() {
    var error       by remember { mutableStateOf<String?>(null) }
    var fromState   by rememberSaveable { mutableStateOf(HearwayAppState.INIT) }
    var nextState   by rememberSaveable { mutableStateOf(HearwayAppState.LOAD_TTS) }
    var userProfile by rememberSaveable { mutableStateOf<UserProfile?>(null) }
    val context     = LocalContext.current

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
            AppLoading(
                onProfileSelected = { selectedUserType ->
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
                    fromState = HearwayAppState.DASHBOARD
                    nextState = HearwayAppState.COMMUNICATE
                },
                onEmergency = { Log.d("HearwayApp", "Emergency clicked") }
            )
        }

        HearwayAppState.COMMUNICATE -> {
            Communicate(
                onDismiss = { nextState = fromState },
                onError = { it ->
                    error = it
                    fromState = HearwayAppState.COMMUNICATE
                    nextState = HearwayAppState.ERROR
                }
            )
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
            val activity = (context as? Activity)
            activity?.finishAndRemoveTask()
        }
    }
}
