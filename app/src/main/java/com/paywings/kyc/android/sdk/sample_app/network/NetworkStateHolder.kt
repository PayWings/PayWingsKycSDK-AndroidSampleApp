package com.paywings.kyc.android.sdk.sample_app.network

import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities

interface NetworkStateHolder {
    val isConnected: Boolean
    val network: Network?
    val networkCapabilities: NetworkCapabilities?
    val linkProperties: LinkProperties?
}