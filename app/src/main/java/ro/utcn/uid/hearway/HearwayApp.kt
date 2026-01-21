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
import ro.utcn.uid.hearway.ui.composables.navigation.NavigationScreen
import ro.utcn.uid.hearway.ui.composables.navigation.RoutePlanningScreen
import ro.utcn.uid.hearway.ui.composables.navigation.FindStopsScreen
import ro.utcn.uid.hearway.ui.composables.assistance.RequestHelpScreen
import ro.utcn.uid.hearway.ui.composables.favorites.SaveRouteScreen
import ro.utcn.uid.hearway.ui.composables.reminders.SetReminderScreen
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
                    Log.d("HearwayApp", "Switching to ROUTE_PLANNING")
                    fromState = HearwayAppState.DASHBOARD
                    nextState = HearwayAppState.ROUTE_PLANNING
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

        HearwayAppState.ROUTE_PLANNING -> {
            Log.d("HearwayApp", "Rendering RoutePlanningScreen for Task 1")
            RoutePlanningScreen(
                userProfile = userProfile,
                onRouteSelected = {
                    Log.d("HearwayApp", "Route selected, moving to ACTIVE_NAVIGATION")
                    fromState = HearwayAppState.ROUTE_PLANNING
                    nextState = HearwayAppState.ACTIVE_NAVIGATION
                },
                onFindStops = {
                    Log.d("HearwayApp", "Finding nearby stops, moving to FIND_STOPS")
                    fromState = HearwayAppState.ROUTE_PLANNING
                    nextState = HearwayAppState.FIND_STOPS
                },
                onRequestHelp = {
                    Log.d("HearwayApp", "Requesting assistance, moving to REQUEST_HELP")
                    fromState = HearwayAppState.ROUTE_PLANNING
                    nextState = HearwayAppState.REQUEST_HELP
                },
                onSetReminder = {
                    Log.d("HearwayApp", "Setting reminder, moving to SET_REMINDER")
                    fromState = HearwayAppState.ROUTE_PLANNING
                    nextState = HearwayAppState.SET_REMINDER
                },
                onDismiss = {
                    Log.d("HearwayApp", "Dismissing RoutePlanningScreen")
                    nextState = fromState
                }
            )
        }

        HearwayAppState.ACTIVE_NAVIGATION -> {
            Log.d("HearwayApp", "Rendering NavigationScreen for Task 3")
            NavigationScreen(
                userProfile = userProfile,
                onDismiss = {
                    Log.d("HearwayApp", "Navigation completed, showing save route option")
                    fromState = HearwayAppState.ACTIVE_NAVIGATION
                    nextState = HearwayAppState.SAVE_ROUTE
                }
            )
        }

        HearwayAppState.FIND_STOPS -> {
            Log.d("HearwayApp", "Rendering FindStopsScreen for Task 4")
            FindStopsScreen(
                userProfile = userProfile,
                onStopSelected = {
                    Log.d("HearwayApp", "Stop selected, returning to dashboard")
                    nextState = HearwayAppState.DASHBOARD
                },
                onDismiss = {
                    Log.d("HearwayApp", "Dismissing FindStopsScreen")
                    nextState = fromState
                }
            )
        }

        HearwayAppState.REQUEST_HELP -> {
            Log.d("HearwayApp", "Rendering RequestHelpScreen for Task 5")
            RequestHelpScreen(
                userProfile = userProfile,
                onDismiss = {
                    Log.d("HearwayApp", "Dismissing RequestHelpScreen")
                    nextState = fromState
                }
            )
        }

        HearwayAppState.SAVE_ROUTE -> {
            Log.d("HearwayApp", "Rendering SaveRouteScreen for Task 8")
            SaveRouteScreen(
                userProfile = userProfile,
                onDismiss = {
                    Log.d("HearwayApp", "Route saved or dismissed, returning to dashboard")
                    nextState = HearwayAppState.DASHBOARD
                }
            )
        }

        HearwayAppState.SET_REMINDER -> {
            Log.d("HearwayApp", "Rendering SetReminderScreen for Task 9")
            SetReminderScreen(
                userProfile = userProfile,
                onDismiss = {
                    Log.d("HearwayApp", "Reminder set or dismissed, returning to previous state")
                    nextState = fromState
                }
            )
        }

        HearwayAppState.FUTURE_NAVIGATION -> {
            Log.d("HearwayApp", "Redirecting to FindStopsScreen (Task 4)")
            nextState = HearwayAppState.FIND_STOPS
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