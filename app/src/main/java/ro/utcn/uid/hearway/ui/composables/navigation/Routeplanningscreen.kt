package ro.utcn.uid.hearway.ui.composables.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ro.utcn.uid.hearway.common.HapticManager
import ro.utcn.uid.hearway.common.UserProfile
import ro.utcn.uid.hearway.common.UserType
import ro.utcn.uid.hearway.tts.TtsManager

@Composable
fun RoutePlanningScreen(
    userProfile: UserProfile?,
    onRouteSelected: () -> Unit,
    onFindStops: () -> Unit,
    onRequestHelp: () -> Unit,
    onSetReminder: () -> Unit,
    onDismiss: () -> Unit
) {
    val routes = listOf(
        RouteOption("Route 1", "Bus 24", "20 min", "Quiet route"),
        RouteOption("Route 2", "Metro + Bus", "25 min", "One transfer"),
        RouteOption("Route 3", "Walking", "35 min", "No vehicles")
    )

    var selectedIndex by remember { mutableIntStateOf(0) }
    var routeStarted by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val isBlind = userProfile?.userType == UserType.BLIND

    LaunchedEffect(Unit) {
        HapticManager.pulse()
        focusRequester.requestFocus()
        if (isBlind) {
            delay(500)
            TtsManager.speak("Route planning. Three routes found. Use volume down to cycle routes, volume up to start navigation. Currently selected: ${routes[selectedIndex].name}, ${routes[selectedIndex].transport}, ${routes[selectedIndex].duration}")
        }
    }

    // Auto-start navigation after selection
    LaunchedEffect(routeStarted) {
        if (routeStarted) {
            delay(1500) // Show confirmation for 1.5 seconds
            onRouteSelected()
        }
    }

    BackHandler {
        onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.nativeKeyEvent.keyCode) {
                        android.view.KeyEvent.KEYCODE_VOLUME_DOWN -> {
                            selectedIndex = (selectedIndex + 1) % routes.size
                            HapticManager.distinct()
                            if (isBlind) {
                                val route = routes[selectedIndex]
                                TtsManager.speak("${route.name}, ${route.transport}, ${route.duration}, ${route.description}")
                            }
                            true
                        }
                        android.view.KeyEvent.KEYCODE_VOLUME_UP -> {
                            routeStarted = true
                            HapticManager.success()
                            if (isBlind) {
                                TtsManager.speak("Route selected. Starting navigation.")
                            }
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!routeStarted) {
            // Header
            Text(
                text = "PLAN YOUR ROUTE",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Fake destination field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Destination",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Central Station",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "ROUTE OPTIONS",
                color = Color.LightGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Route cards
            routes.forEachIndexed { index, route ->
                RouteCard(
                    route = route,
                    isSelected = index == selectedIndex,
                    onClick = {
                        selectedIndex = index
                        routeStarted = true
                        HapticManager.success()
                        if (isBlind) {
                            TtsManager.speak("Route selected. Starting navigation.")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Find Stops button (Task 4)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2196F3), RoundedCornerShape(12.dp))
                    .clickable {
                        HapticManager.pulse()
                        if (isBlind) {
                            TtsManager.speak("Finding nearby stops")
                        }
                        onFindStops()
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FIND NEARBY STOPS",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Request Help button (Task 5)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF9800), RoundedCornerShape(12.dp))
                    .clickable {
                        HapticManager.pulse()
                        if (isBlind) {
                            TtsManager.speak("Request assistance")
                        }
                        onRequestHelp()
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "REQUEST ASSISTANCE",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Set Reminder button (Task 9)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF9C27B0), RoundedCornerShape(12.dp))
                    .clickable {
                        HapticManager.pulse()
                        if (isBlind) {
                            TtsManager.speak("Set departure reminder")
                        }
                        onSetReminder()
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SET DEPARTURE REMINDER",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isBlind) {
                Text(
                    text = "Vol Down: cycle routes, Vol Up: start",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Tap a route to start navigation",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Route started confirmation
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "✓",
                    color = Color.Green,
                    fontSize = 100.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ROUTE STARTED",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Starting navigation...",
                    color = Color.LightGray,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RouteCard(
    route: RouteOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color(0xFF4CAF50) else Color(0xFF1A1A1A),
                RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = if (isSelected) Color.Green else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = route.name,
                    color = if (isSelected) Color.Black else Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = route.transport,
                    color = if (isSelected) Color.Black else Color.LightGray,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = route.description,
                    color = if (isSelected) Color.Black else Color.Gray,
                    fontSize = 14.sp
                )
            }

            Text(
                text = route.duration,
                color = if (isSelected) Color.Black else Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class RouteOption(
    val name: String,
    val transport: String,
    val duration: String,
    val description: String
)