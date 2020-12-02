package com.example.androidtweets

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {


    private lateinit var mMap: GoogleMap
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    private lateinit var srcLatLng: LatLng
    private lateinit var desLatLng: LatLng
    private lateinit var currLatLng: LatLng
    private lateinit var stepLatLng: ArrayList<LatLng>

    private lateinit var name: String

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        lat = intent.getDoubleExtra("LAT", 0.0)
        lon = intent.getDoubleExtra("LON", 0.0)
        name = intent.getStringExtra("ADDR")

        srcLatLng = LatLng(lat, lon)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        stepLatLng = ArrayList()

        loadingDialog = AlertDialog.Builder(this).create()

        loadingDialog.setMessage("loading please wait")


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(srcLatLng, 15f))
        mMap.setOnMapClickListener(this)

        chechPermission()
    }


    override fun onMapClick(p0: LatLng?) {
        mMap.clear()
        desLatLng = LatLng(p0!!.latitude, p0!!.longitude)
        mMap.addMarker(MarkerOptions().position(desLatLng))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(desLatLng, 15f))

    }


    fun onGo(view: View) {

        if (desLatLng != null) {

            mMap.clear()
            mMap.addMarker(MarkerOptions().position(desLatLng))
            mMap.addMarker(MarkerOptions().position(currLatLng))

            direct(currLatLng, desLatLng)


            val builder = LatLngBounds.Builder()
            builder.include(currLatLng).include(desLatLng)
            var lngBounds = builder.build()
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(lngBounds, 100))


        }


    }


    private fun getLoaction() {
        log("getLoaction")
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it ?: return@addOnSuccessListener
            currLatLng = LatLng(
                it.latitude.toDouble(),
                it.longitude.toDouble()
            )

            log("LATLON = " + currLatLng.toString())
        }

    }


    private fun chechPermission() {

        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val checkSelfPermission = ActivityCompat.checkSelfPermission(this, permission)
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {

            getLoaction()

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {


            } else {
                val permissions = arrayOf(permission)
                requestPermissions(permissions, 1)
            }

        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //是否获取到权限
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLoaction()
        }
    }


    private fun direct(orgin: LatLng, dest: LatLng) {

        loadingDialog.show()
        var geoApiContext: GeoApiContext = GeoApiContext.Builder()
            .apiKey(getString(R.string.google_maps_key))
            .build()

        var directionsApi: DirectionsApiRequest = DirectionsApi
            .newRequest(geoApiContext)
            .origin(formateLatLng(orgin))
            .destination(formateLatLng(dest))
            .mode(TravelMode.DRIVING)


        directionsApi.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onFailure(e: Throwable?) {
                log(e.toString())
                loadingDialog.dismiss()
                stepLatLng.clear()
            }

            override fun onResult(result: DirectionsResult?) {
                loadingDialog.dismiss()
                stepLatLng.clear()

                result!!.routes.forEach { directionsRoute ->
                    directionsRoute.legs.forEach { directionsLeg ->
                        directionsLeg.steps.forEach { directionsStep ->
                            log(directionsStep.startLocation.toString())
                            val latLngStart = LatLng(
                                directionsStep.startLocation.lat,
                                directionsStep.startLocation.lng
                            )
                            val latLngend = LatLng(
                                directionsStep.endLocation.lat,
                                directionsStep.endLocation.lng
                            )

                            stepLatLng.add(latLngStart)
                            stepLatLng.add(latLngend)

                            updateLines()
                        }
                    }

                }

            }

        })

    }


    fun updateLines() {

        runOnUiThread {
            run {
                var rectOptions = PolylineOptions()
                rectOptions.addAll(stepLatLng)
                mMap.addPolyline(rectOptions)
            }
        }

    }


    private fun formateLatLng(lat: LatLng): String {

        return lat.latitude.toString() + "," + lat.longitude.toString()

    }

    private fun log(msg: String) {

        Log.d("Map", msg)
    }
}
