package com.paywings.kyc.android.sdk.sample_app.di

import android.app.Application
import com.paywings.oauth.android.sdk.data.enums.EnvironmentType
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application()