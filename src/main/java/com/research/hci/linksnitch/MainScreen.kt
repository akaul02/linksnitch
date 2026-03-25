package com.research.hci.linksnitch

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    isAccessibilityServiceEnabled: Boolean,
    onEnableAccessibilityServiceClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "LinkSnitch",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "To use this app, share a link from any other app and choose LinkSnitch from the share menu.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        if (!isAccessibilityServiceEnabled) {
            Text(
                text = "LinkSnitch accessibility service is not enabled.",
                textAlign = TextAlign.Center,
                color = androidx.compose.ui.graphics.Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onEnableAccessibilityServiceClick) {
                Text("Enable Accessibility Service")
            }
        }
    }
}
