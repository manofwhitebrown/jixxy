package app.jixxy.platform

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Temporary M1 screen: grant overlay permission, start/stop the sprite. UI replaces this (T-UI). */
class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
        setContent {
            Column(Modifier.padding(24.dp)) {
                Button(onClick = {
                    startActivity(
                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    )
                }) { Text("1. Allow overlay") }
                Button(onClick = {
                    OverlayService.wake(this@LauncherActivity)
                }) { Text("2. Wake Jixxy") }
                Button(onClick = { OverlayService.nap(this@LauncherActivity) }) { Text("Nap 1h") }
                Button(onClick = {
                    OverlayService.stop(this@LauncherActivity)
                }) { Text("Stop") }
            }
        }
    }
}
