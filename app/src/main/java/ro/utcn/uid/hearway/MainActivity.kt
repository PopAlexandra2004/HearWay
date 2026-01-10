package ro.utcn.uid.hearway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ro.utcn.uid.hearway.ui.theme.MyHearwayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyHearwayTheme {
                // A surface container using the 'background' color from the theme
                HearwayApp()
            }
        }
    }
}

