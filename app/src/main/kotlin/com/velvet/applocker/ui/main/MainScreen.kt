package com.velvet.applocker.ui.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.util.lerp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import com.velvet.applocker.R
import com.velvet.applocker.infra.TextType
import com.velvet.applocker.ui.components.*
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val state = viewModel.container.stateFlow.collectAsState().value
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
    if (state.isChangePasswordDialogEnabled) {
        PasswordChangeDialog(
            onDismiss = { viewModel.passwordDialogSwitch() },
            setNewPassword = { viewModel.setNewPassword(it) },
            errorTextId = state.newPasswordErrorTextId
        )
    }
    if (state.isChangeFaceDialogEnabled) {
        FacesDialog(onChoosing = { viewModel.faceChangeChoice(it) }, onDismiss = { viewModel.faceChangeSwitch() })
    }
    if (state.isAppLockDialogEnabled) {
        AppListDialog(
            appList = state.appList,
            onChoosing = { viewModel.appLockChoice(it) },
            onDismiss = { viewModel.appLockDialogSwitch() },
            onApply = { viewModel.applyLock() }
        )
    }
    Surface(color = MaterialTheme.colors.background) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val pagerState = rememberPagerState()
                HorizontalPager(
                    count = state.infoTextList.size,
                    contentPadding = PaddingValues(start = 50.dp, end = 50.dp, top = MaterialTheme.spacing.small),
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
                        .padding(MaterialTheme.spacing.small),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = state.infoTextList[page].textId),
                            color = if (state.infoTextList[page].type == TextType.WARNING) {
                                MaterialTheme.colors.onError
                            } else MaterialTheme.colors.onPrimary
                        )
                    }
                }
                HorizontalPagerIndicator(pagerState = pagerState, Modifier.padding(MaterialTheme.spacing.small))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(modifier = Modifier.width(200.dp), onClick = { viewModel.appLockDialogSwitch() }) {
                    Text(text = stringResource(id = R.string.app_lock_button))
                }
                Button(modifier = Modifier.width(200.dp), onClick = { viewModel.passwordDialogSwitch() }) {
                    Text(text = stringResource(id = R.string.password_change_button))
                }
                Button(modifier = Modifier.width(200.dp), onClick = { viewModel.faceChangeSwitch() }) {
                    Text(text = stringResource(id = R.string.face_change_button))
                }
            }
        }
    }
}

