package com.pollub.awpfog.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pollub.awpfog.ui.theme.AwpfogTheme

/**
 * Composable function that displays a section for handling an intervention.
 * The section includes the intervention location and buttons for confirming or rejecting the intervention.
 * It is displayed only when the `isVisible` flag is true.
 *
 * @param isVisible MutableStateOf<Boolean> flag that controls the visibility of the section.
 *                  If true, the intervention section is shown.
 * @param location A string representing the location of the intervention.
 *                 Displayed in the UI as the intervention location.
 * @param onConfirm A callback function that is invoked when the "Confirm Intervention" button is clicked.
 * @param onReject A callback function that is invoked when the "Reject Intervention" button is clicked.
 */
@Composable
fun InterventionSection(
    isVisible: MutableState<Boolean>,
    location: String,
    onConfirm: () -> Unit,
    onReject: () -> Unit
) {
    if(isVisible.value)
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = "Interwencja", fontSize = 18.sp, color = Color.Red)
            Text(text = location, color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280))
            ) {
                Text(text = "Potwierdź interwencję", color = Color.White)
            }

            Button(
                onClick = onReject,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(text = "Odrzuć interwencję", color = Color.White)
            }
        }
}

@Preview(showBackground = true)
@Composable
fun InterventionSectionPreview() {
    AwpfogTheme(dynamicColor = false) {
        val visible = remember { mutableStateOf(true) }
        InterventionSection(visible,"Nadystrzycka",{},{})
    }
}