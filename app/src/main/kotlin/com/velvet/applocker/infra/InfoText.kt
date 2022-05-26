package com.velvet.applocker.infra

import android.content.Intent
import android.provider.Settings
import androidx.annotation.StringRes
import com.velvet.applocker.R

data class InfoText(
    val type: TextType,
    @StringRes val textId: Int,
    val action: Pair<ActionType, Intent?>? = null
) {
    companion object {
        private fun createInstruction() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.instruction)
        }

        private fun createWelcome() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.welcome)
        }

        private fun createDevContacts() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.dev_contacts)
        }

        private fun createSetPassword() : InfoText {
            return InfoText(
                type = TextType.INFO,
                textId = R.string.create_password_info,
                action = ActionType.SET_PASSWORD to null
            )
        }

        private fun createSetFace() : InfoText {
            return InfoText(
                type = TextType.INFO,
                textId = R.string.set_face_info,
                action = ActionType.SET_FACE to null
            )
        }

        fun createEssentials() = listOf(
            createWelcome(),
            createInstruction(),
            createSetPassword(),
            createSetFace(),
            createDevContacts()
        )

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
    NAVIGATION,
    SET_PASSWORD,
    SET_FACE
}

enum class TextType {
    WARNING, //For highlighted messages.
    INFO //For common messages.
}
