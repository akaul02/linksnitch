package com.research.hci.linksnitch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ShareActivity : ComponentActivity() {

    private val TAG = "ShareActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            UrlDetector.findUrls(it).firstOrNull()
        }

        Log.d(TAG, "Analyzing URL: $url")

        val analysisResult = url?.let { UrlAnalyzer.analyze(it) } ?: AnalysisResult.Safe
        
        if (analysisResult is AnalysisResult.Suspicious) {
            Log.d(TAG, "Findings: ${analysisResult.findings.joinToString { it.type.name }}")
        }

        val explanation = when(analysisResult) {
            is AnalysisResult.Suspicious -> ExplanationGenerator.generate(analysisResult.findings)
            else -> "This link appears to be safe."
        } ?: "This link appears to be safe."

        // Start the TtsService to speak the result
        val ttsIntent = Intent(this, TtsService::class.java)
        ttsIntent.putExtra("textToSpeak", explanation)
        startService(ttsIntent)

        setContent {
            AnalysisScreen(
                analysisResult = analysisResult,
                explanation = explanation,
                onDone = { finish() }
            )
        }
    }
}

@Composable
fun AnalysisScreen(
    analysisResult: AnalysisResult,
    explanation: String,
    onDone: () -> Unit
) {
    val isSafe = analysisResult is AnalysisResult.Safe
    val backgroundColor = if (isSafe) {
        Brush.verticalGradient(listOf(Color(0xFF1E8E3E), Color(0xFF34A853)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF9AB00), Color(0xFFFBC02D)))
    }
    val icon = if (isSafe) Icons.Filled.CheckCircle else Icons.Filled.Warning
    val title = if (isSafe) "Safe" else "Warning"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 32.sp,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = explanation,
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onDone,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Text("Done")
            }
        }
    }
}
