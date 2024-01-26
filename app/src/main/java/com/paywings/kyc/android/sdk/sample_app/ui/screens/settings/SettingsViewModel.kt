package com.paywings.kyc.android.sdk.sample_app.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.paywings.kyc.android.sdk.sample_app.network.NetworkState
import com.paywings.kyc.android.sdk.sample_app.ui.nav.RouteNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator
) : ViewModel(), RouteNavigator by routeNavigator