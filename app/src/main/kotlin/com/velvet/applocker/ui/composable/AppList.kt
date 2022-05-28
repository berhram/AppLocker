package com.velvet.applocker.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.velvet.applocker.R
import com.velvet.applocker.infra.AppInfo

@Composable
fun AppList(appList: List<AppInfo>, onChoosing: (List<AppInfo>) -> Unit) {

    val changedList = remember { mutableStateListOf<AppInfo>() }

    LaunchedEffect(appList) {
        changedList.clear()
    }

    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (appList.isEmpty()) {
                    item {
                        DotsPulsing(size = 20.dp, delayUnit = 300)
                    }
                } else {
                    items(appList) { item ->
                        AppItem(item = item, changedList = changedList)
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
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { changedList.clear() },
                    enabled = (changedList.isNotEmpty())
                ) {
                    Icon(
                        imageVector = Icons.Filled.RemoveDone,
                        contentDescription = stringResource(id = R.string.cancel)
                    )
                }
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                Button(
                    modifier = Modifier.weight(4f),
                    onClick = { onChoosing(changedList) },
                    enabled = (changedList.isNotEmpty())
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = stringResource(id = R.string.apply)
                    )
                    Spacer(modifier = Modifier.size(MaterialTheme.spacing.extraSmall))
                    Text(text = stringResource(id = R.string.apply))
                }
            }
        }
    }
}