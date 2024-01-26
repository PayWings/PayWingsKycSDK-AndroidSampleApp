package com.paywings.kyc.android.sdk.sample_app.ui.screens.main

import android.app.Activity
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paywings.kyc.android.sdk.data.enums.KycErrorCode
import com.paywings.kyc.android.sdk.data.model.PayWingsUserCredentials
import com.paywings.kyc.android.sdk.data.model.PayWingsWhiteLabelCredentials
import com.paywings.kyc.android.sdk.initializer.PayWingsKycClient
import com.paywings.kyc.android.sdk.sample_app.ui.nav.RouteNavigator
import com.paywings.kyc.android.sdk.sample_app.ui.nav.graph.OAUTH_ROUTE
import com.paywings.kyc.android.sdk.sample_app.util.asOneTimeEvent
import com.paywings.oauth.android.sdk.data.enums.HttpRequestMethod
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.GetUserDataCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator
): ViewModel(), RouteNavigator by routeNavigator  {

    var uiState: MainUiState by mutableStateOf(value = MainUiState())
    private lateinit var payWingsKycClient: PayWingsKycClient

    fun signOutUser() {
        viewModelScope.launch {
            PayWingsOAuthClient.instance.signOutUser()
            navigateToRoute(OAUTH_ROUTE)
        }
    }

    fun initialization() {
        getUserData()
    }

    fun initializeKycSDK(activity: Activity, kycApiUrl: String,  kycApiUsername: String, kycApiPassword: String) {
        payWingsKycClient = PayWingsKycClient(
            activity = activity,
            whiteLabelCredentials = PayWingsWhiteLabelCredentials(
                endpointUrl = kycApiUrl,
                username = kycApiUsername,
                password = kycApiPassword
            ),
            userCredentials = userCredentials,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    private val userCredentials: (String, String) -> PayWingsUserCredentials = { methodUrl, httpRequestMethod ->
        runBlocking {
            val authorizationData = PayWingsOAuthClient.instance.getNewAuthorizationData(methodUrl = methodUrl, httpRequestMethod = HttpRequestMethod.getByName(httpRequestMethod))
            when {
                !authorizationData.dpop.isNullOrBlank() && authorizationData.accessTokenData != null -> return@runBlocking PayWingsUserCredentials(dpop = authorizationData.dpop!!, accessToken = authorizationData.accessTokenData?.accessToken?:"")
                authorizationData.errorData != null -> {
                    Log.d("KycSampleApp", "Retrieving user credentials return error with errorCode: ${authorizationData.errorData?.error} and description: ${authorizationData.errorData?.errorMessage}")
                    return@runBlocking PayWingsUserCredentials(dpop = "", accessToken = "")
                }
                else -> {
                    Log.d("KycSampleApp", "User needs to sign in.")
                    navigateToRoute(OAUTH_ROUTE)
                    return@runBlocking PayWingsUserCredentials(dpop = "", accessToken = "")
                }
            }
        }
    }

    private val onSuccess: (String, String) -> Unit = { kycId, personId ->
        uiState = uiState.updateState(
            kycStatus = "Successful",
            kycStatusColor = Color.Green,
            kycId = kycId,
            kycPersonId = personId,
            kycReferenceId = null,
        )
    }

    private val onError: (String?, String?, String?, KycErrorCode, String?) -> Unit = { kycId, personId, kycReferenceId, error, errorMessage ->
        Log.d("KycSampleApp", "KYC process resulted in error with errorCode: ${error.id} and description: ${error.description} (${errorMessage?:""})")
        when(error) {
            KycErrorCode.ABORTED_BY_USER -> uiState = uiState.updateState(
                kycStatus = "Canceled by user",
                kycStatusColor = Color.Black,
                kycId = kycId?:"",
                kycPersonId = personId?:"",
                kycReferenceId = kycReferenceId?:"",
            )
            KycErrorCode.REFERENCE_NUMBER_NOT_EXIST, KycErrorCode.REFERENCE_NUMBER_TOO_LONG ->  uiState = uiState.updateState(
                kycStatus = "Failure",
                kycStatusColor = Color.Red,
                kycId = kycId?:"",
                kycPersonId = personId?:"",
                kycReferenceId = kycReferenceId?:"",
                showReferenceNumberInvalidDialog = true.asOneTimeEvent()
            )
            KycErrorCode.REFERENCE_NUMBER_ALREADY_USED -> uiState = uiState.updateState(
                kycStatus = "Failure",
                kycStatusColor = Color.Red,
                kycId = kycId?:"",
                kycPersonId = personId?:"",
                kycReferenceId = kycReferenceId?:"",
                showReferenceNumberAlreadyUsedDialog = true.asOneTimeEvent()
            )
            KycErrorCode.MISMATCH_EMAIL -> uiState = uiState.updateState(
                kycStatus = "Failure",
                kycStatusColor = Color.Red,
                kycId = kycId?:"",
                kycPersonId = personId?:"",
                kycReferenceId = kycReferenceId?:"",
                showEmailNotMatchingReferenceNumberDialog = true.asOneTimeEvent()
            )
            KycErrorCode.MISMATCH_MOBILE_NUMBER -> uiState = uiState.updateState(
                kycStatus = "Failure",
                kycStatusColor = Color.Red,
                kycId = kycId?:"",
                kycPersonId = personId?:"",
                kycReferenceId = kycReferenceId?:"",
                showPhoneNumberNotMatchingReferenceNumberDialog = true.asOneTimeEvent()
            )
            else -> uiState = uiState.updateState(
                kycStatus = "Failed with status code: ${error.id} and description: ${error.description} (${errorMessage?:""})",
                kycStatusColor = Color.Red,
                kycId = kycId?:"",
                kycPersonId = personId?:"",
                kycReferenceId = kycReferenceId?:""
            )
        }
    }

    fun startKyc(referenceNumber: String) {
        uiState = uiState.updateState(isKycInProgress = true)
        viewModelScope.launch {
            payWingsKycClient.startKyc(referenceNumber = referenceNumber)
        }
    }

    private fun getUserData() {
        uiState = uiState.updateState(isGetUserDataInProgress = true)
        viewModelScope.launch {
            PayWingsOAuthClient.instance.getUserData(
                callback = getUserDataCallback
            )
        }
    }

    private val getUserDataCallback = object : GetUserDataCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            Log.d("KycSampleApp", "Error getting user data. ErrorCode: '${error.id}' and errorDescription: '${error.description} ($errorMessage)'")
        }

        override fun onUserData(
            userId: String,
            firstName: String?,
            lastName: String?,
            email: String?,
            emailConfirmed: Boolean,
            phoneNumber: String?
        ) {
            uiState = uiState.updateState(userId = userId, firstName = firstName?:"", lastName = lastName?:"", email = email?:"", phoneNumber = phoneNumber?:"")
        }

        override fun onUserSignInRequired() {
            navigateToRoute(OAUTH_ROUTE)
        }
    }




}