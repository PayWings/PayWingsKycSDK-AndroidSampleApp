package com.paywings.kyc.android.sdk.sample_app.ui.screens.initialization

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import com.paywings.kyc.android.sdk.sample_app.R
import com.paywings.kyc.android.sdk.sample_app.dataStore
import com.paywings.kyc.android.sdk.sample_app.ui.nav.NavRoute
import com.paywings.kyc.android.sdk.sample_app.util.Constants


/**
 * Every screen has a route, so that we don't have to add the route setup of all screens in one file.
 *
 * Inherits NavRoute, to be able to navigate away from this screen. All navigation logic is in there.
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
object InitializationNav : NavRoute<InitializationViewModel> {

    override val route = "initialization_screen"

    @Composable
    override fun viewModel(): InitializationViewModel = hiltViewModel()

    @Composable
    override fun Content(
        viewModel: InitializationViewModel,
        arguments: Bundle?,
        onCloseApp: () -> Unit
    ) = InitializationScreen(viewModel = viewModel, onCloseApp = onCloseApp)
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun InitializationScreen(viewModel: InitializationViewModel, onCloseApp: () -> Unit) {

    val settings by LocalContext.current.dataStore.data.collectAsState(initial = null)

    val dataStore = LocalContext.current.dataStore

    LaunchedEffect(settings) {
        settings?.let {
        }
    }

    InitializationContent()

    LaunchedEffect(settings) {
        settings?.let { settingPreferences ->
            val oauthApiKey = settingPreferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_OAUTH_API_KEY)]
            val oauthDomain = settingPreferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_OAUTH_DOMAIN)]

            if (settingPreferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_SDK_ENDPOINT_URL)].isNullOrBlank()) {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_SDK_ENDPOINT_URL)] =
                        "https://kyc-test.paywings.io/mobile/"
                }
            }
            if (settingPreferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_SDK_ENDPOINT_USERNAME)].isNullOrBlank()) {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_SDK_ENDPOINT_USERNAME)] =
                        "ubcUser"
                }
            }
            if (settingPreferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_SDK_ENDPOINT_PASSWORD)].isNullOrBlank()) {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_SDK_ENDPOINT_PASSWORD)] =
                        "ubcPass"
                }
            }
            if (settingPreferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_REFERENCE_NUMBER)].isNullOrBlank()) {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_REFERENCE_NUMBER)] =
                        ""
                }
            }

            if (oauthApiKey.isNullOrBlank()) {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_OAUTH_API_KEY)] =
                        "fd724674-415d-42d7-b56c-fe3237c956d9"
                }
            }
            if (oauthDomain.isNullOrBlank()) {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey(Constants.SETTINGS_PREFERENCE_KEY_OAUTH_DOMAIN)] =
                        "paywings.io"
                }
            }

            if (!oauthApiKey.isNullOrBlank() && !oauthDomain.isNullOrBlank()) {
                viewModel.initialization(
                    oauthApiKey = oauthApiKey,
                    oauthDomain = oauthDomain
                )
            }
        }
    }

    BackHandler(enabled = true, onBack = { onCloseApp() })
}

@Composable
fun InitializationContent() {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (logoWithText, progressIndicatorWithDescription) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            modifier = Modifier
                .requiredSize(200.dp)
                .constrainAs(logoWithText) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(progressIndicatorWithDescription) {
                    top.linkTo(logoWithText.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            CircularProgressIndicator(strokeWidth = 2.dp)
            Text(text = stringResource(R.string.initialization_screen_progress_message))
        }
    }
}