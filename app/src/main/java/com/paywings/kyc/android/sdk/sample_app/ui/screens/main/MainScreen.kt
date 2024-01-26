package com.paywings.kyc.android.sdk.sample_app.ui.screens.main

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import com.paywings.kyc.android.sdk.sample_app.BuildConfig
import com.paywings.kyc.android.sdk.sample_app.R
import com.paywings.kyc.android.sdk.sample_app.dataStore
import com.paywings.kyc.android.sdk.sample_app.ui.nav.NavRoute
import com.paywings.kyc.android.sdk.sample_app.ui.screens.components.ProcessingButton
import com.paywings.kyc.android.sdk.sample_app.ui.screens.components.ScreenTitle
import com.paywings.kyc.android.sdk.sample_app.ui.screens.dialogs.kyc.KycEmailNotMatchingReferenceNumberDialog
import com.paywings.kyc.android.sdk.sample_app.ui.screens.dialogs.kyc.KycPhoneNumberNotMatchingReferenceNumberDialog
import com.paywings.kyc.android.sdk.sample_app.ui.screens.dialogs.kyc.KycReferenceNumberAlreadyUsedDialog
import com.paywings.kyc.android.sdk.sample_app.ui.screens.dialogs.kyc.KycReferenceNumberInvalidDialog
import com.paywings.kyc.android.sdk.sample_app.ui.screens.dialogs.system.ErrorDialog
import com.paywings.kyc.android.sdk.sample_app.ui.screens.dialogs.system.NoInternetConnectionDialog
import com.paywings.kyc.android.sdk.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.kyc.android.sdk.sample_app.ui.screens.settings.SettingsNav
import com.paywings.kyc.android.sdk.sample_app.ui.theme.scaffoldWindowInsets
import com.paywings.kyc.android.sdk.sample_app.util.consume
import com.vanpra.composematerialdialogs.rememberMaterialDialogState


/**
 * Every screen has a route, so that we don't have to add the route setup of all screens in one file.
 *
 * Inherits NavRoute, to be able to navigate away from this screen. All navigation logic is in there.
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
object MainNav : NavRoute<MainViewModel> {

    override val route = "main_screen"

    @Composable
    override fun viewModel(): MainViewModel = hiltViewModel()

    @Composable
    override fun Content(
        viewModel: MainViewModel,
        arguments: Bundle?,
        onCloseApp: () -> Unit
    ) = MainScreen(viewModel = viewModel, onCloseApp = onCloseApp)
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun MainScreen(viewModel: MainViewModel, onCloseApp: () -> Unit) {

    val activity = LocalContext.current as Activity
    val settings by LocalContext.current.dataStore.data.collectAsState(initial = null)

    LaunchedEffect(Unit) {
       viewModel.initializeKycSDK(activity = activity, kycApiUrl = settings?.get(stringPreferencesKey("sdkEndpointUrl"))?:"", kycApiUsername = settings?.get(stringPreferencesKey("sdkEndpointUsername"))?:"", kycApiPassword = settings?.get(stringPreferencesKey("sdkEndpointPassword"))?:"")
    }

    val kycReferenceNumberInvalidDialogState = rememberMaterialDialogState(initialValue = false)
    val kycReferenceNumberAlreadyUsedDialogState = rememberMaterialDialogState(initialValue = false)
    val kycEmailNotMatchingReferenceNumberDialogState = rememberMaterialDialogState(initialValue = false)
    val kycPhoneNumberNotMatchingReferenceNumberDialogState = rememberMaterialDialogState(initialValue = false)
    val noInternetConnectionDialogState = rememberMaterialDialogState(initialValue = false)
    val errorDialogState = rememberMaterialDialogState(initialValue = false)
    var errorMessage: String by remember { mutableStateOf("") }

    val showKycReferenceNumberAlreadyUsedDialog: () -> Unit = remember { { kycReferenceNumberAlreadyUsedDialogState.show() } }
    val showKycReferenceNumberInvalidDialog: () -> Unit = remember { { kycReferenceNumberInvalidDialogState.show() } }
    val showKycEmailNotMatchingReferenceNumberDialog: () -> Unit = remember { { kycEmailNotMatchingReferenceNumberDialogState.show() } }
    val showKycPhoneNumberNotMatchingReferenceNumberDialog: () -> Unit = remember { { kycPhoneNumberNotMatchingReferenceNumberDialogState.show() } }

    val uiState = viewModel.uiState

    val onNavigateToScreen: (route: String) -> Unit = remember {
        return@remember viewModel::navigateToRoute
    }

    val onSignOut: () -> Unit = remember {
        return@remember viewModel::signOutUser
    }

    val onStartKyc: (referenceNumber: String) -> Unit = remember {
        return@remember viewModel::startKyc
    }

    uiState.systemDialogUiState?.consume {
        when (it) {
            is SystemDialogUiState.ShowTooManySMSRequest -> Unit
            is SystemDialogUiState.ShowNoInternetConnection -> noInternetConnectionDialogState.show()
            is SystemDialogUiState.ShowError -> {
                errorMessage = it.errorMessage
                errorDialogState.show()
            }
        }
    }

    uiState.showReferenceNumberInvalidDialog?.consume {
        if (it) {
            showKycReferenceNumberInvalidDialog()
        }
    }

    uiState.showReferenceNumberAlreadyUsedDialog?.consume {
        if (it) {
            showKycReferenceNumberAlreadyUsedDialog()
        }
    }

    uiState.showEmailNotMatchingReferenceNumberDialog?.consume {
        if (it) {
            showKycEmailNotMatchingReferenceNumberDialog()
        }
    }

    uiState.showPhoneNumberNotMatchingReferenceNumberDialog?.consume {
        if (it) {
            showKycPhoneNumberNotMatchingReferenceNumberDialog()
        }
    }

    MainContent(
        userId = uiState.userId,
        firstName = uiState.firstName,
        lastName = uiState.lastName,
        emailAddress = uiState.email,
        phoneNumber = uiState.phoneNumber,
        kycStatus = uiState.kycStatus,
        kycStatusColor = uiState.kycStatusColor,
        kycReferenceId = uiState.kycReferenceId,
        kycPersonId = uiState.kycPersonId,
        kycId = uiState.kycId,
        isGetUserDataInProgress = uiState.isGetUserDataInProgress,
        isStartKyc = uiState.isKycInProgress,
        onSignOut = onSignOut,
        onStartKyc = { onStartKyc(settings?.get(stringPreferencesKey("referenceNumber"))?:"") },
        onShowSettings = { onNavigateToScreen(SettingsNav.route) }
    )

    KycReferenceNumberInvalidDialog(
        dialogState = kycReferenceNumberInvalidDialogState
    )

    KycReferenceNumberAlreadyUsedDialog(
        dialogState = kycReferenceNumberAlreadyUsedDialogState
    )

    KycEmailNotMatchingReferenceNumberDialog(
        dialogState = kycEmailNotMatchingReferenceNumberDialogState
    )

    KycPhoneNumberNotMatchingReferenceNumberDialog(
        dialogState = kycPhoneNumberNotMatchingReferenceNumberDialogState
    )


    NoInternetConnectionDialog(
        dialogState = noInternetConnectionDialogState,
        cancelButtonNameResId = R.string.button_exit,
        onRecheckInternetConnection = {
            noInternetConnectionDialogState.takeIf { it.showing }?.hide()
        },
        onCancel = {
            noInternetConnectionDialogState.takeIf { it.showing }?.hide()
            onCloseApp()
        }
    )

    ErrorDialog(
        dialogState = errorDialogState,
        detailedMessage = errorMessage,
        onCancel = {
            errorDialogState.takeIf { it.showing }?.hide()
            onCloseApp()
        }
    )

    LaunchedEffect(Unit) {
        viewModel.initialization()
    }

    BackHandler(enabled = true, onBack = onCloseApp)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    userId: String,
    firstName: String,
    lastName: String,
    emailAddress: String,
    phoneNumber: String,
    kycStatus: String?,
    kycStatusColor: Color,
    kycReferenceId: String?,
    kycPersonId: String?,
    kycId: String?,
    isGetUserDataInProgress: Boolean,
    isStartKyc: Boolean,
    onSignOut: () -> Unit,
    onStartKyc: () -> Unit,
    onShowSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(enabled = !isStartKyc, onClick = onShowSettings) {
                        Icon(
                            imageVector = Icons.Outlined.ManageAccounts,
                            contentDescription = Icons.Outlined.ManageAccounts.name
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(titleContentColor = MaterialTheme.colorScheme.primary, navigationIconContentColor = MaterialTheme.colorScheme.primary, actionIconContentColor = MaterialTheme.colorScheme.primary)
            )
        },
        contentWindowInsets = MaterialTheme.shapes.scaffoldWindowInsets
    ) { paddingValues ->

        Column(modifier = Modifier
            .padding(paddingValues = paddingValues)
            .verticalScroll(rememberScrollState())) {
            Text(modifier = Modifier.align(alignment = Alignment.End), text = stringResource(id = R.string.main_screen_app_version, BuildConfig.VERSION_NAME))
            Spacer(Modifier.height(24.dp))
            Card {
                Column(modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()) {
                    ScreenTitle(
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                        title = stringResource(id = R.string.main_screen_user_data_title)
                    )
                    Spacer(Modifier.height(24.dp))
                    if (isGetUserDataInProgress) {
                        Column(
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(strokeWidth = 2.dp)
                            Text(text = stringResource(R.string.main_screen_getting_user_data_progress_message))
                        }
                        Spacer(Modifier.height(24.dp))
                    } else {
                        Text(
                            text = stringResource(
                                id = R.string.main_screen_user_data_user_id,
                                userId
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                id = R.string.main_screen_user_data_first_name,
                                firstName
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                id = R.string.main_screen_user_data_last_name,
                                lastName
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                id = R.string.main_screen_user_data_email,
                                emailAddress
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                id = R.string.main_screen_user_data_phone_number,
                                phoneNumber
                            )
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            modifier = Modifier.align(Alignment.End),
                            onClick = { onSignOut() }) {
                            Text(text = stringResource(id = R.string.button_sign_out))
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            ProcessingButton(
                textResId = R.string.main_screen_start_kyc,
                isLoading = isStartKyc,
                isEnabled = true,
                onClick = onStartKyc
            )
            Spacer(Modifier.height(24.dp))
            if (!kycStatus.isNullOrEmpty()) {
                Card {
                    Column(modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth()) {
                        ScreenTitle(
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                            title = stringResource(id = R.string.main_screen_kyc_result_title)
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(text = stringResource(id = R.string.main_screen_kyc_result_status, kycStatus), color = kycStatusColor)
                        Spacer(Modifier.height(8.dp))
                        kycReferenceId?.let {
                            Text(text = stringResource(id = R.string.main_screen_kyc_result_reference_id, kycReferenceId))
                            Spacer(Modifier.height(8.dp))
                        }
                        kycPersonId?.let {
                            Text(text = stringResource(id = R.string.main_screen_kyc_result_person_id, kycPersonId))
                            Spacer(Modifier.height(8.dp))
                        }
                        kycId?.let {
                            Text(text = stringResource(id = R.string.main_screen_kyc_result_kyc_id, kycId))
                        }
                    }
                }
            }
        }
    }
}

