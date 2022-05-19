package com.velvet.applocker.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.velvet.applocker.setThemedContent
import org.koin.androidx.compose.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setThemedContent {
            MainScreen(viewModel = getViewModel())
        }
    }
}

