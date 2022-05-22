package com.velvet.applocker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.velvet.applocker.R
import com.velvet.applocker.infra.AppInfo
import com.velvet.applocker.xor

@Composable
fun AppList(appList: List<AppInfo>, onChoosing: (List<AppInfo>) -> Unit) {

    val changedList = remember { mutableStateListOf<AppInfo>() }

    @Composable
    fun AppItem(item: AppInfo) {
        Surface(modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = MaterialTheme.spacing.extraSmall,
                horizontal = MaterialTheme.spacing.medium
            ),
            color = MaterialTheme.colors.background
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(if (changedList.contains(item)) MaterialTheme.colors.primary else MaterialTheme.colors.surface),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                Image(
                    modifier = Modifier.size(32.dp),
                    bitmap = item.icon.toBitmap().asImageBitmap(),
                    contentDescription = "Image with id ${item.name}"
                )
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                Text(
                    text = item.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body1,
                    color = if (changedList.contains(item)) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface)
                Spacer(modifier = Modifier.weight(1f))
                Checkbox(
                    checked = changedList.contains(item) xor item.isLocked,
                    onCheckedChange = { changedList.xor(item) }
                )
            }
        }
    }
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                if (appList.isEmpty()) {
                    item {
                        DotsPulsing(size = 20.dp, delayUnit = 300)
                    }
                } else {
                    items(appList) { item ->
                        AppItem(item = item)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = MaterialTheme.spacing.small,
                        horizontal = MaterialTheme.spacing.medium
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(modifier = Modifier.weight(1f), onClick = { changedList.clear() }, enabled = (changedList.isNotEmpty())) {
                    Icon(imageVector = Icons.Filled.RemoveDone, contentDescription = stringResource(id = R.string.cancel))
                }
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                Button(modifier = Modifier.weight(4f), onClick = { onChoosing(changedList) }, enabled = (changedList.isNotEmpty())) {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = stringResource(id = R.string.apply))
                    Spacer(modifier = Modifier.size(MaterialTheme.spacing.extraSmall))
                    Text(text = stringResource(id = R.string.apply))
                }
            }
        }
    }
}

@Preview("AppList preview")
@Composable
private fun AppListPreview() {
    AppTheme {
        AppList(
            appList = listOf(
                AppInfo(
                    name = "Some app 1",
                    packageName = "",
                    isLocked = false,
                    icon = LocalContext.current.getDrawable(R.drawable.fitness)!!
                ),
                AppInfo(
                    name = "Some app 2",
                    packageName = "",
                    isLocked = true,
                    icon = LocalContext.current.getDrawable(R.drawable.fitness)!!
                ),
                AppInfo(
                    name = "Some app 3",
                    packageName = "",
                    isLocked = false,
                    icon = LocalContext.current.getDrawable(R.drawable.fitness)!!
                ),
                AppInfo(
                    name = "Some app 1",
                    packageName = "",
                    isLocked = false,
                    icon = LocalContext.current.getDrawable(R.drawable.fitness)!!
                ),
                AppInfo(
                    name = "Some app 2",
                    packageName = "",
                    isLocked = true,
                    icon = LocalContext.current.getDrawable(R.drawable.fitness)!!
                ),
                AppInfo(
                    name = "Some app 3",
                    packageName = "",
                    isLocked = false,
                    icon = LocalContext.current.getDrawable(R.drawable.fitness)!!
                ),
                AppInfo(
                    name = "Some app 1",
                    packageName = "",
                    isLocked = false,
                    icon = LocalContext.current.getDrawable(R.drawable.fitness)!!
                ),
                AppInfo(
                    name = "Some app 2",
                    packageName = "",
                    isLocked = true,
                    icon = LocalContext.current.getDrawable(R.drawable.fitness)!!
                ),
                AppInfo(
                    name = "Some app 3",
                    packageName = "",
                    isLocked = false,
                    icon = LocalContext.current.getDrawable(R.drawable.fitness)!!
                )
            ), onChoosing = { })
    }
}