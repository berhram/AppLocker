package com.velvet.applocker.infra

import android.content.Intent
import android.provider.Settings
import androidx.annotation.StringRes
import com.velvet.applocker.R

data class InfoText(
    val type: TextType,
    @StringRes val textId: Int,
    val action: Pair<ActionType, Intent>? = null
) {
    companion object {
        fun createInstruction() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.instruction)
        }

        fun createWelcome() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.welcome)
        }

        fun createDevContacts() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.dev_contacts)
        }

        fun createUsageStatsWarning() : InfoText {
            return InfoText(
                type = TextType.WARNING,
                textId = R.string.usage_stats_warning,
                action = ActionType.NAVIGATION to Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            )
        }

        fun createOverlayWarning() : InfoText {
            return InfoText(
                type = TextType.WARNING,
                textId = R.string.overlay_warning,
                action = ActionType.NAVIGATION to Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            )
        }
    }
}

enum class ActionType {
    NAVIGATION
}

enum class TextType {
    WARNING, //For highlighted messages.
    INFO //For common messages.
}
