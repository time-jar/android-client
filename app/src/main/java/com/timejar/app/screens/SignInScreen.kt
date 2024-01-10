package com.timejar.app.screens

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.timejar.app.R

import com.timejar.app.api.supabase.Supabase
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignInScreen(navController = rememberNavController())
        }
    }
}

@Composable
fun SignInScreen(navController: NavController) {
    val context = LocalContext.current
    var uiToastMessage by remember { mutableStateOf<String?>(null) }

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiToastMessage) {
        uiToastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val onSignInButtonClicked: (String, String) -> Unit = {userEmail, userPassword ->
        Log.i("LoginScreen", "Email: $userEmail, Password: $userPassword")

        val isFirstTimeConnected = isFirstTimeConnection(context)

        coroutineScope.launch {
            Supabase.login(userEmail, userPassword, onSuccess = {
                uiToastMessage = "SignInScreen onSignInButtonClicked SUCCESS"

                if (isFirstTimeConnected) {
                    navController.navigate("help_screen")
                    setLoggedInFlag(context)
                } else {
                    navController.navigate("menu_screen")
                }
            }, onFailure = {
                it.printStackTrace()
                val alert = "${it.message}"
                Log.e("SignInScreen onSignInButtonClicked", alert)
                uiToastMessage = alert
            })
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFE5E5E5))
        .imePadding()

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
                text = stringResource(id = R.string.sign_in_to_continue),
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                color = Color(0xFF393F45)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.label_email_address),
                        color = Color(0xFFABB3BB))
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
                modifier = Modifier.fillMaxWidth()
                    .onPreviewKeyEvent {
                        if (it.key == Key.Tab && it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN){
                            focusManager.moveFocus(FocusDirection.Down)
                            true
                        } else {
                            false
                        }
                    },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.label_password),
                        color = Color(0xFFABB3BB))
                },                placeholder = { Text(text = stringResource(id = R.string.hint_enter_password)) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF91B3B4),
                    unfocusedBorderColor = Color(0xFFABB3BB),
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Email Icon",
                        tint = Color(0xFF91B3B4)
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = {passwordVisible = !passwordVisible}){
                        Icon(
                            imageVector  = image, description,
                            tint = Color(0xFFABB3BB)
                        )                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .onPreviewKeyEvent {
                        if (it.key == Key.Tab && it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN){
                            focusManager.moveFocus(FocusDirection.Down)
                            true
                        } else {
                            false
                        }
                    },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    onSignInButtonClicked(email.text, password.text)
                },
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.do_not_have_account),
                    color = Color(0xFFABB3BB),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.sign_up),
                    color = Color(0xFF91B3B4),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("sign_up_screen")
                    }
                )
            }
        }
    }
}

// Function to check if it's the first time connection
fun isFirstTimeConnection(context: Context): Boolean {
    val preferences = context.getSharedPreferences("MyAppPreferences", MODE_PRIVATE)

    return preferences.getBoolean("isFirstTimeConnection", true)
}

// Function to set the flag indicating that the user has logged in
fun setLoggedInFlag(context: Context) {
    val preferences = context.getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
    preferences.edit().putBoolean("isFirstTimeConnection", false).apply()
}

@Composable
@Preview
fun SignInScreenPreview() {
    SignInScreen(navController = rememberNavController())
}