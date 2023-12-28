package com.timejar.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
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
import com.timejar.app.R
import com.timejar.app.api.supabase.Supabase
import com.timejar.app.api.supabase.UserAppUsage

@Composable
fun EventScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    var numberOfEvents = 99
    var selectedRow by remember { mutableStateOf(-1) }
    var userAppUsageList by remember { mutableStateOf(emptyList<UserAppUsage>()) }

    val temp = UserAppUsage(
        id = 3,
        created_at = "",
        app_name = 345,
        user_id = "efsgxfgfhg",
        acceptance = 3,
        should_be_blocked = false,
        action = 2,
        location = 3,
        weekday = 5,
        time_of_day = "ded",
        app_usage_time = 33
    )

    userAppUsageList = listOf<UserAppUsage>(temp)

    Supabase.getAppActivityEvents(
        onSuccess = { data: List<UserAppUsage> ->
            userAppUsageList = data
        },
        onFailure = { error ->
            // Handle the error
        }
    )

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

                Column(/*modifier = Modifier.horizontalScroll(scrollState) */) {

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("ID", modifier = Modifier.weight(1f))
                        Text("App Name", modifier = Modifier.weight(1f))
                        Text("Created At", modifier = Modifier.weight(1f))
                        Text("User ID", modifier = Modifier.weight(1f))
                        Text("Acceptance", modifier = Modifier.weight(1f))
                        // Add other headers as needed
                    }

                    // Data rows
                    userAppUsageList.forEach { usage ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = usage.id.toString(), modifier = Modifier.weight(1f))
                            Text(text = usage.app_name.toString(), modifier = Modifier.weight(1f))
                            Text(text = usage.created_at.toString(), modifier = Modifier.weight(1f))
                            Text(text = usage.user_id, modifier = Modifier.weight(1f))
                            Text(text = usage.acceptance?.toString() ?: "N/A", modifier = Modifier.weight(1f))
                            // Add other data cells as needed
                        }
                    }

                    /*
                    Table(
                        columns = listOf(
                            TableColumnDefinition { Text("ID") },
                            TableColumnDefinition { Text("Created At") },
                            TableColumnDefinition { Text("App Name") },
                            TableColumnDefinition { Text("User ID") },
                            TableColumnDefinition { Text("Acceptance") },
                            TableColumnDefinition { Text("Should Be Blocked") },
                            TableColumnDefinition { Text("Action") },
                            TableColumnDefinition { Text("Location") },
                            TableColumnDefinition { Text("Weekday") },
                            TableColumnDefinition { Text("Time of Day") },
                            TableColumnDefinition { Text("App Usage Time") }
                        )
                    ) { userAppUsageList.forEachIndexed { index, usage ->
                            row {
                                onClick = { selectedRow = index }
                                cell { Text(usage.id.toString()) }
                                cell { Text(usage.created_at) }
                                cell { Text(usage.app_name.toString()) }
                                cell { Text(usage.user_id) }
                                cell { Text(usage.acceptance?.toString() ?: "N/A") }
                                cell { Text(usage.should_be_blocked?.toString() ?: "N/A") }
                                cell { Text(usage.action?.toString() ?: "N/A") }
                                cell { Text(usage.location.toString()) }
                                cell { Text(usage.weekday.toString()) }
                                cell { Text(usage.time_of_day) }
                                cell { Text(usage.app_usage_time?.toString() ?: "N/A") }
                            }
                        }
                    }
                    */
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = AbsoluteAlignment.Right
            ) {
                Button(
                    onClick = { Supabase.signOut(onSuccess = {}, onFailure = {})},
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

