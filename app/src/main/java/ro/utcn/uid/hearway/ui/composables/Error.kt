package ro.utcn.uid.hearway.ui.composables

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext

@Composable
fun Error(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var retryEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            enabled = retryEnabled,
            onClick = {
                retryEnabled = false
            }
        ) {
            Text("Retry in 10 seconds")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton( onClick = {
            (context as? Activity)?.finish()
        }) { Text("Exit application") }

    }

    if (!retryEnabled) {
        LaunchedEffect(Unit) {
            delay(10_000)
            onRetry()
        }
    }
}
