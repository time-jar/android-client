package com.timejar.app

import NavGraph
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.timejar.app.sensing.notification.createUserDecisionNotificationChannel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // createNotificationChannel for UserDecisionReceiver
        createUserDecisionNotificationChannel(this)

        setContent {
            MyAppTheme {
                NavGraph()
            }
        }
    }
}

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}
