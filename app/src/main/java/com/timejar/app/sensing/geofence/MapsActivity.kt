package com.timejar.app.sensing.geofence

import NavGraph
import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.timejar.app.R
import java.util.concurrent.atomic.AtomicReference

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnCompleteListener<Void> {

    companion object {
        const val TAG = "MapsActivity"
        const val PERMISSION_REQUEST_BACKGROUND_LOCATION = 1
        const val PERMISSION_REQUEST_FINE_LOCATION = 2
    }

    private lateinit var mMap: GoogleMap
    private lateinit var mHome: Marker
    private lateinit var mWork: Marker
    private lateinit var mSchool: Marker

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mGeofencingClient: GeofencingClient

    private val mGeofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        val customActionBar = layoutInflater.inflate(R.layout.action_bar, null)
        supportActionBar?.customView = customActionBar
        supportActionBar?.setDisplayShowCustomEnabled(true)

        // Set up the NavController for the custom ActionBar
        val menuIcon = customActionBar.findViewById<ImageView>(R.id.menuIcon)
        menuIcon.setOnClickListener {
            supportActionBar?.hide()
            setContent {
                NavGraph()
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        mGeofencingClient = LocationServices.getGeofencingClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        sortPermissions()
    }

    @SuppressLint("MissingPermission")
    private fun whenMapReady() {

        val latitude: AtomicReference<Double>  =  AtomicReference(0.0)
        val longitude: AtomicReference<Double>  =  AtomicReference(0.0)

        mMap.isMyLocationEnabled = true

        val markerHome: MarkerOptions = MarkerOptions()
            .position(LatLng(latitude.get(), longitude.get()))
            .title(getString(R.string.map_marker_home))
            .draggable(true)
        markerHome.icon(bitmapDescriptorFromVector(this, R.drawable.ic_home_black_24dp))
        mHome = mMap.addMarker(markerHome)!!

        val markerWork: MarkerOptions = MarkerOptions()
            .position(LatLng(latitude.get(), longitude.get() + 0.005))
            .title(getString(R.string.map_marker_work))
            .draggable(true)
        markerWork.icon(bitmapDescriptorFromVector(this, R.drawable.ic_work_black_24dp))
        mWork = mMap.addMarker(markerWork)!!

        val markerSchool: MarkerOptions = MarkerOptions()
            .position(LatLng(latitude.get(), longitude.get() - 0.005))
            .title(getString(R.string.map_marker_school))
            .draggable(true)
        markerSchool.icon(bitmapDescriptorFromVector(this, R.drawable.ic_school_black_24dp))
        mSchool = mMap.addMarker(markerSchool)!!

        mFusedLocationProviderClient.lastLocation.addOnSuccessListener {

            if (it == null) {
                Log.d(TAG, "Last known location is null")
            } else {
                latitude.set(it.latitude)
                longitude.set(it.longitude)

                Log.d(TAG, "Last known location is $latitude , $longitude")

                if (mHome.position.latitude == 0.0) {
                    mHome.position = LatLng(latitude.get(), longitude.get())
                }

                if (mWork.position.latitude == 0.0) {
                    mWork.position = LatLng(latitude.get(), longitude.get() + 0.005)
                }

                if (mSchool.position.latitude == 0.0) {
                    mSchool.position = LatLng(latitude.get(), longitude.get() - 0.005)
                }

                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(latitude.get(), longitude.get())).zoom(14f).build()
                mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition))
            }
        }

        mMap.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener{
            override fun onMarkerDragEnd(marker: Marker) {

                val lat = marker.position.latitude
                val lon = marker.position.longitude


                if (marker.title.equals(getString(R.string.map_marker_home))) {
                    getGeofencingRequest(getString(R.string.map_marker_home), lat, lon)?.let {
                        addGeofence(
                            it
                        )
                    }
                } else if (marker.title.equals(getString(R.string.map_marker_work))) {
                    getGeofencingRequest(getString(R.string.map_marker_work), lat, lon)?.let {
                        addGeofence(
                            it
                        )
                    }
                } else if (marker.title.equals(getString(R.string.map_marker_school))) {
                    getGeofencingRequest(getString(R.string.map_marker_school), lat, lon)?.let {
                        addGeofence(
                            it
                        )
                    }
                }
            }

            override fun onMarkerDragStart(p0: Marker) {}

            override fun onMarkerDrag(p0: Marker) {}
        })

    }


    private fun getGeofencingRequest(
        markerType: String,
        latitude: Double,
        longitude: Double
    ): GeofencingRequest? {
        val geofenceTransition = when (markerType) {
            getString(R.string.map_marker_home) ->
                Geofence.GEOFENCE_TRANSITION_ENTER

            getString(R.string.map_marker_work) ->
                Geofence.GEOFENCE_TRANSITION_EXIT

            getString(R.string.map_marker_school) ->
                Geofence.GEOFENCE_TRANSITION_DWELL

            else -> {
                // Handle other cases or return null if the marker type is not recognized
                return null
            }
        }

        val geofenceRadius = when (markerType) {
            getString(R.string.map_marker_home),
            getString(R.string.map_marker_school) -> 200.0

            getString(R.string.map_marker_work) -> 300.0

            else -> {
                // Handle other cases or return null if the marker type is not recognized
                return null
            }
        }

        val geofence = Geofence.Builder()
            .setRequestId(markerType) // Use marker type as the geofence request ID
            .setCircularRegion(latitude, longitude, geofenceRadius.toFloat())
            .setTransitionTypes(geofenceTransition)
            .apply {
                if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                    setLoiteringDelay(1000)
                }
            }
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()


        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
    }


    @SuppressLint("MissingPermission")
    private fun addGeofence(request : GeofencingRequest){

        mGeofencingClient.addGeofences(request, mGeofencePendingIntent)
            .addOnCompleteListener(this)

    }


    override fun onComplete(task: Task<Void>) {
        if (task.isSuccessful) {
            Toast.makeText(this, "Setting geofence successful", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Setting geofence unsuccessful", Toast.LENGTH_LONG).show()
        }
    }

    // For drawing icons on the map
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    // For handling permissions
    private fun sortPermissions(){

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("This app needs background location access")
                    builder.setMessage(getString(R.string.rationale_location))
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener {
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            PERMISSION_REQUEST_BACKGROUND_LOCATION
                        )
                    }
                    builder.show()
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        val builder =
                            AlertDialog.Builder(this)
                        builder.setTitle("Functionality limited")
                        builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            // This will take the user to a page where they have to click twice to drill down to grant the permission
                            startActivity(intent)
                        }
                        builder.show()
                    }
                }
            } else {
                whenMapReady()
            }
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_FINE_LOCATION
                )
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Functionality limited")
                builder.setMessage("Since location access has not been granted, this app will not be able to discover geofences.  Please go to Settings -> Applications -> Permissions and grant location access to this app.")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    // This will take the user to a page where they have to click twice to drill down to grant the permission
                    startActivity(intent)
                }
                builder.show()
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_BACKGROUND_LOCATION) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                sortPermissions()
            }
            else {
                whenMapReady()
            }
        } else if (requestCode == PERMISSION_REQUEST_FINE_LOCATION) {
            sortPermissions()
        }
    }

}