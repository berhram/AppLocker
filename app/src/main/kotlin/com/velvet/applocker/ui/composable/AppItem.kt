package com.velvet.applocker.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.velvet.applocker.infra.AppInfo
import com.velvet.applocker.xor

@Composable
fun AppItem(item: AppInfo, changedList: MutableList<AppInfo>) {
    Surface(
        modifier = Modifier
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
                color = if (changedList.contains(item)) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Checkbox(
                checked = changedList.contains(item) xor item.isLocked,
                onCheckedChange = { changedList.xor(item) }
            )
        }
    }
}