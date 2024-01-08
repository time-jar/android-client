package com.timejar.app.screens

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.timejar.app.api.supabase.Supabase
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current
    var uiToastMessage by remember { mutableStateOf<String?>(null) }

    val radioOptions = listOf("Male", "Female")
    var sex by remember { mutableStateOf(radioOptions[0]) }
    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    var dateOfBirth by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }

    val formatter = SimpleDateFormat("dd.mm.yyyy", Locale.ENGLISH);

    LaunchedEffect(uiToastMessage) {
        uiToastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val onSignUpButtonClicked: (String, String, String, String, String, String, String) -> Unit =
        onSignUpButtonClicked@{ userSex, userFirstName, userLastName, userDateOfBirth, userEmail, userPassword, userConfirmPassword ->
            Log.i(
                "LoginScreen",
                "Sex: $userSex, FirstName: $userFirstName, LastName: $userLastName, Email: $userEmail, Password: $userPassword, Confirm password: $confirmPassword"
            )

            var parsedUserDateOfBirth: Date

            coroutineScope.launch {
                try {
                    parsedUserDateOfBirth = formatter.parse(userDateOfBirth)
                        ?: throw ParseException("Invalid date format, should be dd.mm.yyyy", 0)
                } catch (e: ParseException) {
                    val alert = "Please enter the date in dd/MM/yyyy format. ${e.message}"
                    Log.e("SignUpScreen onSignUpButtonClicked", alert)
                    uiToastMessage = alert

                    return@launch
                }

                if (userPassword.length < 8) {
                    val alert = "Password has to be at least 8 characters."
                    Log.e("SignUpScreen onSignUpButtonClicked", alert)
                    uiToastMessage = alert

                    return@launch
                }

                if (userPassword != userConfirmPassword) {
                    val alert = "Passwords are not the same."
                    Log.e("SignUpScreen onSignUpButtonClicked", alert)
                    uiToastMessage = alert

                    return@launch
                }

                Supabase.signUp(
                    userSex,
                    userFirstName,
                    userLastName,
                    parsedUserDateOfBirth,
                    userEmail,
                    userPassword,
                    onSuccess = {
                        uiToastMessage = "SignUpScreen onSignUpButtonClicked SUCCESS"

                        navController.navigate("menu_screen")
                    },
                    onFailure = {
                        it.printStackTrace()
                        val alert = "${it.message}"
                        Log.e("SignUpScreen onSignUpButtonClicked", alert)
                        uiToastMessage = alert
                    })
            }
        }

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

/*            Column (
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

            Spacer(modifier = Modifier.height(12.dp))*/

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

            Row(
                Modifier.fillMaxWidth(),
            ) {
                radioOptions.forEach { option ->
                    Row(
                        Modifier
                            .padding(end = 50.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == sex),
                            onClick = { sex = option },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF91B3B4),
                                unselectedColor = Color(0xFFABB3BB)
                            )
                        )
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            color = Color(0xFF393F45),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }

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

            Spacer(modifier = Modifier.height(2.dp))

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

            Spacer(modifier = Modifier.height(2.dp))

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.date_of_birth_label), // in "dd.mm.yyyy" format
                        color = Color(0xFFABB3BB)
                    )
                },
                placeholder = { Text(text = stringResource(id = R.string.hint_enter_date_of_birth)) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF91B3B4),
                    unfocusedBorderColor = Color(0xFFABB3BB),
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "DateRange Icon",
                        tint = Color(0xFF91B3B4)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(2.dp))

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

            Spacer(modifier = Modifier.height(2.dp))

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

            Spacer(modifier = Modifier.height(2.dp))

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
                onClick = {
                    onSignUpButtonClicked( sex, firstName.text, lastName.text, dateOfBirth.text, email.text, password.text, confirmPassword.text)
                },
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

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical=24.dp)

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
                    modifier = Modifier.clickable {
                        navController.navigate("login_screen")
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(navController = rememberNavController())
}