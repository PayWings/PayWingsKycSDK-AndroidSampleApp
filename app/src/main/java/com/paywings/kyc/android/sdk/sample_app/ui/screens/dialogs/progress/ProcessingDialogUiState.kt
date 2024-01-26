package com.paywings.kyc.android.sdk.sample_app.ui.screens.dialogs.progress

sealed class ProcessingDialogUiState {
    object ShowProcessing: ProcessingDialogUiState()
    object HideProcessing: ProcessingDialogUiState()
}