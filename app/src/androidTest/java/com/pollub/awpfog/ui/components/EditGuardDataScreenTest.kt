package com.pollub.awpfog.ui.components

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
class EditGuardDataScreenTest() {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun assert_Edit_Guard_Data_Screen_Errors() {
        composeTestRule.setContent {
            EditGuardDataScreen()
        }
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_SUBMIT).performClick()
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_LOGIN_INPUT_ERROR).assertExists()
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_PASSWORD_INPUT_ERROR).assertExists()
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_NEW_PASSWORD_INPUT_ERROR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_NAME_INPUT_ERROR).assertExists()
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_SURNAME_INPUT_ERROR).assertExists()
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_EMAIL_INPUT_ERROR).assertExists()
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_PHONE_INPUT_ERROR).assertExists()
    }

    @Test
    fun assert_Edit_Guard_Data_Screen_Wrong_New_Password() {
        var saveCalled = false

        composeTestRule.setContent {
            EditGuardDataScreen(onSavePress = { _, _, _, _, _, _, _ -> saveCalled = true })
        }
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_LOGIN_INPUT)
            .performTextInput("Login123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_PASSWORD_INPUT)
            .performTextInput("Secure@123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_NEW_PASSWORD_INPUT)
            .performTextInput("qwerty")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_NAME_INPUT)
            .performTextInput("ValidUserName123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_SURNAME_INPUT)
            .performTextInput("ValidUserSurname123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_EMAIL_INPUT)
            .performTextInput("example.user@domain.com")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_PHONE_INPUT)
            .performTextInput("+12345678901")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_SUBMIT).performClick()

        assert(!saveCalled)
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_NEW_PASSWORD_INPUT_ERROR).assertExists()
    }
    @Test
    fun assert_Edit_Guard_Data_Screen_Good_New_Password() {
        var saveCalled = false

        composeTestRule.setContent {
            EditGuardDataScreen(onSavePress = { _, _, _, _, _, _, _ -> saveCalled = true })
        }
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_LOGIN_INPUT)
            .performTextInput("Login123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_PASSWORD_INPUT)
            .performTextInput("Secure@123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_NEW_PASSWORD_INPUT)
            .performTextInput("Secure@12345")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_NAME_INPUT)
            .performTextInput("ValidUserName123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_SURNAME_INPUT)
            .performTextInput("ValidUserSurname123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_EMAIL_INPUT)
            .performTextInput("example.user@domain.com")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_PHONE_INPUT)
            .performTextInput("+12345678901")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_SUBMIT).performClick()

        assert(saveCalled)
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_NEW_PASSWORD_INPUT_ERROR).assertDoesNotExist()
    }
    @Test
    fun assert_Edit_Guard_Data_Screen_Passed_No_New_Password() {
        var saveCalled = false

        composeTestRule.setContent {
            EditGuardDataScreen(onSavePress = { _, _, _, _, _, _, _ -> saveCalled = true })
        }
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_LOGIN_INPUT)
            .performTextInput("Login123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_PASSWORD_INPUT)
            .performTextInput("Secure@123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_NAME_INPUT)
            .performTextInput("ValidUserName123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_SURNAME_INPUT)
            .performTextInput("ValidUserSurname123")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_EMAIL_INPUT)
            .performTextInput("example.user@domain.com")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_PHONE_INPUT)
            .performTextInput("+12345678901")
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_SUBMIT).performClick()
        assert(saveCalled)
        composeTestRule.onNodeWithTag(TestTags.EDIT_GUARD_DATA_SCREEN_NEW_PASSWORD_INPUT_ERROR).assertDoesNotExist()
    }
}
