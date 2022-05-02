package com.velvet.kamikazelock

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.lifecycle.lifecycleScope
import com.velvet.kamikazelock.data.cache.overlay.OverlayCacheContract
import com.velvet.kamikazelock.data.infra.ValidationStatus
import com.velvet.kamikazelock.ui.overlay.OverlayScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf


class OverlayActivity : ComponentActivity() {

    private val cache by inject<OverlayCacheContract.ActivityCache>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.d("OVER", "overlay compose set")
            MaterialTheme(colors = if (isSystemInDarkTheme()) darkColors() else lightColors()) {
                OverlayScreen(viewModel = getViewModel { parametersOf(intent.getStringExtra(KEY_PACKAGE_NAME)) })
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            cache.statusFlow.collect {
                when (it) {
                    ValidationStatus.SUCCESS_REAL_PASSWORD -> {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    ValidationStatus.SUCCESS_FAKE_PASSWORD -> {
                        uninstallApp()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    ValidationStatus.FAILURE_NO_PASSWORD_SET -> {
                        setResult(Activity.RESULT_OK)
                        finish()
                        Toast.makeText(applicationContext, R.string.password_not_created, Toast.LENGTH_LONG).show()
                    }
                    ValidationStatus.FAILURE_WRONG_PASSWORD -> {
                        Toast.makeText(applicationContext, R.string.wrong_password, Toast.LENGTH_LONG).show()
                    }
                    null -> {  }
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

    private fun uninstallApp() {
        this.packageManager.packageInstaller.uninstall(
            intent.getStringExtra(KEY_PACKAGE_NAME)!!,
            PendingIntent.getActivity(
                this, 0,
                Intent(applicationContext, applicationContext.javaClass),
                PendingIntent.FLAG_IMMUTABLE).intentSender)
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