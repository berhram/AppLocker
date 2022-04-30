package com.velvet.kamikazelock.ui.main

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.util.lerp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import com.google.accompanist.pager.*
import com.velvet.kamikazelock.R
import com.velvet.kamikazelock.data.infra.AppInfo
import com.velvet.kamikazelock.data.infra.Face
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
                is MainEffect.IconChanged -> Toast.makeText(context, R.string.icon_change_success, Toast.LENGTH_LONG).show()
            }
        }
    }
    if (state.isChangeFaceDialogEnabled) {
        FacesDialog(onChoosing = { viewModel.faceChangeChoice(it) }, onDismiss = { viewModel.faceChangeDialogDismiss() })
    }
    if (state.isAppLockDialogEnabled) {
        AppListDialog(
            appList = state.appList,
            onChoosing = { viewModel.appLockChoice(it) },
            onDismiss = { viewModel.appLockDialogDismiss() },
            onApply = { viewModel.applyLock() },
            isLoading = state.isAppLoading
        )
    }
    Surface {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val pagerState = rememberPagerState()
                HorizontalPager(
                    count = 4,
                    contentPadding = PaddingValues(start = 50.dp, end = 50.dp, top = 10.dp),
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
                        .padding(10.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colors.primary)
                        .aspectRatio(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Text field number $page",
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
                HorizontalPagerIndicator(pagerState = pagerState, Modifier.padding(10.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(modifier = Modifier.width(200.dp), onClick = { viewModel.appLockButtonClick() }) {
                    Text(text = "Locked apps")
                }
                Button(modifier = Modifier.width(200.dp), onClick = { /*TODO*/ }) {
                    Text(text = "Test button 2")
                }
                Button(modifier = Modifier.width(200.dp), onClick = { viewModel.faceChangeButtonClick() }) {
                    Text(text = "Change Face")
                }
            }
        }
    }
}

@Composable
fun FacesDialog(onChoosing: (Face) -> Unit, onDismiss: () -> Unit) {
    @Composable
    fun FaceItem(imageId: Int, textId: Int, face: Face) {
        Column(modifier = Modifier.clickable { onChoosing(face) }, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = imageId), contentDescription = "Image with id $imageId")
            Text(text = stringResource(id = textId))
        }
    }
    Dialog({ onDismiss() }) {
        Column(
            Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.background)
                .padding(10.dp)
        ) {
            Text(text = stringResource(id = R.string.face_change_dialog_title), style = MaterialTheme.typography.h4, color = MaterialTheme.colors.primary)
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = stringResource(id = R.string.face_change_dialog_text), style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onBackground)
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FaceItem(imageId = R.drawable.launcher, textId = R.string.app_name, face = Face.DEFAULT)
                FaceItem(imageId = R.drawable.schedule, textId = R.string.app_name_schedule, face = Face.SCHEDULE)
                FaceItem(imageId = R.drawable.fitness, textId = R.string.app_name_fitness, face = Face.FITNESS)
            }
            OutlinedButton(modifier = Modifier.weight(1f), onClick = { onDismiss() }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    }
}

@Composable
fun AppListDialog(appList: List<AppInfo>, onChoosing: (AppInfo) -> Unit, onDismiss: () -> Unit, onApply: () -> Unit, isLoading: Boolean) {
    @Composable
    fun AppItem(item: AppInfo) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Image(modifier = Modifier.size(32.dp), bitmap = item.icon.toBitmap().asImageBitmap(), contentDescription = "Image with id ${item.name}")
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = item.name, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.weight(1f))
            Checkbox(checked = item.isChanged xor item.isLocked, onCheckedChange = { onChoosing(item) })
        }
    }
    Dialog({ onDismiss() }) {
        Column(
            Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.background)
                .padding(10.dp)
        ) {
            Text(text = stringResource(id = R.string.locked_apps_dialog_title),
                style = MaterialTheme.typography.h4, color = MaterialTheme.colors.primary)
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = stringResource(id = R.string.locked_apps_dialog_text), style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onBackground)
            Spacer(modifier = Modifier.size(10.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (appList.isEmpty() || isLoading) {
                    item {
                        DotsPulsing(size = 20.dp, delayUnit = 300)
                    }
                } else {
                    items(appList) {
                        AppItem(item = it)
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(modifier = Modifier.weight(1f), onClick = { onDismiss() }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
                Spacer(modifier = Modifier.size(10.dp))
                Button(modifier = Modifier.weight(1f), onClick = { onApply() }) {
                    Text(text = stringResource(id = R.string.apply))
                }
            }
        }
    }
}

@Composable
fun DotsPulsing(size: Dp, delayUnit: Int) {
    @Composable
    fun Dot(
        scale: Float
    ) = Spacer(
        Modifier
            .size(size)
            .scale(scale)
            .background(color = MaterialTheme.colors.primary, shape = CircleShape)
    )
    val infiniteTransition = rememberInfiniteTransition()
    @Composable
    fun animateScaleWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                1f at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        )
    )
    val scale1 by animateScaleWithDelay(0)
    val scale2 by animateScaleWithDelay(delayUnit)
    val scale3 by animateScaleWithDelay(delayUnit * 2)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        val spaceSize = 2.dp
        Dot(scale1)
        Spacer(Modifier.width(spaceSize))
        Dot(scale2)
        Spacer(Modifier.width(spaceSize))
        Dot(scale3)
    }
}