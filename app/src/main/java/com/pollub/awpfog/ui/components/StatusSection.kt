package com.pollub.awpfog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pollub.awpfog.data.models.Guard
import com.pollub.awpfog.ui.theme.AwpfogTheme

/**
 * Composable function that displays a status section with a title and a status indicator.
 *
 * @param statusTitle The title of the status (e.g., "Połączenie", "Zgłoszenie").
 * @param statusValue Boolean value representing the status: `true` for active and `false` for inactive.
 *
 * This section includes:
 * - A title and a description ("Aktywny" or "Nieaktywny") based on the status value.
 * - A circular status indicator: green if active, red if inactive.
 */
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

/**
 * Composable function that displays a status section with a title and a status indicator.
 *
 * @param statusTitle The title of the status (e.g., "Połączenie", "Zgłoszenie").
 * @param status Int representing [Guard.GuardStatus] status.
 *
 * This section includes:
 * - A title and a description ("Aktywny", "Nieaktywny", "Interwencja", "Nie odpowiada") based on the status value.
 * - A circular status indicator: green if active, light gray if inactive, red on intervention and purple when not responding.
 */
@Composable
fun StatusSection(statusTitle: String, status: Int) {

    val color = when(status){
        Guard.GuardStatus.AVAILABLE.status -> Color.Green
        Guard.GuardStatus.UNAVAILABLE.status -> Color.LightGray
        Guard.GuardStatus.INTERVENTION.status -> Color.Red
        Guard.GuardStatus.NOT_RESPONDING.status -> Color(0xff7000ff)
        else -> Color.LightGray
    }
    val text = when(status){
        Guard.GuardStatus.AVAILABLE.status -> "Aktywny"
        Guard.GuardStatus.UNAVAILABLE.status -> "Nieaktywny"
        Guard.GuardStatus.INTERVENTION.status -> "Interwencja"
        Guard.GuardStatus.NOT_RESPONDING.status -> "Nie odpowiada"
        else -> ""
    }

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
                text = text,
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
                    .background(color)
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStatusSection() {
    AwpfogTheme(dynamicColor = false) {
        StatusSection("Status połączenia", true)
    }
}