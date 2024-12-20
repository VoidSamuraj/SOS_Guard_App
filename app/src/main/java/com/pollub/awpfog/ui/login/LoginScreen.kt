package com.pollub.awpfog.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pollub.awpfog.R
import com.pollub.awpfog.ui.theme.AwpfogTheme
import com.pollub.awpfog.utils.TestTags


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
    onLoginPress: (login: String, password: String) -> Unit = { _, _ -> },
    navToRegister: () -> Unit = {},
    onRemindPasswordPress: () -> Unit = {}
) {

    val loginState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

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
            text = "Aby się zalogować wprowadź swój login i hasło",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        OutlinedTextField(
            value = loginState.value,
            onValueChange = {
                if (it.length <= 20) {
                    loginState.value = it
                }
            },
            label = { Text("Login*") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag(TestTags.LOGIN_SCREEN_LOGIN_INPUT),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            maxLines = 1
        )

        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Hasło*") },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .testTag(TestTags.LOGIN_SCREEN_PASSWORD_INPUT),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                val image =
                    if (passwordVisible.value) R.drawable.outline_visibility_24 else R.drawable.outline_visibility_off_24
                Icon(
                    painter = painterResource(image),
                    contentDescription = if (passwordVisible.value) "Ukryj hasło" else "Pokaż hasło",
                    modifier = Modifier.clickable { passwordVisible.value = !passwordVisible.value }
                )
            },
            maxLines = 1
        )

        Button(
            onClick = { onLoginPress(loginState.value, passwordState.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .testTag(TestTags.LOGIN_SCREEN_LOGIN_BUTTON),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Zaloguj", color = MaterialTheme.colorScheme.onSecondary)
        }

        Button(
            onClick = onRemindPasswordPress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag(TestTags.LOGIN_SCREEN_FORGOT_PASSWORD_BUTTON),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp)
                .testTag(TestTags.LOGIN_SCREEN_REGISTER_BUTTON),
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
