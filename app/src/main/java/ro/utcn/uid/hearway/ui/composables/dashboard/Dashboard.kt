package ro.utcn.uid.hearway.ui.composables.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ro.utcn.uid.hearway.ui.theme.MyHearwayTheme

@Composable
fun Dashboard(
    onNavigate: () -> Unit,
    onCommunicate: () -> Unit,
    onEmergency: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(all = 20.dp)
    ) {
        Text(
            text = "Where to?",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp, top = 20.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(
                onClick = onNavigate,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Green
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .semantics { contentDescription = "Start Navigation" }
            ) {
                Text(
                    text = "START NAVIGATION",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onCommunicate,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Communicate" }
            ) {
                Text(
                    text = "COMMUNICATE",
                    color = Color.Black,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Button(
                onClick = onEmergency,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Emergency (SOS)" }
            ) {
                Text(
                    text = "EMERGENCY SOS",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
