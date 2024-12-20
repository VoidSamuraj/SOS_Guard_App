package com.pollub.awpfog.ui.components

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pollub.awpfog.R
import com.pollub.awpfog.utils.TestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun assert_TopBar_On_Icon_Click(){
        var saveCalled = false
        val userName="Janusz Nowak"
        composeTestRule.setContent {
            TopBar(userName, R.drawable.baseline_account_circle_24, onIconClick = {saveCalled = true }, onLogout = {saveCalled = false })
        }
        composeTestRule.onNodeWithTag(TestTags.TOP_BAR_USER_NAME).assertTextEquals(userName)
        composeTestRule.onNodeWithTag(TestTags.TOP_BAR_ICON_BUTTON).performClick()
        assert(saveCalled)
    }
    @Test
    fun assert_TopBar_On_Logout_Click(){
        var saveCalled = false
        val userName="Janusz Nowak"
        composeTestRule.setContent {
            TopBar(userName, R.drawable.baseline_account_circle_24, onIconClick = {saveCalled = false }, onLogout = {saveCalled = true })
        }
        composeTestRule.onNodeWithTag(TestTags.TOP_BAR_USER_NAME).assertTextEquals(userName)
        composeTestRule.onNodeWithTag(TestTags.TOP_BAR_LOGOUT_BUTTON).performClick()
        assert(saveCalled)
    }
}