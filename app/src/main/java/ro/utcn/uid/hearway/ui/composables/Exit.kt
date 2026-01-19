package ro.utcn.uid.hearway.ui.composables

import android.app.Activity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun Exit(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Exit Midterm application") },
        text = { Text("Are you sure you want to exit the application")},
        confirmButton = {
            TextButton( onClick = {
                (context as? Activity)?.finish()
            }) { Text("Yes") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("No")}
        }
    )

}