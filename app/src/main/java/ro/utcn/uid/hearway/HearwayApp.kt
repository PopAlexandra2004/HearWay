package ro.utcn.uid.hearway

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import ro.utcn.uid.hearway.common.HapticManager
import ro.utcn.uid.hearway.common.HearwayAppState
import ro.utcn.uid.hearway.common.UserProfile
import ro.utcn.uid.hearway.common.UserType
import ro.utcn.uid.hearway.tts.TtsManager
import ro.utcn.uid.hearway.ui.composables.Error
import ro.utcn.uid.hearway.ui.composables.FutureImplementationScreen
import ro.utcn.uid.hearway.ui.composables.communicate.Communicate
import ro.utcn.uid.hearway.ui.composables.dashboard.Dashboard
import ro.utcn.uid.hearway.ui.composables.emergency.EmergencyScreen
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
        HapticManager.initialize(context)
        onDispose {
            TtsManager.shutdown()
        }
    }

    Log.d("HearwayApp", "Current State: $nextState")

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
                userProfile = userProfile,
                onNavigate = {
                    Log.d("HearwayApp", "Switching to FUTURE_NAVIGATION")
                    fromState = HearwayAppState.DASHBOARD
                    nextState = HearwayAppState.FUTURE_NAVIGATION
                },
                onCommunicate = {
                    Log.d("HearwayApp", "Switching to COMMUNICATE")
                    fromState = HearwayAppState.DASHBOARD
                    nextState = HearwayAppState.COMMUNICATE
                },
                onEmergency = {
                    Log.d("HearwayApp", "Switching to EMERGENCY from Dashboard click/long press")
                    fromState = HearwayAppState.DASHBOARD
                    nextState = HearwayAppState.EMERGENCY
                }
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

        HearwayAppState.EMERGENCY -> {
            Log.d("HearwayApp", "Rendering EmergencyScreen Now")
            EmergencyScreen(
                userProfile = userProfile,
                onDismiss = { showCancelledToast -> 
                    if (showCancelledToast) {
                        Toast.makeText(context, "Reporting Cancelled", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("HearwayApp", "Dismissing EmergencyScreen")
                    nextState = fromState 
                }
            )
        }

        HearwayAppState.FUTURE_NAVIGATION -> {
            Log.d("HearwayApp", "Rendering FutureImplementationScreen")
            FutureImplementationScreen(
                onDismiss = { 
                    Log.d("HearwayApp", "Dismissing FutureImplementationScreen")
                    nextState = fromState
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
