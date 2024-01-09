import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.timejar.app.api.supabase.Supabase
import com.timejar.app.screens.HelpMapScreen
import com.timejar.app.screens.SignInScreen
import com.timejar.app.screens.Menu
import com.timejar.app.screens.PermissionScreen
import com.timejar.app.screens.SignUpScreen

@Composable
fun NavGraph(){

    val navController = rememberNavController()
    val context = LocalContext.current

    val isFirstLaunch by remember { mutableStateOf(isFirstLaunch(context)) }

    val startDestination = when {
        isFirstLaunch -> "permissions_screen"
        Supabase.isLoggedIn() -> "menu_screen"
        else -> "login_screen"
    }
    NavHost(
        navController = navController,
        startDestination = startDestination)
    {
        composable("login_screen"){
            SignInScreen(navController = navController)
        }
        composable("sign_up_screen"){
            SignUpScreen(navController = navController)
        }
        composable("map_screen") {
            MapScreen()
        }
        composable("help_screen"){
            HelpMapScreen(navController = navController)
        }
        composable("permissions_screen"){
            PermissionScreen(navController = navController)
        }
        // composable("event_screen"){  // Disabled, since we are not using it
        //    EventScreen(navController = navController)
        // }
        composable("menu_screen"){
            Menu(navController = navController)
        }
    }
}

fun isFirstLaunch(context: Context): Boolean {
    val preferences = context.getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
    val isFirstLaunch = preferences.getBoolean("isFirstLaunch", true)
    if (isFirstLaunch) {
        preferences.edit().putBoolean("isFirstLaunch", false).apply()
    }
    return isFirstLaunch
}