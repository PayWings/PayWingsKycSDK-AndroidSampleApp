package com.paywings.kyc.android.sdk.sample_app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import com.paywings.kyc.android.sdk.sample_app.network.NetworkState
import com.paywings.kyc.android.sdk.sample_app.ui.nav.graph.StartUpNavGraph
import com.paywings.kyc.android.sdk.sample_app.ui.theme.PayWingsKYCAndroidSDKSampleAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "KycSettings")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkState: NetworkState

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        networkState.start()
        setActivityContent()
    }

    override fun onDestroy() {
        super.onDestroy()
        networkState.destroy()
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
    private fun setActivityContent() {
        setContent {
            val navController = rememberNavController()
            val onCloseApp: () -> Unit = remember {
                return@remember { closeApp() }
            }

            PayWingsKYCAndroidSDKSampleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StartUpNavGraph(
                        navController,
                        onCloseApp = onCloseApp
                    )
                }
            }
        }
    }

    private fun closeApp() {
        finishAndRemoveTask()
    }
}

