package com.velvet.kamikazelock.ui.overlay

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.velvet.kamikazelock.R
import com.velvet.kamikazelock.ui.main.MainEffect
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OverlayScreen(viewModel: OverlayViewModel) {
    val state = viewModel.container.stateFlow.collectAsState().value
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.container.sideEffectFlow.collectLatest {
            when (it) {
                OverlayEffect.PasswordTooShort -> Toast.makeText(context, R.string.password_too_short, Toast.LENGTH_LONG).show()
            }
        }
    }
    Surface {
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(0.4f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = state.password, style = MaterialTheme.typography.h3, color = MaterialTheme.colors.onBackground)
                }
            }
            Column(
                modifier = Modifier.weight(0.6f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val buttonSize = 65.dp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(modifier = Modifier.size(buttonSize), onClick = { viewModel.enterDigit(1) }) {
                        Text(text = "1", style = MaterialTheme.typography.h6)
                    }
                    Button(modifier = Modifier.size(buttonSize), onClick = { viewModel.enterDigit(2) }) {
                        Text(text = "2", style = MaterialTheme.typography.h6)
                    }
                    Button(modifier = Modifier.size(buttonSize), onClick = { viewModel.enterDigit(3) }) {
                        Text(text = "3", style = MaterialTheme.typography.h6)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(modifier = Modifier.size(buttonSize), onClick = { viewModel.enterDigit(4) }) {
                        Text(text = "4", style = MaterialTheme.typography.h6)
                    }
                    Button(modifier = Modifier.size(buttonSize), onClick = { viewModel.enterDigit(5) }) {
                        Text(text = "5", style = MaterialTheme.typography.h6)
                    }
                    Button(modifier = Modifier.size(buttonSize), onClick = { viewModel.enterDigit(6) }) {
                        Text(text = "6", style = MaterialTheme.typography.h6)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(modifier = Modifier.size(buttonSize), onClick = { viewModel.enterDigit(7) }) {
                        Text(text = "7", style = MaterialTheme.typography.h6)
                    }
                    Button(modifier = Modifier.size(buttonSize), onClick = { viewModel.enterDigit(8) }) {
                        Text(text = "8", style = MaterialTheme.typography.h6)
                    }
                    Button(modifier = Modifier.size(buttonSize), onClick = { viewModel.enterDigit(9) }) {
                        Text(text = "9", style = MaterialTheme.typography.h6)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(modifier = Modifier.size(buttonSize), onClick = { viewModel.deleteDigit() }) {
                        Icon(modifier = Modifier.fillMaxSize(), imageVector = Icons.Filled.ArrowLeft, contentDescription = "Remove icon")
                    }
                    Button(modifier = Modifier.size(buttonSize), onClick = { viewModel.enterDigit(0) }) {
                        Text(text = "0", style = MaterialTheme.typography.h6)
                    }
                    IconButton(modifier = Modifier.size(buttonSize), onClick = { viewModel.confirm() }) {
                        Icon(modifier = Modifier.fillMaxSize(), imageVector = Icons.Filled.ArrowRight, contentDescription = "Enter icon")
                    }
                }
            }
        }
    }
}