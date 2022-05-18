package com.velvet.applocker.infra

import androidx.annotation.StringRes
import com.velvet.applocker.R

data class InfoText(
    val type: TextType,
    @StringRes val textId: Int
) {
    companion object {
        fun getInstruction() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.instruction)
        }

        fun getWelcome() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.welcome)
        }

        fun getFalsePasswordNotYetImplemented() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.false_password_not_implemented)
        }

        fun getDevContacts() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.dev_contacts)
        }

        fun getUsageStatsWarning() : InfoText {
            return InfoText(type = TextType.WARNING, textId = R.string.usage_stats_warning)
        }

        fun getOverlayWarning() : InfoText {
            return InfoText(type = TextType.WARNING, textId = R.string.overlay_warning)
        }
    }
}
