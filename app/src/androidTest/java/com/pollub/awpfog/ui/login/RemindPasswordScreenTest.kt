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
class RemindPasswordScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun assert_RemindPasswordScreen_Email_Not_Valid(){

        var sendPressed = false
        var navBackPressed = false
        composeTestRule.setContent {
            RemindPasswordScreen(
                onSendPress = {
                    sendPressed=true
                },
                navBack = {
                    navBackPressed=true
                }
            )
        }

        composeTestRule.onNodeWithTag(TestTags.REMIND_PASSWORD_SCREEN_REMIND_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TestTags.REMIND_PASSWORD_SCREEN_EMAIL_INPUT_ERROR).assertExists()
        assert(!sendPressed)
        assert(!navBackPressed)
    }
    @Test
    fun assert_RemindPasswordScreen_Email_Valid(){

        var sendPressed = false
        var navBackPressed = false
        composeTestRule.setContent {
            RemindPasswordScreen(
                onSendPress = {
                    sendPressed=true
                },
                navBack = {
                    navBackPressed=true
                }
            )
        }
        composeTestRule.onNodeWithTag(TestTags.REMIND_PASSWORD_SCREEN_REMIND_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TestTags.REMIND_PASSWORD_SCREEN_EMAIL_INPUT_ERROR).assertExists()
        assert(!sendPressed)
        composeTestRule.onNodeWithTag(TestTags.REMIND_PASSWORD_SCREEN_EMAIL_INPUT).performTextInput("example.user@domain.com")
        composeTestRule.onNodeWithTag(TestTags.REMIND_PASSWORD_SCREEN_REMIND_BUTTON).performClick()
        assert(sendPressed)
        assert(!navBackPressed)
        composeTestRule.onNodeWithTag(TestTags.REMIND_PASSWORD_SCREEN_EMAIL_INPUT_ERROR).assertDoesNotExist()

        composeTestRule.onNodeWithTag(TestTags.REMIND_PASSWORD_SCREEN_NAV_BACK).performClick()
        assert(sendPressed)
        assert(navBackPressed)
    }
}