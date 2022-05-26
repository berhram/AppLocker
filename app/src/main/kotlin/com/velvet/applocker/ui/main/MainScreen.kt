package com.velvet.applocker.ui.main

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.util.lerp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.*
import com.velvet.applocker.R
import com.velvet.applocker.infra.ActionType
import com.velvet.applocker.infra.Face
import com.velvet.applocker.infra.TextType
import com.velvet.applocker.ui.composable.*
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val state = viewModel.container.stateFlow.collectAsState().value
    val setPasswordDialogState = remember { mutableStateOf(false) }
    val setFaceDialogState = remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.container.sideEffectFlow.collectLatest {
            when (it) {
                is MainEffect.IconChanged -> {
                    Toast.makeText(context, R.string.icon_change_success, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    LaunchedEffect(setPasswordDialogState) {
        if (!setFaceDialogState.value) {
            viewModel.onCloseSetPasswordDialog()
        }
    }

    fun handleAction(action: Pair<ActionType, Intent?>) {
        when (action.first) {
            ActionType.NAVIGATION -> { context.startActivity(action.second) }
            ActionType.SET_FACE -> { setFaceDialogState.value = true }
            ActionType.SET_PASSWORD -> { setPasswordDialogState.value = true }
        }
    }

    if (setPasswordDialogState.value) {
        PasswordChangeDialog(
            state = setPasswordDialogState,
            setNewPassword = { viewModel.setNewPassword(it) },
            errorTextId = state.setPasswordErrorTextId
        )
    }

    if (setFaceDialogState.value) {
        FacesDialog(
            onChoosing = { viewModel.faceChangeChoice(it) },
            state = setFaceDialogState,
            faces = Face.getDefaultFaces()
        )
    }

    Surface(color = MaterialTheme.colors.background) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            val pagerState = rememberPagerState()
            HorizontalPager(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                count = state.infoTextList.size,
                contentPadding = PaddingValues(
                    start = MaterialTheme.spacing.large,
                    end = MaterialTheme.spacing.large,
                    top = MaterialTheme.spacing.small
                ),
                state = pagerState
            ) { page ->
                Column(modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                    .clip(MaterialTheme.shapes.medium)
                    .background(if (state.infoTextList[page].type == TextType.WARNING) MaterialTheme.colors.error else MaterialTheme.colors.primary)
                    .aspectRatio(1f)
                    .padding(MaterialTheme.spacing.large),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = state.infoTextList[page].textId),
                        color = if (state.infoTextList[page].type == TextType.WARNING) {
                            MaterialTheme.colors.onError
                        } else {
                            MaterialTheme.colors.onPrimary
                        }
                    )
                    state.infoTextList[page].action?.let { action ->
                        Spacer(modifier = Modifier.weight(1f))
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { handleAction(action) },
                            //TODO more detailed test needed
                            colors = ButtonDefaults.buttonColors(
                                contentColor = if (state.infoTextList[page].type == TextType.WARNING) {
                                    MaterialTheme.colors.error
                                } else MaterialTheme.colors.primary,
                                backgroundColor = if (state.infoTextList[page].type == TextType.WARNING) {
                                    MaterialTheme.colors.onError
                                } else MaterialTheme.colors.onBackground
                            )
                        ) {
                            Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = stringResource(id = R.string.proceed))
                        }
                    }
                }
            }
            HorizontalPagerIndicator(pagerState = pagerState, Modifier.padding(MaterialTheme.spacing.small))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                AppList(appList = state.appList, onChoosing = { viewModel.applyLock(it) })
            }
        }
    }
}

