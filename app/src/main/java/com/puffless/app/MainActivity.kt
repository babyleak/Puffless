package com.puffless.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.rememberNavController
import com.puffless.app.ui.PuffNavGraph
import com.puffless.app.viewmodel.PuffViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    val viewModel: PuffViewModel = viewModel()
                    val navController = rememberNavController()
                    PuffNavGraph(navController, viewModel)
                }
            }
        }
    }
}