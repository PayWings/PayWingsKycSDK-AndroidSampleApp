package com.paywings.kyc.android.sdk.sample_app.ui.screens.initialization

import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sdk.data.enums.EnvironmentType
import com.paywings.kyc.android.sdk.sample_app.ui.nav.RouteNavigator
import com.paywings.kyc.android.sdk.sample_app.ui.nav.graph.MAIN_ROUTE
import com.paywings.kyc.android.sdk.sample_app.ui.nav.graph.OAUTH_ROUTE
import com.paywings.kyc.android.sdk.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.kyc.android.sdk.sample_app.util.asOneTimeEvent
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.OAuthInitializationCallback
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@HiltViewModel
class InitializationViewModel @Inject constructor(
    application: Application,
    private val routeNavigator: RouteNavigator
) : AndroidViewModel(application), RouteNavigator by routeNavigator {

    private val context
        get() = getApplication<Application>()

    var uiState: InitializationUiState by mutableStateOf(value = InitializationUiState())

    private var oauthApiKey: String = ""
    private var oauthDomain: String = ""

    var oauthInitializationRetryCount: Int = 0

    private val oauthInitializationCallback = object: OAuthInitializationCallback {
        override fun onFailure(error: OAuthErrorCode, errorMessage: String?) {
            if (oauthInitializationRetryCount < 2) {
                oauthInitializationRetryCount++
                oauthInitialization()
            } else {
                uiState = uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowError(errorMessage = errorMessage?:"").asOneTimeEvent())
            }
        }

        override fun onSuccess() {
            checkUserSignIn()
        }
    }

    fun initialization(oauthApiKey: String, oauthDomain: String) {
        this.oauthApiKey = oauthApiKey
        this.oauthDomain = oauthDomain
        oauthInitialization()
    }

    private fun checkUserSignIn() {
        viewModelScope.launch {
            when (PayWingsOAuthClient.instance.isUserSignIn()) {
                true -> navigateToRoute(MAIN_ROUTE)
                false -> navigateToRoute(OAUTH_ROUTE)
            }
        }
    }

    private fun oauthInitialization() {
        viewModelScope.launch {
            PayWingsOAuthClient.init(
                context = context,
                environmentType = EnvironmentType.TEST,
                apiKey = oauthApiKey,
                domain = oauthDomain,
                appPlatformID = "C0BD1332-D3A3-4FE2-9C2F-49C79BAE2946",
                recaptchaKey = "6LfsCKIoAAAAACh_ycSZx6wgAngWBEi9NHrU541j",
                callback = oauthInitializationCallback
            )
        }
    }
}

