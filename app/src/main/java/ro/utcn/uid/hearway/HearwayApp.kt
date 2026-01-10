package ro.utcn.uid.hearway

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ro.utcn.uid.hearway.common.UserProfile
import ro.utcn.uid.hearway.ui.composables.AppLoading

@Composable
fun HearwayApp() {
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        TtsManager.initialize(context)
        onDispose {
            TtsManager.shutdown()
        }
    }

    if (userProfile == null) {
        AppLoading(onProfileSelected = { selectedUserType ->
            userProfile = UserProfile(name = "User", userType = selectedUserType)
        })
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Dashboard for ${userProfile?.userType} user.")
        }
    }
}
