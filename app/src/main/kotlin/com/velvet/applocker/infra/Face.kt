package com.velvet.applocker.infra

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.velvet.applocker.R

/**
 * Class that contains values to configuration looking of app's faces.
 * @param textId Id of string resource containing face name
 * @param iconId Id of drawable resource containing face icon
 * @param name If default, this must be path to main Activity, if additional, this must be alias name
 * Also can return preinstalled in AppLocker faces
 */
data class Face(
    @StringRes val textId: Int,
    @DrawableRes val iconId: Int,
    val name: String
) {
    companion object {
        fun getDefaultFaces() = listOf(
            Face(
                textId = R.string.app_name,
                iconId = R.drawable.launcher,
                name = "ui.main.MainActivity"
            ),
            Face(
                textId = R.string.app_name_fitness,
                iconId = R.drawable.fitness,
                name = "MainActivityFitnessAlias"
            ),
            Face(
                textId = R.string.app_name_schedule,
                iconId = R.drawable.schedule,
                name = "MainActivityScheduleAlias"
            )
        )
    }
}