package app.jixxy.platform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

/** Temporary launcher screen for the M0 skeleton. UI replaces this later (T-UI). */
class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Text("Jixxy is not awake yet.") }
    }
}
