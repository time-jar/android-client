import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.timejar.app.map.MapsActivity

@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    // Your existing LaunchedEffect
        val intent = Intent(context, MapsActivity::class.java)
        context.startActivity(intent)
}


@Composable
@Preview
fun MapScreenPreview() {
    MapScreen(navController = rememberNavController())
}