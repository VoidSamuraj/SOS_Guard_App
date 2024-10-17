package com.pollub.awpfog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pollub.awpfog.ui.theme.AwpfogTheme

/**
 * Composable function that renders different sets of buttons depending on whether the intervention has started.
 *
 * @param interventionStarted A MutableState that controls the current state of the intervention.
 *                            If true, it indicates that the intervention is active.
 * @param supportAlongTheWay A MutableState that controls whether support has been called.
 *                           If true, it indicates that support is on its way.
 * @param confirmArrival Callback function that is invoked when the "Confirm Arrival" button is clicked.
 * @param stopIntervention Callback function that is invoked when the "Stop Intervention" button is clicked.
 * @param callForSupport Callback function that is invoked when the "Call for Support" button is clicked.
 * @param endIntervention Callback function that is invoked when the "End Intervention" button is clicked.
 */
@Composable
fun InterventionButtons(interventionStarted: MutableState<Boolean>, supportAlongTheWay:  MutableState<Boolean>, confirmArrival:()->Unit, stopIntervention:()->Unit, callForSupport:()->Unit, endIntervention:()->Unit){
    if(!interventionStarted.value)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    interventionStarted.value=true
                    confirmArrival()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(80.dp)
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(text = "Potwierdź przybycie", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
            Button(
                onClick = {
                    interventionStarted.value=false
                    supportAlongTheWay.value=false
                    stopIntervention()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(80.dp)
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(text = "Przerwij interwencję", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }
    else
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 0.dp),
        ) {
            Button(
                onClick = {
                    interventionStarted.value=false
                    supportAlongTheWay.value=false
                    endIntervention()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(100.dp)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()

            ) {
                Text(text = "Zakończ interwencję", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
            var buttonColor = if (supportAlongTheWay.value) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondary
            Button(
                enabled = !supportAlongTheWay.value,
                onClick = {
                    if(!supportAlongTheWay.value)
                        callForSupport()
                    supportAlongTheWay.value=true
                },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor, disabledContainerColor = buttonColor),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(100.dp)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                if(supportAlongTheWay.value)
                    Text(text = "Wsparcie w drodze", color = Color.DarkGray, fontSize = 18.sp, textAlign = TextAlign.Center)
                else
                    Text(text = "Wezwij wsparcie", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }
}

@Preview(showBackground = true)
@Composable
fun InterventionButtonsPreview() {
    val interventionStarted = remember{ mutableStateOf(true) }
    val supportAlongTheWay = remember{ mutableStateOf(true) }
    AwpfogTheme(dynamicColor = false) {
        InterventionButtons(interventionStarted, supportAlongTheWay,{},{},{},{})
    }
}