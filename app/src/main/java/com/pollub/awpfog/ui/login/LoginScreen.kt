package com.pollub.awpfoc.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pollub.awpfog.ui.theme.AwpfogTheme


/**
 * Composable function for the login screen where users can enter their email and password to log in.
 *
 * The screen includes fields for email and password input, buttons for logging in, reminding the password,
 * and navigating to the registration screen.
 *
 * @param modifier Optional [Modifier] to be applied to the root element.
 * @param onLoginPress Lambda function to be executed when the login button is pressed.
 * @param navToRegister Lambda function to be executed when the navigation to the registration screen is triggered.
 * @param onRemindPasswordPress Lambda function to be executed when the remind password button is pressed.
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginPress: ()->Unit={},
    navToRegister: ()->Unit={},
    onRemindPasswordPress: ()->Unit={}
) {

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Zaloguj",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = "Aby się zalogować wprowadź swój adres e-mail i hasło",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email*") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Hasło*") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Button(
            onClick = onLoginPress ,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Zaloguj", color = MaterialTheme.colorScheme.onSecondary)
        }

        Button(
            onClick = onRemindPasswordPress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Zapomniałem hasła", color = MaterialTheme.colorScheme.onSecondary)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Jeżeli nie posiadasz konta zarejestruj się",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        Button(
            onClick = navToRegister,
            modifier = Modifier.fillMaxWidth().padding(bottom = 100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Załóż nowe konto", color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    AwpfogTheme(dynamicColor = false) {
        LoginScreen()
    }
}
