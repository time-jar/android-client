package com.timejar.app.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.timejar.app.R
import com.timejar.app.api.supabase.Supabase
import kotlinx.coroutines.launch

@Composable
fun Menu(navController: NavController) {
    val context = LocalContext.current
    var uiToastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiToastMessage) {
        uiToastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFE5E5E5))
    ) {

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu Icon",
                tint = Color(0xFFABB3BB),
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 80.dp)
            ) {

                Text(
                    text = stringResource(id = R.string.menu),
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 50.sp,
                    color = Color(0xFF393F45)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!Supabase.isLoggedIn()) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Button(
                            onClick = {
                                navController.navigate("login_screen")
                            },
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF91B3B4)),
                            modifier = Modifier
                                .height(60.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.login_label),
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 20.sp,
                                )
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth(),
                        color = Color(0xFFABB3BB)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        textAlign = TextAlign.Left,
                        text = stringResource(id = R.string.events_label),
                        color = Color(0xFFABB3BB),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                            .clickable {
                                navController.navigate("event_screen")
                            }
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth(),
                    color = Color(0xFFABB3BB)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        textAlign = TextAlign.Left,
                        text = stringResource(id = R.string.title_activity_maps),
                        color = Color(0xFFABB3BB),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                            .clickable {
                                navController.navigate("map_screen")
                            }
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth(),
                    color = Color(0xFFABB3BB)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        textAlign = TextAlign.Left,
                        text = stringResource(id = R.string.location_settings_label),
                        color = Color(0xFFABB3BB),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                            .clickable {
                                navController.navigate("location_settings_screen")
                            }
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth(),
                    color = Color(0xFFABB3BB)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        textAlign = TextAlign.Left,
                        text = stringResource(id = R.string.permissions),
                        color = Color(0xFFABB3BB),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                            .clickable {
                                navController.navigate("permissions_screen")
                            }
                    )
                }

                if (Supabase.isLoggedIn()) {

                    Spacer(modifier = Modifier.height(50.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    Supabase.signOut(onSuccess = {
                                        uiToastMessage = "Menu signOut SUCCESS"

                                        navController.navigate("login_screen")
                                    }, onFailure = {
                                        it.printStackTrace()
                                        val alert = "${it.message}"
                                        Log.e("Menu signOut", alert)
                                        uiToastMessage = alert
                                    })
                                }
                            },
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFFC66161)),
                            modifier = Modifier
                                .height(60.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.log_out),
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
}

@Composable
@Preview
fun MenuPreview() {
    Menu(navController = rememberNavController())
}