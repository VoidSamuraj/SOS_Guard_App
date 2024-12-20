package com.pollub.awpfog.ui.login

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pollub.awpfog.utils.TestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    val login = "login"
    val password = "pa\$\$word"

    @Test
    fun assert_Login_Screen_Works() {
        var loginPressed = false
        var registerPressed = false
        var remindPasswordPressed = false
        var receivedLogin = ""
        var receivedPassword = ""
        composeTestRule.setContent {
            LoginScreen(
                onLoginPress = { login, password ->
                    receivedLogin = login
                    receivedPassword = password
                    loginPressed = true
                },
                onRemindPasswordPress = {
                    remindPasswordPressed = true
                },
                navToRegister = {
                    registerPressed = true
                })
        }
        composeTestRule.onNodeWithTag(TestTags.LOGIN_SCREEN_LOGIN_INPUT)
            .performTextInput(login)
        composeTestRule.onNodeWithTag(TestTags.LOGIN_SCREEN_PASSWORD_INPUT)
            .performTextInput(password)
        composeTestRule.onNodeWithTag(TestTags.LOGIN_SCREEN_LOGIN_BUTTON).performClick()
        assert(loginPressed)
        assert(!registerPressed)
        assert(!remindPasswordPressed)
        assert(receivedLogin == login)
        assert(receivedPassword == password)
        composeTestRule.onNodeWithTag(TestTags.LOGIN_SCREEN_REGISTER_BUTTON).performClick()
        assert(loginPressed)
        assert(registerPressed)
        assert(!remindPasswordPressed)
        composeTestRule.onNodeWithTag(TestTags.LOGIN_SCREEN_FORGOT_PASSWORD_BUTTON).performClick()
        assert(loginPressed)
        assert(registerPressed)
        assert(remindPasswordPressed)
    }
}