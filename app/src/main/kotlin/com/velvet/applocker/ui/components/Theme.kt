package com.velvet.applocker.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) {
        darkColors(surface = Color(0xFF212121))
    } else {
        lightColors(surface = Color(0xFFEEEEEE))
    }
    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme (
            colors = colors,
            content = content,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes
        )
    }
}