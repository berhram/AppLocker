package com.velvet.applocker.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.velvet.applocker.R
import com.velvet.applocker.infra.AppInfo
import com.velvet.applocker.infra.Face

@Composable
fun SystemUISetup() {
    val systemUiController = rememberSystemUiController()
    val isLightTheme = MaterialTheme.colors.isLight
    val systemBarColor = MaterialTheme.colors.surface
    val transparentColor: (Color) -> Color = { original ->
        systemBarColor.compositeOver(original)
    }
    LaunchedEffect(Unit) {
        systemUiController.setStatusBarColor(
            color = systemBarColor, darkIcons = isLightTheme,
            transformColorForLightContent = transparentColor
        )
        systemUiController.setNavigationBarColor(
            color = systemBarColor,
            darkIcons = isLightTheme,
            navigationBarContrastEnforced = false,
            transformColorForLightContent = transparentColor
        )
    }
}

@Composable
fun AppItem(item: AppInfo, onChoosing: (AppInfo) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            bitmap = item.icon.toBitmap().asImageBitmap(),
            contentDescription = "Image with id ${item.name}"
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
        Text(text = item.name, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.weight(1f))
        Checkbox(checked = item.isChanged xor item.isLocked, onCheckedChange = { onChoosing(item) })
    }
}

@Composable
fun AppListDialog(appList: List<AppInfo>, onChoosing: (AppInfo) -> Unit, onDismiss: () -> Unit, onApply: () -> Unit) {
    Dialog({ onDismiss() }) {
        Surface(color = MaterialTheme.colors.background) {
            Column(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colors.background)
                    .padding(MaterialTheme.spacing.small)
            ) {
                Text(text = stringResource(id = R.string.locked_apps_dialog_title),
                    style = MaterialTheme.typography.h4, color = MaterialTheme.colors.primary)
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                Text(
                    text = stringResource(id = R.string.locked_apps_dialog_text),
                    style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onBackground
                )
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (appList.isEmpty()) {
                        item {
                            DotsPulsing(size = 20.dp, delayUnit = 300)
                        }
                    } else {
                        items(appList) {
                            AppItem(item = it, onChoosing = onChoosing)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(modifier = Modifier.weight(1f), onClick = { onDismiss() }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                    Button(modifier = Modifier.weight(1f), onClick = { onApply() }) {
                        Text(text = stringResource(id = R.string.apply))
                    }
                }
            }
        }
    }
}

@Composable
fun FaceItem(imageId: Int, textId: Int, face: Face, onChoosing: (Face) -> Unit) {
    Column(
        modifier = Modifier.clickable { onChoosing(face) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = imageId), contentDescription = "Image with id $imageId")
        Text(text = stringResource(id = textId))
    }
}
//TODO turn icons to list
@Composable
fun FacesDialog(onChoosing: (Face) -> Unit, onDismiss: () -> Unit) {
    Dialog({ onDismiss() }) {
        Surface(color = MaterialTheme.colors.background) {
            Column(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colors.background)
                    .padding(MaterialTheme.spacing.small)
            ) {
                Text(
                    text = stringResource(id = R.string.face_change_dialog_title),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                Text(
                    text = stringResource(id = R.string.face_change_dialog_text),
                    style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onBackground
                )
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FaceItem(imageId = R.drawable.launcher, textId = R.string.app_name, face = Face.DEFAULT, onChoosing = onChoosing)
                    FaceItem(imageId = R.drawable.schedule, textId = R.string.app_name_schedule, face = Face.SCHEDULE, onChoosing = onChoosing)
                    FaceItem(imageId = R.drawable.fitness, textId = R.string.app_name_fitness, face = Face.FITNESS, onChoosing = onChoosing)
                }
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { onDismiss() }) {
                    Text(text = stringResource(id = R.string.cancel))
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

@Composable
fun PasswordChangeDialog(
    onDismiss: () -> Unit,
    setNewPassword: (String) -> Unit,
    @StringRes errorTextId: Int?
) {
    var newPassword by remember { mutableStateOf("") }
    Dialog({ onDismiss() }) {
        Surface(color = MaterialTheme.colors.background) {
            Column(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colors.background)
                    .padding(MaterialTheme.spacing.small)
            ) {
                Text(
                    text = stringResource(id = R.string.change_password_dialog_title),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                Text(
                    text = stringResource(id = R.string.change_password_dialog_text),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground
                )
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                if (errorTextId != null) {
                    Text(
                        text = stringResource(id = errorTextId),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.error
                    )
                    Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                }
                OutlinedTextField(
                    value = newPassword, onValueChange = { newPassword = it },
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(R.string.new_password_enter),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.caption
                        )
                    }
                )
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(modifier = Modifier.weight(1f), onClick = { onDismiss() }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { setNewPassword(newPassword) }) {
                        Text(text = stringResource(id = R.string.apply))
                    }
                }
            }
        }
    }
}