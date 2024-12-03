package com.pollub.awpfog

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModelProvider
import com.pollub.awpfog.data.SharedPreferencesManager
import com.pollub.awpfog.network.NetworkClient
import com.pollub.awpfog.ui.components.PermissionsInfoScreen
import com.pollub.awpfog.ui.main.AppUI
import com.pollub.awpfog.ui.theme.AwpfogTheme
import com.pollub.awpfog.utils.CheckPermissions
import com.pollub.awpfog.utils.EnableEdgeToEdgeAndSetBarTheme
import com.pollub.awpfog.utils.TokenManager
import com.pollub.awpfog.viewmodel.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//debug
const val address="10.0.2.2:8443"
//const val address="3.71.97.88:443"


const val BASE_URL = "https://$address/"
const val BASE_WEBSOCKET_URL = "wss://$address/guardSocket"


class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var viewModel: AppViewModel
    var isDarkMode = true

    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = TokenManager.TOKEN_EXPIRATION_THRESHOLD*500

    private val refreshTask = object : Runnable {
        override fun run() {
            CoroutineScope(Dispatchers.Default).launch{
                TokenManager.refreshTokenIfNeeded()
            }
            handler.postDelayed(this, refreshInterval)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPreferencesManager.init(this)
        viewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        NetworkClient.WebSocketManager.setViewModel(viewModel)

        handler.post(refreshTask)

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
                        AppUI(this, viewModel)
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
                    AppUI(this, viewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(refreshTask)
    }
}
