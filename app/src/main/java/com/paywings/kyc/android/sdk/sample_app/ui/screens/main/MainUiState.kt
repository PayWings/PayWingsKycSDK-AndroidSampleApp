package com.paywings.kyc.android.sdk.sample_app.ui.screens.main

import androidx.compose.ui.graphics.Color
import com.paywings.kyc.android.sdk.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.kyc.android.sdk.sample_app.util.OneTimeEvent

data class MainUiState(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val kycStatus: String? = null,
    val kycStatusColor: Color = Color.Black,
    val kycReferenceId: String? = null,
    val kycPersonId: String = "",
    val kycId: String = "",
    val isGetUserDataInProgress: Boolean = false,
    val isKycInProgress: Boolean = false,
    val startKyc: OneTimeEvent<Boolean>? = null,
    val systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    val showReferenceNumberInvalidDialog: OneTimeEvent<Boolean>? = null,
    val showReferenceNumberAlreadyUsedDialog: OneTimeEvent<Boolean>? = null,
    val showEmailNotMatchingReferenceNumberDialog: OneTimeEvent<Boolean>? = null,
    val showPhoneNumberNotMatchingReferenceNumberDialog: OneTimeEvent<Boolean>? = null,
)

fun MainUiState.updateState(
    userId: String = this.userId,
    firstName: String = this.firstName,
    lastName: String = this.lastName,
    email: String = this.email,
    phoneNumber: String = this.phoneNumber,
    kycStatus: String? = this.kycStatus,
    kycStatusColor: Color = this.kycStatusColor,
    kycReferenceId: String? = this.kycReferenceId,
    kycPersonId: String = this.kycPersonId,
    kycId: String = this.kycId,
    isGetUserDataInProgress: Boolean = false,
    isKycInProgress: Boolean = false,
    startKyc: OneTimeEvent<Boolean>? = null,
    systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    showReferenceNumberInvalidDialog: OneTimeEvent<Boolean>? = null,
    showReferenceNumberAlreadyUsedDialog: OneTimeEvent<Boolean>? = null,
    showEmailNotMatchingReferenceNumberDialog: OneTimeEvent<Boolean>? = null,
    showPhoneNumberNotMatchingReferenceNumberDialog: OneTimeEvent<Boolean>? = null,
) : MainUiState {
    return MainUiState(
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
        kycStatus = kycStatus,
        kycStatusColor = kycStatusColor,
        kycReferenceId = kycReferenceId,
        kycPersonId = kycPersonId,
        kycId = kycId,
        isGetUserDataInProgress = isGetUserDataInProgress,
        isKycInProgress = isKycInProgress,
        startKyc = startKyc,
        systemDialogUiState = systemDialogUiState,
        showReferenceNumberInvalidDialog = showReferenceNumberInvalidDialog,
        showReferenceNumberAlreadyUsedDialog = showReferenceNumberAlreadyUsedDialog,
        showEmailNotMatchingReferenceNumberDialog = showEmailNotMatchingReferenceNumberDialog,
        showPhoneNumberNotMatchingReferenceNumberDialog = showPhoneNumberNotMatchingReferenceNumberDialog,
    )
}

fun MainUiState.resetState(): MainUiState {
    return MainUiState()
}
