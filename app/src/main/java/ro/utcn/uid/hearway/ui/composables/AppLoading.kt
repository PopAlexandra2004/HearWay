package ro.utcn.uid.hearway.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ro.utcn.uid.hearway.tts.TtsManager
import ro.utcn.uid.hearway.common.UserType
import ro.utcn.uid.hearway.ui.theme.MyHearwayTheme

@Composable
fun AppLoading(
    onProfileSelected: (UserType) -> Unit,
    onError: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val ttsInitialized by TtsManager.isInitialized
    var error by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    if (isError) {
        error = "Custom error"
        onError(error ?: "Unknown error")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event: KeyEvent ->
                if (event.type == KeyEventType.KeyDown && event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_VOLUME_DOWN) {
                    onProfileSelected(UserType.BLIND)
                    true
                } else {
                    false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select your Profile",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = { onProfileSelected(UserType.DEAF) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            modifier = Modifier.fillMaxWidth().height(80.dp)
        ) {
            Text(text = "Deaf", color = Color.Black, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { /* Non-clickable */ },
            enabled = false,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray,
                disabledContainerColor = Color.DarkGray
            ),
            modifier = Modifier.fillMaxWidth().height(80.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Blind", color = Color.White, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowDownward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }

    LaunchedEffect(ttsInitialized) {
        focusRequester.requestFocus()
        if (ttsInitialized) {
            TtsManager.speak("To select the blind profile, please press the volume down button.")
        }
    }
}
