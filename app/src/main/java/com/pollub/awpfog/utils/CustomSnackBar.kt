package com.pollub.awpfog.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pollub.awpfog.R


/**
 * Displays a custom Snackbar with an optional icon, message, and fade-out animation.
 *
 * @param modifier A modifier for customizing the appearance of the component.
 * @param message The text displayed in the Snackbar.
 * @param backgroundColor The background color of the Snackbar.
 * @param iconResId An optional resource ID for an icon to be displayed on the left side of the message.
 * @param duration The duration of the fade-out animation of the Snackbar in milliseconds. Defaults to 3000 ms (3 seconds).
 * @param onDismiss A callback function invoked after the fade-out animation completes.
 */
@Composable
fun CustomSnackBar(
    modifier: Modifier,
    message: String,
    backgroundColor: Color,
    iconResId: Int? = null,
    duration: Int = 3000,
    onDismiss: () -> Unit
) {
    val padding = 16.dp

    val progress = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 0f,
            animationSpec = tween(duration)
        )
        onDismiss() // Po zakończeniu animacji zamknij snackbar
    }

    Box(
        modifier = modifier
            .padding(padding)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(padding)
            .fillMaxWidth()
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                iconResId?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 8.dp)
                    )
                }
                Text(
                    text = message,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Box{
                    CircularProgressIndicator(
                        progress = { progress.value },
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .height(4.dp).align(Alignment.TopCenter),
                        color = Color.White,
                        trackColor = Color.Transparent,

                        )
                    IconButton(onClick = onDismiss, modifier= Modifier.align(Alignment.Center),) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Zamknij",
                            tint = Color.White
                        )
                    }
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewCustomSnackbar() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        CustomSnackBar(
            modifier = Modifier.padding(innerPadding),
            message = "To jest przykładowy komunikat!",
            backgroundColor = Color.Red,
            iconResId = R.drawable.baseline_error_outline_24,
            onDismiss = {}
        )
    }
}
