package com.puffless.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.rememberNavController
import com.puffless.app.ui.PuffNavGraph
import com.puffless.app.ui.theme.PufflessTheme
import com.puffless.app.utils.NotificationHelper
import com.puffless.app.viewmodel.PuffViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }

        if (NotificationHelper.shouldSendNotification(this)) {
            android.os.Handler(mainLooper).postDelayed({
                NotificationHelper.sendSimpleNotification(
                    this,
                    "Puffless",
                    "Не забудь: сколько затяжек осталось сегодня?"
                )
                NotificationHelper.markNotificationSent(this)
            }, 5000)
        }

        setContent {
            val viewModel: PuffViewModel = viewModel()

            PufflessTheme(themeSetting = viewModel.themeSetting) {
                Surface {
                    val navController = rememberNavController()
                    val lifecycleOwner = LocalLifecycleOwner.current

                    DisposableEffect(lifecycleOwner) {
                        lifecycleOwner.lifecycle.addObserver(viewModel)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(viewModel)
                        }
                    }

                    PuffNavGraph(navController, viewModel)
                }
            }
        }
    }
}