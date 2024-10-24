package com.pollub.awpfog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModelProvider
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.ui.components.PermissionsInfoScreen
import com.pollub.awpfog.ui.main.AppUI
import com.pollub.awpfog.ui.theme.AwpfogTheme
import com.pollub.awpfog.utils.CheckPermissions
import com.pollub.awpfog.utils.EnableEdgeToEdgeAndSetBarTheme
import com.pollub.awpfog.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var viewModel: AppViewModel
    var isDarkMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPreferencesManager.init(this)
        viewModel = ViewModelProvider(this).get(AppViewModel::class.java)

        requestPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            setContent {
                AwpfogTheme(dynamicColor = false) {
                    val lighterColor = MaterialTheme.colorScheme.secondary.toArgb()
                    val darkerColor = MaterialTheme.colorScheme.primary.toArgb()
                    EnableEdgeToEdgeAndSetBarTheme(lighterColor, darkerColor)
                    if (allGranted) {
                        AppUI(viewModel)
                    } else {
                        PermissionsInfoScreen()
                    }
                }
            }
        }

        setContent {
            AwpfogTheme(dynamicColor = false) {
                CheckPermissions(this, requestPermissionsLauncher) {
                    val lighterColor = MaterialTheme.colorScheme.secondary.toArgb()
                    val darkerColor = MaterialTheme.colorScheme.primary.toArgb()
                    EnableEdgeToEdgeAndSetBarTheme(lighterColor, darkerColor)
                    AppUI(viewModel)
                }
            }
        }
    }
}
