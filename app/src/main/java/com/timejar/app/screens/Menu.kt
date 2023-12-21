package com.timejar.app.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun Menu(navController: NavController) {

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
            }
        }
    }
}

@Composable
@Preview
fun MenuPreview() {
    Menu(navController = rememberNavController())
}