package com.example.cednik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.cednik.datastore.DataStoreConstants
import com.example.cednik.datastore.IDataStoreRepository
import com.example.cednik.navigation.Destination
import com.example.cednik.navigation.NavGraph
import com.example.cednik.ui.theme.CednikTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStore: IDataStoreRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        lifecycleScope.launch {
            setInitialLanguage()
        }
        setContent {
            CednikTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(startDestination = Destination.LoginScreen.route)
                }
            }
        }
    }

    private suspend fun setInitialLanguage() {
        val language = dataStore.getString(DataStoreConstants.LANGUAGE_KEY.name)
        val locale = if (!language.isNullOrEmpty()) {
            Locale(language)

        } else null
        if (locale != null) {
            updateResources(locale)
        }
    }

    private fun updateResources(locale: Locale) {
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}


