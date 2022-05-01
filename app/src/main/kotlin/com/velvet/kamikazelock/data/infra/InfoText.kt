package com.velvet.kamikazelock.data.infra

import androidx.annotation.StringRes
import com.velvet.kamikazelock.R

data class InfoText(
    val type: TextType,
    @StringRes val textId: Int
) {
    companion object {
        fun getInstruction() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.instruction)
        }

        fun getDevContacts() : InfoText {
            return InfoText(type = TextType.INFO, textId = R.string.dev_contacts)
        }

        fun getUsageStatsWarning() : InfoText {
            return InfoText(type = TextType.WARNING, textId = R.string.usage_stats_warning)
        }
    }
}
