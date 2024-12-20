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
class InterventionButtonsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun assert_InterventionButtons_Before_Start_Disabled() {
        val interventionStarted = mutableStateOf(false)
        val supportAlongTheWay = mutableStateOf(false)
        val isSystemDisconnected = mutableStateOf(true)

        composeTestRule.setContent {
            InterventionButtons(
                interventionStarted,
                supportAlongTheWay,
                isSystemDisconnected,
                {}, {}, {}, {})
        }
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_CONFIRM_ARRIVAL_BUTTON).assertExists().assertIsNotEnabled()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_REJECT_BUTTON).assertExists().assertIsNotEnabled()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_FINISH_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_REINFORCEMENTS_BUTTON).assertDoesNotExist()
    }

    @Test
    fun assert_InterventionButtons_Before_Start_Enabled() {
        val interventionStarted = mutableStateOf(false)
        val supportAlongTheWay = mutableStateOf(false)
        val isSystemDisconnected = mutableStateOf(false)

        composeTestRule.setContent {
            InterventionButtons(
                interventionStarted,
                supportAlongTheWay,
                isSystemDisconnected,
                {}, {}, {}, {})
        }
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_CONFIRM_ARRIVAL_BUTTON).assertExists().assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_REJECT_BUTTON).assertExists().assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_FINISH_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_REINFORCEMENTS_BUTTON).assertDoesNotExist()
    }
    @Test
    fun assert_InterventionButtons_After_Start_Disabled() {
        val interventionStarted = mutableStateOf(true)
        val supportAlongTheWay = mutableStateOf(false)
        val isSystemDisconnected = mutableStateOf(true)

        composeTestRule.setContent {
            InterventionButtons(
                interventionStarted,
                supportAlongTheWay,
                isSystemDisconnected,
                {}, {}, {}, {})
        }
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_CONFIRM_ARRIVAL_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_REJECT_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_FINISH_BUTTON).assertExists().assertIsNotEnabled()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_REINFORCEMENTS_BUTTON).assertExists().assertIsNotEnabled()
    }
    @Test
    fun assert_InterventionButtons_After_Start_Enabled() {
        val interventionStarted = mutableStateOf(true)
        val supportAlongTheWay = mutableStateOf(false)
        val isSystemDisconnected = mutableStateOf(false)

        composeTestRule.setContent {
            InterventionButtons(
                interventionStarted,
                supportAlongTheWay,
                isSystemDisconnected,
                {}, {}, {}, {})
        }
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_CONFIRM_ARRIVAL_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_REJECT_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_FINISH_BUTTON).assertExists().assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_REINFORCEMENTS_BUTTON).assertExists().assertIsEnabled()
    }
    @Test
    fun assert_InterventionButtons_After_Start_Support_Called() {
        val interventionStarted = mutableStateOf(true)
        val supportAlongTheWay = mutableStateOf(true)
        val isSystemDisconnected = mutableStateOf(false)

        composeTestRule.setContent {
            InterventionButtons(
                interventionStarted,
                supportAlongTheWay,
                isSystemDisconnected,
                {}, {}, {}, {})
        }
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_CONFIRM_ARRIVAL_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_REJECT_BUTTON).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_FINISH_BUTTON).assertExists().assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTags.INTERVENTION_BUTTONS_REINFORCEMENTS_BUTTON).assertExists().assertIsNotEnabled()
    }
}