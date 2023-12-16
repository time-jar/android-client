package com.timejar.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.seanproctor.datatable.Table
import com.seanproctor.datatable.TableColumnDefinition
import com.timejar.app.R

@Composable
fun EventScreen(navController: NavController) {

    val numberOfEvents = 99
    var selectedRow by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFE5E5E5))
    ) {
        Column(
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

            Column(modifier = Modifier
                .background(Color(0xFF91B3B4), shape = RoundedCornerShape(25.dp))
                .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {

                Text(
                    text = stringResource(id = R.string.number_of_events),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 28.sp,
                    color = Color(0xFF393F45)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = numberOfEvents.toString(),
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 30.sp,
                    color = Color(0xFF393F45)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.last_10_events),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 28.sp,
                    color = Color(0xFF393F45)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Table(
                    columns = listOf(
                        TableColumnDefinition {
                            Text("Header A")
                        },
                        TableColumnDefinition {
                            Text("Header B")
                        },
                        TableColumnDefinition(Alignment.CenterEnd) {
                            Text("Header C")
                        },
                    )
                ) {
                    row {
                        onClick = { selectedRow = 0 }
                        cell { Text("Cell A1") }
                        cell { Text("Cell B1") }
                        cell { Text("Cell C1") }
                    }
                    row {
                        onClick = { selectedRow = 1 }
                        cell { Text("Cell A2") }
                        cell { Text("Cell B2") }
                        cell { Text("Cell C2") }
                    }
                }

            }

            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = AbsoluteAlignment.Right
            ) {
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(8.dp),
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

@Composable
@Preview
fun EventScreenPreview() {
    EventScreen(navController = rememberNavController())
}