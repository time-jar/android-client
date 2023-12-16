package com.timejar.app.screens

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
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.timejar.app.R

class PermissionsScreen {

    // AppActivityAccessibilityService
    //
    // val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    //  startActivity(intent)
}

@Composable
fun PermissionScreen(navController: NavController) {
    var notificationSwitch by remember { mutableStateOf(true) }
    var locationSwitch by remember { mutableStateOf(true) }
    var activityRecognitionSwitch by remember { mutableStateOf(true) }

    Box(modifier = Modifier
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


            Column (
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text (
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

                        Switch(
                            checked = notificationSwitch,
                            onCheckedChange = {
                                notificationSwitch = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF91B3B4),
                                checkedBorderColor = Color(0xFF91B3B4),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFABB3BB),
                                uncheckedBorderColor = Color(0xFFABB3BB)
                            ),
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                    }
                }

                Divider (
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

                    Column (
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){

                        Switch(
                            checked = locationSwitch,
                            onCheckedChange = {
                                locationSwitch = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF91B3B4),
                                checkedBorderColor = Color(0xFF91B3B4),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFABB3BB),
                                uncheckedBorderColor = Color(0xFFABB3BB)
                            ),
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                    }
                }

                Divider (
                    color = Color(0xFFABB3BB),
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
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

                    Column (
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){

                        Switch(
                            checked = activityRecognitionSwitch,
                            onCheckedChange = {
                                activityRecognitionSwitch = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF91B3B4),
                                checkedBorderColor = Color(0xFF91B3B4),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFABB3BB),
                                uncheckedBorderColor = Color(0xFFABB3BB)
                            ),
                            modifier = Modifier.padding(horizontal = 5.dp)
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