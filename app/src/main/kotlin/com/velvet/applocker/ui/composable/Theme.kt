package com.velvet.applocker.ui.composable

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
        darkColors(
            primary = Color(0xFF648CFD),
            primaryVariant = Color(0xFF1E5FC9),
            onPrimary = Color(0xFF000000),
            secondary = Color(0xFF6CC9AD),
            secondaryVariant = Color(0xFF38987E),
            onSecondary = Color(0xFF000000),
            surface = Color(0xFF212121)
        )
    } else {
        lightColors(
            primary = Color(0xFF1E5FC9),
            primaryVariant = Color(0xFF003697),
            onPrimary = Color(0xFFFFFFFF),
            secondary = Color(0xFF38987E),
            secondaryVariant = Color(0xFF02886B),
            onSecondary = Color(0xFF000000),
            surface = Color(0xFFEEEEEE)
        )
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