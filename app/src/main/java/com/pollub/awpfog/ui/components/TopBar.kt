package com.pollub.awpfog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pollub.awpfog.R
import com.pollub.awpfog.ui.theme.AwpfogTheme

/**
 * Composable function that displays a top app bar with user information,
 * an edit button, and a logout button.
 *
 * The top app bar includes an selected icon, which performs custom action
 * when clicked, a text displaying the user's ID,
 * and a button to log out of the application.
 *
 * @param clientName Name representing logged user.
 * @param iconId Id representing icon placed in left corner.
 * @param onIconClick Lambda function to be executed when the user icon is clicked.
 * @param onLogout Lambda function to be executed when the logout button is clicked,
 *                  logging the user out of the application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(clientName: String, iconId: Int, onIconClick: () -> Unit, onLogout: () -> Unit) {
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
                    painter = painterResource(id = iconId),
                    contentDescription = "Call",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            onIconClick()
                        }
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = clientName,
                    color = Color.White,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
        TopBar("Januszesxxxxx noWakowassdisad", R.drawable.baseline_account_circle_24, {}, {})
    }
}