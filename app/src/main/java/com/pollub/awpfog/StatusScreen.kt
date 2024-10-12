package com.pollub.awpfog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip

@Composable
fun StatusScreen() {
    val currentLocation = "Krańcowa 1, Lublin, 20-001"
    val connectionStatus by remember { mutableStateOf(true) }
    val gpsStatus by remember { mutableStateOf(true) }
    var patrolStatus by remember { mutableStateOf(true) }


    fun onStatusChange() {
        patrolStatus = !patrolStatus
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            StatusTopAppBar("2137", {})
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            LocationInfo(currentLocation)

            // Status sections
            StatusSection("Status połączenia z bazą", connectionStatus)
            StatusSection("Status udostępniania pozycji GPS", gpsStatus)
            StatusSection("Status patrolu", patrolStatus)

            // Toggle for patrol status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Zmień status patrolu", modifier = Modifier.weight(1f))
                Switch(checked = patrolStatus, onCheckedChange = { onStatusChange() })
            }

            InterventionSection(currentLocation, {}, {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusTopAppBar(
    userId: String,
    onLogoutClick: () -> Unit
) {
    TopAppBar(
        title = {
        },
        navigationIcon = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_account_circle_24),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "ID: $userId", color = Color.White, fontSize = 16.sp)
                    Text(
                        text = "Zalogowany",
                        color = Color.LightGray, fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onLogoutClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "Wyloguj", color = Color.White)
                }
            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun LocationInfo(currentLocation: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Aktualna lokalizacja", fontSize = 18.sp)
        Text(text = currentLocation, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun StatusSection(statusTitle: String, statusValue: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(modifier = Modifier.wrapContentHeight().weight(1f)) {
            Text(text = statusTitle, fontSize = 18.sp)
            Text(
                text = if (statusValue) "Aktywny" else "Nieaktywny",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Gray)

        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (statusValue) Color.Green else Color.Red)
                    .align(Alignment.Center)
            )
        }
    }

}

@Composable
fun InterventionSection(
    location: String,
    onConfirm: () -> Unit,
    onReject: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = "Interwencja", fontSize = 18.sp, color = Color.Red)
        Text(text = location, color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280)) // Example color
        ) {
            Text(text = "Potwierdź interwencję", color = Color.White)
        }

        Button(
            onClick = onReject,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)) // Example color
        ) {
            Text(text = "Odrzuć interwencję", color = Color.White)
        }
    }
}
