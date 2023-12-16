package com.timejar.app.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.timejar.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSettings(navController: NavController) {
    var workLocation by remember { mutableStateOf(TextFieldValue("")) }
    var homeLocation by remember { mutableStateOf(TextFieldValue("")) }
    var schoolLocation by remember { mutableStateOf(TextFieldValue("")) }

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

            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "Time Jar Logo",
                modifier = Modifier.padding(vertical = 24.dp)
                    .size(100.dp)
            )

            Text (
                text = stringResource(id = R.string.location_label),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 28.sp,
                color = Color(0xFF393F45)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.enter_your_locations),
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                color = Color(0xFF393F45)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = homeLocation,
                onValueChange = { homeLocation = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.home_location_label),
                        color = Color(0xFFABB3BB)
                    )
                },
                placeholder = { Text(text = stringResource(id = R.string.hint_enter_home_location)) },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF91B3B4),
                    unfocusedBorderColor = Color(0xFFABB3BB)
                ),
                modifier = Modifier.fillMaxWidth().height(100.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = workLocation,
                onValueChange = { workLocation = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.work_location_label),
                        color = Color(0xFFABB3BB)
                    )
                },
                placeholder = { Text(text = stringResource(id = R.string.hint_enter_home_location)) },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF91B3B4),
                    unfocusedBorderColor = Color(0xFFABB3BB)
                ),
                modifier = Modifier.fillMaxWidth().height(100.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = schoolLocation,
                onValueChange = { schoolLocation = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.school_location_label),
                        color = Color(0xFFABB3BB)
                    )
                },
                placeholder = { Text(text = stringResource(id = R.string.hint_enter_school_location)) },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF91B3B4),
                    unfocusedBorderColor = Color(0xFFABB3BB)
                ),
                modifier = Modifier.fillMaxWidth().height(100.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF91B3B4)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.enter),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                    )
                )
            }
        }
    }
}

@Composable
@Preview
fun LocationSettingsPreview() {
    LocationSettings(navController = rememberNavController())
}