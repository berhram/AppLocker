package com.velvet.kamikazelock.ui.overlay

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OverlayScreen(viewModel: OverlayViewModel) {
    val state = viewModel.container.stateFlow.collectAsState().value
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.container.sideEffectFlow.collectLatest {
            when (it) {

            }
        }
    }
    Surface {
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
               Text(text = state.password)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { viewModel.enterDigit(1) }) {
                        Text(text = "1")
                    }
                    Button(onClick = { viewModel.enterDigit(2) }) {
                        Text(text = "2")
                    }
                    Button(onClick = { viewModel.enterDigit(3) }) {
                        Text(text = "3")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { viewModel.enterDigit(4) }) {
                        Text(text = "4")
                    }
                    Button(onClick = { viewModel.enterDigit(5) }) {
                        Text(text = "5")
                    }
                    Button(onClick = { viewModel.enterDigit(6) }) {
                        Text(text = "6")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { viewModel.enterDigit(7) }) {
                        Text(text = "7")
                    }
                    Button(onClick = { viewModel.enterDigit(8) }) {
                        Text(text = "8")
                    }
                    Button(onClick = { viewModel.enterDigit(9) }) {
                        Text(text = "9")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { viewModel.deleteDigit() }) {
                        Icon(imageVector = Icons.Filled.Cancel, contentDescription = "Remove icon")
                    }
                    Button(onClick = { viewModel.enterDigit(0) }) {
                        Text(text = "0")
                    }
                    IconButton(onClick = { viewModel.confirm() }) {
                        Icon(imageVector = Icons.Filled.ArrowRight, contentDescription = "Enter icon")
                    }
                }
            }
        }
    }
}