package com.pollub.awpfog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pollub.awpfog.R
import com.pollub.awpfog.data.models.Guard
import com.pollub.awpfog.ui.theme.AwpfogTheme
import com.pollub.awpfog.utils.isEmailValid
import com.pollub.awpfog.utils.isLoginValid
import com.pollub.awpfog.utils.isPasswordValid
import com.pollub.awpfog.utils.isPhoneValid
import com.pollub.awpfog.utils.isUsernameValid
import kotlin.text.isEmpty

/**
 * Composable function for editing user data including fields for login, password, first name, last name,
 * email, phone number, and PESEL.
 *
 * The screen includes fields for user data input and a button to save the changes.
 *
 * @param modifier Optional [Modifier] to be applied to the root element.
 * @param guard Optional [Guard] object used to pre-populate the form fields with existing data.
 * @param onSavePress Lambda function triggered when the save button is pressed.
 * It captures the updated user data fields such as login, password, newPassword, firstName, lastName, email, phone, and PESEL.
 * The function allows processing and saving these values.
*/
@Composable
fun EditGuardDataScreen(
    modifier: Modifier = Modifier,
    guard: Guard? = null,
    onSavePress: (login: String, password: String, newPassword: String, firstName: String, lastName: String, email: String, phone: String) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    var loginState by remember {
        mutableStateOf(
            guard?.login ?: ""
        )
    }
    var passwordState by remember { mutableStateOf("") }
    var newPasswordState by remember { mutableStateOf("") }
    var nameState by remember { mutableStateOf(guard?.name ?: "") }
    var surnameState by remember {
        mutableStateOf(
            guard?.surname ?: ""
        )
    }
    var emailState by remember {
        mutableStateOf(
            guard?.email ?: ""
        )
    }
    var phoneState by remember {
        mutableStateOf(
            guard?.phone ?: ""
        )
    }

    var isLoginValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var isNewPasswordValid by remember { mutableStateOf(true) }
    var isNameValid by remember { mutableStateOf(true) }
    var isSurnameValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPhoneValid by remember { mutableStateOf(true) }

    var passwordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }

    var scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Edytuj dane użytkownika",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .weight(0.1f),
            color = MaterialTheme.colorScheme.onPrimary
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Companion.Gray)
                .padding(vertical = 1.dp)
                .background(MaterialTheme.colorScheme.background)
                .weight(0.8f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                value = loginState,
                onValueChange = {
                    if (it.length <= 20) {
                        loginState = it
                        isLoginValid = isLoginValid(it)
                    }
                },
                label = { Text("Login*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isLoginValid) 16.dp else 4.dp),
                isError = !isLoginValid,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Companion.Text,
                    imeAction = ImeAction.Companion.Next
                ),
                maxLines = 1
            )
            if (!isLoginValid) {
                Text(
                    text = "Login jest wymagany i powinien zawierać od 3 do 20 znaków",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            OutlinedTextField(
                value = passwordState,
                onValueChange = {
                    passwordState = it
                    isPasswordValid = passwordState.length > 0
                },
                label = { Text("Hasło*") },
                visualTransformation = if (passwordVisible) VisualTransformation.Companion.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isPasswordValid) 16.dp else 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Companion.Password,
                    imeAction = ImeAction.Companion.Next
                ),
                isError = !isPasswordValid,
                trailingIcon = {
                    val image =
                        if (passwordVisible) R.drawable.outline_visibility_24 else R.drawable.outline_visibility_off_24
                    Icon(
                        painter = painterResource(image),
                        contentDescription = if (passwordVisible) "Ukryj hasło" else "Pokaż hasło",
                        modifier = Modifier.clickable {
                            passwordVisible = !passwordVisible
                        }
                    )
                },
                maxLines = 1
            )
            if (!isPasswordValid) {
                Text(
                    text = "Należy podać obecne hasło",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            OutlinedTextField(
                value = newPasswordState,
                onValueChange = {
                    newPasswordState = it
                    isNewPasswordValid =
                        newPasswordState.isEmpty() || isPasswordValid(it)
                },
                label = { Text("Nowe Hasło") },
                visualTransformation = if (newPasswordVisible) VisualTransformation.Companion.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isNewPasswordValid) 16.dp else 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Companion.Password,
                    imeAction = ImeAction.Companion.Next
                ),
                isError = !isNewPasswordValid,
                trailingIcon = {
                    val image =
                        if (newPasswordVisible) R.drawable.outline_visibility_24 else R.drawable.outline_visibility_off_24
                    Icon(
                        painter = painterResource(image),
                        contentDescription = if (newPasswordVisible) "Ukryj hasło" else "Pokaż hasło",
                        modifier = Modifier.clickable {
                            newPasswordVisible = !newPasswordVisible
                        }
                    )
                },
                maxLines = 1
            )
            if (!isNewPasswordValid) {
                Text(
                    text = "Hasło powinno mieć minimum 8 znaków, zawierać małą i dużą literę, cyfrę i znak specjalny.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            OutlinedTextField(
                value = nameState,
                onValueChange = {
                    if (it.length <= 40) {
                        nameState = it
                        isNameValid = isUsernameValid(it)
                    }
                },
                label = { Text("Imię*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isNameValid) 16.dp else 4.dp),
                isError = !isNameValid,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Companion.Text,
                    imeAction = ImeAction.Companion.Next
                ),
                maxLines = 1
            )
            if (!isNameValid) {
                Text(
                    text = "Imie powinno zawierać od 3 do 40 znaków.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = surnameState,
                onValueChange = {
                    if (it.length <= 40) {
                        surnameState = it
                        isSurnameValid = isUsernameValid(it)
                    }
                },
                label = { Text("Nazwisko*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isSurnameValid) 16.dp else 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Companion.Text,
                    imeAction = ImeAction.Companion.Next
                ),
                isError = !isSurnameValid,
                maxLines = 1
            )
            if (!isSurnameValid) {
                Text(
                    text = "Nazwisko powinno zawierać od 3 do 40 znaków.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = emailState,
                onValueChange = {
                    emailState = it
                    isEmailValid = isEmailValid(it)
                },
                label = { Text("Email*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isEmailValid) 16.dp else 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Companion.Email,
                    imeAction = ImeAction.Companion.Next
                ),
                isError = !isEmailValid,
                maxLines = 1
            )
            if (!isEmailValid) {
                Text(
                    text = "Proszę podać poprawny email",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = phoneState,
                onValueChange = {
                    if (it.length <= 13) {
                        phoneState = it
                        isPhoneValid = isPhoneValid(it)
                    }
                },
                label = { Text("Nr telefonu*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Companion.Phone,
                    imeAction = ImeAction.Companion.Next
                ),
                isError = !isPhoneValid
            )
            if (!isPhoneValid) {
                Text(
                    text = "Telefon powinien zawierać od 10 do 13 cyfr z opcjonalnym znakiem +",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

        }
        Button(
            onClick = {

                isLoginValid = isLoginValid(loginState)
                isPasswordValid = passwordState.length > 0
                isNewPasswordValid =
                    newPasswordState.isEmpty() || isPasswordValid(
                        newPasswordState
                    )
                isNameValid = isUsernameValid(nameState)
                isSurnameValid = isUsernameValid(surnameState)
                isEmailValid = isEmailValid(emailState)
                isPhoneValid = isPhoneValid(phoneState)

                if (isLoginValid && isPasswordValid && isNewPasswordValid && isNameValid && isSurnameValid && isEmailValid && isPhoneValid)
                    onSavePress(
                        loginState,
                        passwordState,
                        newPasswordState,
                        nameState,
                        surnameState,
                        emailState,
                        phoneState
                    )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .weight(0.1f),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Zapisz zmiany", color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditGuardDataScreen() {
    AwpfogTheme(dynamicColor = false) {
        EditGuardDataScreen()
    }
}
