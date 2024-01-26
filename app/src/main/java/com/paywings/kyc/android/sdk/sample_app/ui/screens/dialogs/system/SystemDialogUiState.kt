package com.paywings.kyc.android.sdk.sample_app.ui.screens.dialogs.system

sealed class SystemDialogUiState {
    class ShowError(val errorMessage: String): SystemDialogUiState()
    data object ShowNoInternetConnection: SystemDialogUiState()
    data object ShowTooManySMSRequest: SystemDialogUiState()
}
