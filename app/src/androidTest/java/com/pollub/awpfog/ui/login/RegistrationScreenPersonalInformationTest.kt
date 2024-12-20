package com.pollub.awpfog.ui.login

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pollub.awpfog.data.models.GuardInfo
import com.pollub.awpfog.utils.TestTags
import com.pollub.awpfog.viewmodel.RegisterScreenViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationScreenPersonalInformationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var registerScreenViewModel: RegisterScreenViewModel

    val login = "Login123"
    val password = "Secure@123"

    @Test
    fun assert_RegistrationScreenPersonalInformation_No_Input(){
        var registerPressed = false
        var navBackPressed = false
        registerScreenViewModel=RegisterScreenViewModel()
        registerScreenViewModel.login=login
        registerScreenViewModel.password=password
        composeTestRule.setContent {
            RegistrationScreenPersonalInformation(
                registerScreenViewModel = registerScreenViewModel,
                navBack = {
                    navBackPressed=true
                },
                onSignUp = {
                    registerPressed=true
                })
        }

        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_REGISTER_BUTTON).performClick()

        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_NAME_INPUT_ERROR).assertExists()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_SURNAME_INPUT_ERROR).assertExists()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_EMAIL_INPUT_ERROR).assertExists()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_PHONE_INPUT_ERROR).assertExists()

        assert(!navBackPressed)
        assert(!registerPressed)
    }

    @Test
    fun assert_RegistrationScreenPersonalInformation_Pass(){
        var registerPressed = false
        var navBackPressed = false
        var returnedGuardInfo: GuardInfo? = null
        registerScreenViewModel=RegisterScreenViewModel()
        registerScreenViewModel.login=login
        registerScreenViewModel.password=password
        composeTestRule.setContent {
            RegistrationScreenPersonalInformation(
                registerScreenViewModel = registerScreenViewModel,
                navBack = {
                    navBackPressed=true
                },
                onSignUp = {
                    registerPressed=true
                    returnedGuardInfo=it
                })
        }

        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_NAME_INPUT).performTextInput("ValidUserName123")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_SURNAME_INPUT).performTextInput("ValidUserSurname123")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_EMAIL_INPUT).performTextInput("example.user@domain.com")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_PHONE_INPUT).performTextInput("+12345678901")
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_REGISTER_BUTTON).performClick()

        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_NAME_INPUT_ERROR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_SURNAME_INPUT_ERROR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_EMAIL_INPUT_ERROR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_PHONE_INPUT_ERROR).assertDoesNotExist()
        assert(!navBackPressed)
        assert(registerPressed)
        assert(returnedGuardInfo != null)
        assert(returnedGuardInfo?.name =="ValidUserName123")
        assert(returnedGuardInfo?.surname =="ValidUserSurname123")
        assert(returnedGuardInfo?.email =="example.user@domain.com")
        assert(returnedGuardInfo?.phone =="+12345678901")

        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PERSONAL_INFORMATION_CANCEL_BUTTON).performClick()
        assert(navBackPressed)
        assert(registerPressed)


    }
}