package com.pollub.awpfog.utils

import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.pollub.awpfog.MainActivity

/**
 * Enables edge-to-edge mode and sets the theme colors for the system's status and navigation bars.
 *
 * @param lighterColor The color for the bars in light mode.
 * @param darkerColor The color for the bars in dark mode.
 *
 * Applies `lighterColor` for the background and `darkerColor` for content (icons, text) in light mode,
 * and `darkerColor` for both in dark mode.
 */
@Composable
fun MainActivity.EnableEdgeToEdgeAndSetBarTheme(lighterColor:Int, darkerColor:Int){
    enableEdgeToEdge(
        statusBarStyle = if (!isDarkMode) {
            SystemBarStyle.light(
                lighterColor,
                darkerColor
            )
        } else {
            SystemBarStyle.dark(
                darkerColor
            )
        },
        navigationBarStyle = if (!isDarkMode) {
            SystemBarStyle.light(
                lighterColor,
                darkerColor
            )
        } else {
            SystemBarStyle.dark(darkerColor)
        }
    )
}