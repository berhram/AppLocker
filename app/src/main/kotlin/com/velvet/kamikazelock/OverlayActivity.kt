package com.velvet.kamikazelock

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.lifecycle.lifecycleScope
import com.velvet.kamikazelock.data.cache.app.RepositoryAppCache
import com.velvet.kamikazelock.data.cache.overlay.ActivityOverlayCache
import com.velvet.kamikazelock.data.infra.ValidationStatus
import com.velvet.kamikazelock.ui.overlay.OverlayScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

class OverlayActivity : ComponentActivity() {

    private val cache: ActivityOverlayCache by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.d("OVER", "overlay compose set")
            MaterialTheme(colors = if (isSystemInDarkTheme()) darkColors() else lightColors()) {
                OverlayScreen(viewModel = getViewModel { parametersOf(intent.getStringExtra(KEY_PACKAGE_NAME)) })
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            cache.successFlow.collect {
                when (it) {
                    ValidationStatus.SUCCESS -> {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent("android.intent.action.MAIN")
        intent.addCategory("android.intent.category.HOME")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    companion object {

        private const val KEY_PACKAGE_NAME = "KEY_PACKAGE_NAME"

        fun newIntent(context: Context, packageName: String): Intent {
            val intent = Intent(context, OverlayActivity::class.java)
            intent.putExtra(KEY_PACKAGE_NAME, packageName)
            return intent
        }
    }
}