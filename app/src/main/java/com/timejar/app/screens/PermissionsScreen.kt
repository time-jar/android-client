package com.timejar.app.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import android.Manifest
import android.content.ComponentName
import android.content.Intent
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import com.timejar.app.R
import com.timejar.app.api.supabase.Supabase
import com.timejar.app.permissions.PermissionViewModel

class PermissionsScreen {

    // AppActivityAccessibilityService

    // val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    //  startActivity(intent)
}

@Composable
fun PermissionScreen(navController: NavController) {

    val viewModel: PermissionViewModel = viewModel()

    /* TODO: Enable accessibility service

    // Settings -> Accessibility -> [Your Service] and turn it on.

    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    startActivity(intent)

    */
    val context = LocalContext.current

    val isNotificationPermissionGranted = viewModel.isPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS)
    val isLocationPermissionGranted = viewModel.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val isActivityRecognitionPermissionGranted = viewModel.isPermissionGranted(context, Manifest.permission.ACTIVITY_RECOGNITION)
    val isAccessibilityPermissionGranted = viewModel.isAccessibilityPermissionGranted(context)

    val notificationButtonEnabled = remember { mutableStateOf(!isNotificationPermissionGranted)}
    val locationButtonEnabled = remember { mutableStateOf(!isLocationPermissionGranted)}
    val activityRecognitionButtonEnabled = remember { mutableStateOf(!isActivityRecognitionPermissionGranted)}
    val accessibilityButtonEnabled = remember { mutableStateOf(!isAccessibilityPermissionGranted)}

    // Notification permission
    val requestNotificationPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            viewModel.onNotificationPermissionResult(isGranted)
            notificationButtonEnabled.value = !isGranted
        }

    // Location permission
    val requestLocationPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            viewModel.onLocationPermissionResult(isGranted)
            locationButtonEnabled.value = !isGranted
        }

    // Activity Recognition permission
    val requestActivityRecognitionPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            viewModel.onActivityRecognitionPermissionResult(isGranted)
            activityRecognitionButtonEnabled.value = !isGranted
        }

    // Accessibility permission
    val requestAccessibilityPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            // Check if the accessibility service is enabled after returning from settings
            val isServiceEnabled = viewModel.isAccessibilityServiceEnabled(
                context,
                ComponentName(context, PermissionsScreen::class.java)
            )
            viewModel.onAccessibilityPermissionResult(isServiceEnabled)
            accessibilityButtonEnabled.value = !isServiceEnabled
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5E5E5))
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {


/*            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxSize()
            )
            {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu Icon",
                    tint = Color(0xFFABB3BB),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            navController.navigate("menu_screen")
                        }
                )
            }*/

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = stringResource(id = R.string.permissions),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 28.sp,
                color = Color(0xFF393F45)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.enable_permissions),
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                color = Color(0xFF393F45)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Column {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications Icon",
                            tint = Color(0xFF91B3B4)
                        )
                    }

                    Column {

                        Text(
                            textAlign = TextAlign.Left,
                            text = stringResource(id = R.string.notification_label),
                            color = Color(0xFFABB3BB),
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {

                        Button(
                            onClick = {
                                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            },
                            enabled = notificationButtonEnabled.value,
                            colors = ButtonDefaults.buttonColors(Color(0xFF91B3B4)),
                            modifier = Modifier.padding(horizontal = 5.dp)
                        ) {
                            Text("Enable")
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth(),
                    color = Color(0xFFABB3BB)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Column {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Icon",
                            tint = Color(0xFF91B3B4)
                        )
                    }

                    Column {

                        Text(
                            text = stringResource(id = R.string.location_label),
                            color = Color(0xFFABB3BB),
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {

                        Button(
                            onClick = {
                                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            },
                            enabled = locationButtonEnabled.value,
                            colors = ButtonDefaults.buttonColors(Color(0xFF91B3B4)),
                            modifier = Modifier.padding(horizontal = 5.dp)
                        ) {
                            Text("Enable")
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth(),
                    color = Color(0xFFABB3BB)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Person Icon",
                            tint = Color(0xFF91B3B4)
                        )
                    }

                    Column {

                        Text(
                            text = stringResource(id = R.string.activity_recognition_label),
                            color = Color(0xFFABB3BB),
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }


                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {

                        Button(
                            onClick = {
                                requestActivityRecognitionPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                            },
                            enabled = activityRecognitionButtonEnabled.value,
                            colors = ButtonDefaults.buttonColors(Color(0xFF91B3B4)),
                            modifier = Modifier.padding(horizontal = 5.dp)
                        ) {
                            Text("Enable")
                        }
                    }
                }

                Divider(
                    color = Color(0xFFABB3BB),
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            tint = Color(0xFF91B3B4)
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(id = R.string.accessibility_label),
                            color = Color(0xFFABB3BB),
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                // Start the accessibility settings activity
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                requestAccessibilityPermissionLauncher.launch(intent)
                            },
                            enabled = accessibilityButtonEnabled.value,
                            colors = ButtonDefaults.buttonColors(Color(0xFF91B3B4)),
                            modifier = Modifier.padding(horizontal = 5.dp)
                        ) {
                            Text("Go to Settings")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        onClick = {
                            if (Supabase.isLoggedIn()) {
                                navController.navigate("menu_screen")
                            } else {
                                navController.navigate("sign_up_screen")
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF91B3B4)),
                        modifier = Modifier
                            .height(60.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.done_label),
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 20.sp,
                            )
                        )
                    }
                }
            }
        }
    }
}



@Composable
@Preview
fun PermissionScreenPreview() {
    PermissionScreen(navController = rememberNavController())
}