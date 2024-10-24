package com.pollub.awpfog.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pollub.awpfog.ui.theme.AwpfogTheme
import com.pollub.awpfog.utils.isPhoneValid
import com.pollub.awpfog.utils.isUsernameValid
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pollub.awpfog.data.models.Guard
import com.pollub.awpfog.data.models.GuardInfo
import com.pollub.awpfog.utils.isEmailValid
import com.pollub.awpfog.viewmodel.RegisterScreenViewModel

/**
 * Personal information registration screen that collects user details for account creation.
 *
 * @param modifier Modifier for this composable, allowing customization of style and layout.
 * @param navBack Function called to navigate back to the previous screen.
 * @param onSignUp Function called when the user completes the registration, with user details as parameters.
 */
@Composable
fun RegistrationScreenPersonalInformation(
    modifier: Modifier = Modifier,
    registerScreenViewModel: RegisterScreenViewModel,
    navBack: () -> Unit = {},
    onSignUp: (guard: GuardInfo) -> Unit = { _ -> }
) {


    var nameState by remember { mutableStateOf(registerScreenViewModel.name) }
    var surnameState by remember { mutableStateOf(registerScreenViewModel.surname) }
    var emailState by remember { mutableStateOf(registerScreenViewModel.surname) }
    var phoneState by remember { mutableStateOf(registerScreenViewModel.phone) }

    var isNameValid by remember { mutableStateOf(true) }
    var isSurnameValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPhoneValid by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            isError = !isNameValid,
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
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
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
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
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
                .padding(bottom = if (isPhoneValid) 16.dp else 4.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            isError = !isPhoneValid,
            maxLines = 1
        )
        if (!isPhoneValid) {
            Text(
                text = "Telefon powinien zawierać od 10 do 13 cyfr z opcjonalnym znakiem +",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }


        Button(
            onClick = {

                isNameValid = isUsernameValid(nameState)
                isSurnameValid = isUsernameValid(surnameState)
                isEmailValid = isEmailValid(emailState)
                isPhoneValid = isPhoneValid(phoneState)

                if (isNameValid && isSurnameValid && isEmailValid && isPhoneValid) {
                    onSignUp(
                        GuardInfo(
                            id = -1,
                            name = nameState,
                            surname = surnameState,
                            email = emailState,
                            phone = phoneState,
                            statusCode = Guard.GuardStatus.UNAVAILABLE.status,
                            location = "",
                            account_deleted = false,
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Zarejestruj się", color = MaterialTheme.colorScheme.onSecondary)
        }

        TextButton(
            onClick = {
                registerScreenViewModel.name = nameState
                registerScreenViewModel.surname = surnameState
                registerScreenViewModel.email = emailState
                registerScreenViewModel.phone = phoneState
                navBack()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Cofnij", color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegistrationScreenPersonalInformation() {
    AwpfogTheme(dynamicColor = false) {
        RegistrationScreenPersonalInformation(registerScreenViewModel = viewModel())
    }
}
