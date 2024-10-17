package com.pollub.awpfog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pollub.awpfog.R
import com.pollub.awpfog.ui.theme.AwpfogTheme

/**
 * Composable function that displays a top bar with user information and a logout button.
 *
 * The top bar contains an icon representing the user, a text displaying the guard's ID,
 * and a button to log out of the application.
 *
 * @param onLogout Lambda function to be executed when the logout button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(clientId:String, onLogout: () -> Unit) {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            titleContentColor = MaterialTheme.colorScheme.secondary
        ),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_account_circle_24),
                    contentDescription = "Call",
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "ID: $clientId",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        },
        actions = {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A5061)),
                shape = CircleShape,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Wyloguj", color = Color.White)
            }
        }
    )
}



@Preview(showBackground = true)
@Composable
fun TopAppBarPreview() {
    AwpfogTheme(dynamicColor = false) {
        TopBar("2137",{})
    }
}