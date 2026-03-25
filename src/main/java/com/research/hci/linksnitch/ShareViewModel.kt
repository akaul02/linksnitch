package com.research.hci.linksnitch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ShareUiState(
    val analysisResult: AnalysisResult = AnalysisResult.Safe,
    val explanation: String = "This link appears to be safe."
)

class ShareViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ShareUiState())
    val uiState: StateFlow<ShareUiState> = _uiState.asStateFlow()

    fun analyzeUrlFromText(text: String?) {
        val url = text?.let { UrlDetector.findUrls(it).firstOrNull() }
        
        val result = url?.let { UrlAnalyzer.analyze(it) } ?: AnalysisResult.Safe
        
        val explanationMessage = when (result) {
            is AnalysisResult.Suspicious -> ExplanationGenerator.generate(result.findings) ?: "Analysis complete."
            else -> "This link appears to be safe."
        }

        _uiState.value = ShareUiState(
            analysisResult = result,
            explanation = explanationMessage
        )
    }
}
