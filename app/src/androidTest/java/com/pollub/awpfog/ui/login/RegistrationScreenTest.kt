package com.pollub.awpfog.ui.login

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pollub.awpfog.utils.TestTags
import com.pollub.awpfog.viewmodel.RegisterScreenViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    val login = "Login123"
    val password = "Secure@123"

    @Test
    fun assert_Registration_Screen_No_Input() {
        var navToLoginPressed = false
        var navToNextScreenPressed = false
        composeTestRule.setContent {
            RegistrationScreen(
                registerScreenViewModel = RegisterScreenViewModel(),
                navToLogin = {
                    navToLoginPressed=true
                },
                navToNextScreen = {
                    navToNextScreenPressed=true
                })
        }

        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_CREATE_ACCOUNT_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_LOGIN_INPUT_ERROR).assertExists()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PASSWORD_INPUT_ERROR).assertExists()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PASSWORD_REPEAT_INPUT_ERROR).assertDoesNotExist()
        assert(!navToLoginPressed)
        assert(!navToNextScreenPressed)
    }
    @Test
    fun assert_Registration_Screen_No_Repeat_Password() {
        var navToLoginPressed = false
        var navToNextScreenPressed = false
        composeTestRule.setContent {
            RegistrationScreen(
                registerScreenViewModel = RegisterScreenViewModel(),
                navToLogin = {
                    navToLoginPressed=true
                },
                navToNextScreen = {
                    navToNextScreenPressed=true
                })
        }
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_LOGIN_INPUT).performTextInput(login)
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PASSWORD_INPUT).performTextInput(password)
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_CREATE_ACCOUNT_BUTTON).performClick()

        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_LOGIN_INPUT_ERROR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PASSWORD_INPUT_ERROR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PASSWORD_REPEAT_INPUT_ERROR).assertExists()
        assert(!navToLoginPressed)
        assert(!navToNextScreenPressed)

    }
    @Test
    fun assert_Registration_Screen_Pass() {
        var navToLoginPressed = false
        var navToNextScreenPressed = false
        composeTestRule.setContent {
            RegistrationScreen(
                registerScreenViewModel = RegisterScreenViewModel(),
                navToLogin = {
                    navToLoginPressed=true
                },
                navToNextScreen = {
                    navToNextScreenPressed=true
                })
        }
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_LOGIN_INPUT).performTextInput(login)
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PASSWORD_INPUT).performTextInput(password)
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PASSWORD_REPEAT_INPUT).performTextInput(password)
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_CREATE_ACCOUNT_BUTTON).performClick()

        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_LOGIN_INPUT_ERROR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PASSWORD_INPUT_ERROR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_PASSWORD_REPEAT_INPUT_ERROR).assertDoesNotExist()
        assert(navToNextScreenPressed)

        composeTestRule.onNodeWithTag(TestTags.REGISTER_SCREEN_LOGIN_BUTTON).performClick()
        assert(navToLoginPressed)

    }
}