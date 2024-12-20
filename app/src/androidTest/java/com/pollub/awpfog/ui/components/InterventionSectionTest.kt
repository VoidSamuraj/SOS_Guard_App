package com.pollub.awpfog.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pollub.awpfog.utils.TestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InterventionSectionTest() {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun assert_InterventionSection_Does_Not_Exist() {
        val visible = mutableStateOf(false)
        val isConnecting = mutableStateOf(true)
        composeTestRule.setContent {
            InterventionSection(visible, isConnecting, "Nadbystrzycka", {}, {})
        }
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_SECTION_MAIN_COLUMN)
            .assertDoesNotExist()
    }

    @Test
    fun assert_InterventionSection_Does_Exist() {
        val visible = mutableStateOf(true)
        val isConnecting = mutableStateOf(true)
        composeTestRule.setContent {
            InterventionSection(visible, isConnecting, "Nadbystrzycka", {}, {})
        }
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_SECTION_MAIN_COLUMN).assertExists()
    }

    @Test
    fun assert_InterventionSection_Buttons_Are_Not_Active() {
        val visible = mutableStateOf(true)
        val isConnecting = mutableStateOf(true)
        composeTestRule.setContent {
            InterventionSection(visible, isConnecting, "Nadbystrzycka", {}, {})
        }
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_SECTION_CONFIRM_INTERVENTION_BUTTON)
            .assertIsNotEnabled()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_SECTION_REJECT_INTERVENTION_BUTTON)
            .assertIsNotEnabled()
    }

    @Test
    fun assert_InterventionSection_Buttons_Are_Active() {
        val visible = mutableStateOf(true)
        val isConnecting = mutableStateOf(false)
        composeTestRule.setContent {
            InterventionSection(visible, isConnecting, "Nadbystrzycka", {}, {})
        }
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_SECTION_CONFIRM_INTERVENTION_BUTTON)
            .assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_SECTION_REJECT_INTERVENTION_BUTTON)
            .assertIsEnabled()
    }

}