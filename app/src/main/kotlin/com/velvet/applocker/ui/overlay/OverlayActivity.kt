package com.velvet.applocker.ui.overlay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.velvet.applocker.R
import com.velvet.applocker.data.cache.overlay.OverlayCacheContract
import com.velvet.applocker.infra.ValidationStatus
import com.velvet.applocker.receiver.UnlockResultReceiver.Companion.APP_UNLOCKED
import com.velvet.applocker.setThemedContent
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.getViewModel

class OverlayActivity : ComponentActivity() {

    private val cache by inject<OverlayCacheContract.ActivityCache>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setThemedContent {
            OverlayScreen(viewModel = getViewModel())
        }
        lifecycleScope.launch {
            cache.status.receiveAsFlow().collect {
                when (it) {
                    ValidationStatus.SUCCESS -> {
                        sendBroadcast(Intent(APP_UNLOCKED))
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    ValidationStatus.FAILURE_NO_PASSWORD_SET -> {
                        Toast.makeText(
                            this@OverlayActivity,
                            R.string.password_not_created,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ValidationStatus.FAILURE_WRONG_PASSWORD -> {
                        Toast.makeText(
                            this@OverlayActivity,
                            R.string.wrong_password,
                            Toast.LENGTH_LONG
                        ).show()
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