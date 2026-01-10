package ro.utcn.uid.hearway.ui.composables.communicate

import android.Manifest
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import ro.utcn.uid.hearway.R
import ro.utcn.uid.hearway.tts.TtsManager

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Communicate() {
    val cameraPermissionState =
        rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(cameraPermissionState.status) {
        if (!cameraPermissionState.status.isGranted &&
            !cameraPermissionState.status.shouldShowRationale
        ) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            cameraPermissionState.status.isGranted -> {
                CameraPreview(LocalLifecycleOwner.current)
            }

            cameraPermissionState.status.shouldShowRationale -> {
                PermissionRationale(
                    onRequest = { cameraPermissionState.launchPermissionRequest() }
                )
            }

            else -> {
                PermissionDenied()
            }
        }

        CommunicateOverlay()
    }
}

@Composable
fun CameraPreview(lifecycleOwner: LifecycleOwner) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            val previewView = PreviewView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview
                    )
                } catch (e: Exception) {
                    // Fails silently. We can add logging here if needed.
                }
            }, ContextCompat.getMainExecutor(context))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun CommunicateOverlay() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color(0x80000000)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.cab_driver),
            contentDescription = "DEMO",
            modifier = Modifier.weight(2f)
        )

        // Buttons take up the remaining 1/3
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { TtsManager.speak("Yes, I am Ana") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Green
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Yes, I am Ana", color = Color.Black, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { TtsManager.speak("Thanks") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Thanks", color = Color.White, fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
                }
                Button(
                    onClick = { TtsManager.speak("Airport") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Airport", color = Color.White, fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun PermissionRationale(onRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "The camera is important for this feature. Please grant the permission.",
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRequest) {
            Text(text = "Request Permission")
        }
    }
}

@Composable
fun PermissionDenied() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Camera permission was denied. This feature is unavailable.",
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
