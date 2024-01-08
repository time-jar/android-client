import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.timejar.app.sensing.geofence.MapsActivity

@Composable
fun MapScreen() {
    val context = LocalContext.current
        val intent = Intent(context, MapsActivity::class.java)
        context.startActivity(intent)
}


@Composable
@Preview
fun MapScreenPreview() {
    MapScreen()
}