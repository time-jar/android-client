package com.timejar.app.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.timejar.app.R
import com.timejar.app.api.supabase.Supabase
import java.util.Date

@Composable
fun SignUpScreen() {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    // TODO: var dateOfBirth by remember { mutableStateOf(Date("")) }
    // TODO: var sex by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }

    val onSignUpButtonClicked: (String, String, String, String, Date, String, String) -> Unit = { email, password, firstName, lastName, dateOfBirth, sex, confirmPassword  ->
        Log.i("LoginScreen", "FirstName: $firstName, LastName: $lastName, Email: $email, Password: $password, Confirm password: $confirmPassword")

        if (password.length < 8) {
            // loginAlert.value = "Password has to be at least 8 characters."
        }

        if (password != confirmPassword) {
            // loginAlert.value = "Passwords are not the same."
        }

        Supabase.signUp(email, password, firstName, lastName, dateOfBirth, sex, onSuccess = {
            // redirect to new screen
        }, onFailure = {
            it.printStackTrace()
            // loginAlert.value = "There was an error while logging in. Check your credentials and try again."
        })
    }

    val onSignInButtonClicked: () -> Unit = {}

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
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "Time Jar Logo",
                modifier = Modifier.padding(vertical = 24.dp)
                    .size(100.dp)
            )

            Text (
                text = stringResource(id = R.string.welcome_back),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 28.sp,
                color = Color(0xFF393F45)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.create_an_account),
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                color = Color(0xFF393F45)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.name_label),
                        color = Color(0xFFABB3BB)
                    )
                },
                placeholder = { Text(text = stringResource(id = R.string.hint_enter_name)) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF91B3B4),
                    unfocusedBorderColor = Color(0xFFABB3BB),
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Person Icon",
                        tint = Color(0xFF91B3B4)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.surname_label),
                        color = Color(0xFFABB3BB)
                    )
                },
                placeholder = { Text(text = stringResource(id = R.string.hint_enter_surname)) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF91B3B4),
                    unfocusedBorderColor = Color(0xFFABB3BB),
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Person Icon",
                        tint = Color(0xFF91B3B4)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.label_email_address),
                        color = Color(0xFFABB3BB)
                    )
                },
                placeholder = { Text(text = stringResource(id = R.string.hint_enter_email)) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF91B3B4),
                    unfocusedBorderColor = Color(0xFFABB3BB),
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = Color(0xFF91B3B4)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.label_password),
                        color = Color(0xFFABB3BB)
                    )
                },                placeholder = { Text(text = stringResource(id = R.string.hint_enter_password)) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF91B3B4),
                    unfocusedBorderColor = Color(0xFFABB3BB),
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Icon",
                        tint = Color(0xFF91B3B4)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.confirm_password_label),
                        color = Color(0xFFABB3BB)
                    )
                },
                placeholder = { Text(text = stringResource(id = R.string.hint_confirm_password)) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF91B3B4),
                    unfocusedBorderColor = Color(0xFFABB3BB),
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Icon",
                        tint = Color(0xFF91B3B4)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSignUpButtonClicked(firstName.text, lastName.text, email.text, password.text, dateOfBirth.text, sex.text, confirmPassword.text) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF91B3B4)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.confirm),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.already_have_account),
                    color = Color(0xFFABB3BB),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.sign_in),
                    color = Color(0xFF91B3B4),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignInButtonClicked() }
                )
            }
        }
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
}