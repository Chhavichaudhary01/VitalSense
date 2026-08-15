package com.vitalsense.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitalsense.app.core.data.repository.VitalSenseRepository
import com.vitalsense.app.core.state.AppStateHolder
import com.vitalsense.app.core.ui.theme.VitalSenseTheme
import com.vitalsense.app.core.ui.theme.WarmCreamBackground
import com.vitalsense.app.feature.navigation.VitalSenseNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appStateHolder: AppStateHolder

    @Inject
    lateinit var repository: VitalSenseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val currentLanguage by appStateHolder.currentLanguage.collectAsStateWithLifecycle()

            VitalSenseTheme(language = currentLanguage) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WarmCreamBackground
                ) {
                    VitalSenseNavGraph(
                        appStateHolder = appStateHolder,
                        repository = repository
                    )
                }
            }
        }
    }
}
