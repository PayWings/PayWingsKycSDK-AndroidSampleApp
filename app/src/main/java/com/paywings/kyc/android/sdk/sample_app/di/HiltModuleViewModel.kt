package com.paywings.kyc.android.sdk.sample_app.di

import com.paywings.kyc.android.sdk.sample_app.ui.nav.RouteNavigator
import com.paywings.kyc.android.sdk.sample_app.ui.nav.RouteNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class HiltModuleViewModel {
    @Provides
    @ViewModelScoped
    fun bindRouteNavigator(): RouteNavigator = RouteNavigatorImpl()
}