import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.timejar.app.screens.EventScreen
import com.timejar.app.screens.LocationSettings
import com.timejar.app.screens.LoginScreen
import com.timejar.app.screens.Menu
import com.timejar.app.screens.PermissionScreen
import com.timejar.app.screens.SignUpScreen

@Composable
fun NavGraph(){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login_screen")
    {
        composable("login_screen"){
            LoginScreen(navController = navController)
        }
        composable("sign_up_screen"){
            SignUpScreen(navController = navController)
        }
        composable("location_settings_screen"){
            LocationSettings(navController = navController)
        }
        composable("map_screen") {
            MapScreen(navController = navController)
        }
        composable("permissions_screen"){
            PermissionScreen(navController = navController)
        }
        composable("event_screen"){
            EventScreen(navController = navController)
        }
        composable("menu_screen"){
            Menu(navController = navController)
        }
    }
}