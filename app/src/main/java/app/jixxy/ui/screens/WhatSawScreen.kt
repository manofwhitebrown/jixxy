package app.jixxy.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.jixxy.R
import app.jixxy.ui.theme.JixxyTheme

/** Skeleton for What Jixxy saw. Real layout lands with T-UI-4. Copy is placeholder (see strings.xml). */
@Composable
fun WhatSawScreen() {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.placeholder_what_saw), style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Preview(showBackground = true, name = "dark") @Composable private fun WhatSawScreenPreviewDark() = JixxyTheme(dark = true) { WhatSawScreen() }
@Preview(showBackground = true, name = "light") @Composable private fun WhatSawScreenPreviewLight() = JixxyTheme(dark = false) { WhatSawScreen() }
