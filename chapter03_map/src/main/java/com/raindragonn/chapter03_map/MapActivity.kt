package com.raindragonn.chapter03_map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.raindragonn.chapter03_map.databinding.ActivityMapBinding
import com.raindragonn.chapter03_map.model.LocationLatLngEntity
import com.raindragonn.chapter03_map.model.SearchResultEntity
import com.raindragonn.chapter03_map.util.NetworkManager
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MapActivity : AppCompatActivity(), OnMapReadyCallback, CoroutineScope {

    private val locationManager: LocationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var searchResult: SearchResultEntity
    private lateinit var myLocationListener: LocationListener
    private lateinit var job: Job

    private var currentSelectMarker: Marker? = null
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    companion object {
        private const val EXTRA_SEARCH_RESULT = "EXTRA_SEARCH_RESULT"
        private const val CAMERA_ZOOM_LEVEL = 17f

        private const val PERMISSION_REQUEST_CODE = 1010

        fun startMap(context: Context, searchResultEntity: SearchResultEntity) {
            context.startActivity(Intent(context, MapActivity::class.java).apply {
                putExtra(EXTRA_SEARCH_RESULT, searchResultEntity)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        job = Job()

        getDataFromIntent()
        setUpGoogleMap()
        initViews()
    }

    private fun getDataFromIntent() {
        if (::searchResult.isInitialized.not()) {
            intent?.let {
                searchResult = it.getParcelableExtra<SearchResultEntity>(EXTRA_SEARCH_RESULT)
                    ?: throw Exception("데이터가 존재하지 않습니다.")
            }
        }
    }

    private fun setUpGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        currentSelectMarker = setUpMarker(searchResult)

        currentSelectMarker?.showInfoWindow()
    }

    private fun setUpMarker(searchResult: SearchResultEntity): Marker {
        val positionLatLng = LatLng(
            searchResult.locationLatLng.latitude.toDouble(),
            searchResult.locationLatLng.longitude.toDouble()
        )

        val markerOption = MarkerOptions().apply {
            position(positionLatLng)
            title(searchResult.name)
            snippet(searchResult.fullAddress)
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, CAMERA_ZOOM_LEVEL))

        return map.addMarker(markerOption)
    }

    private fun initViews() = with(binding) {
        fab.setOnClickListener {
            onFabClick()
        }
    }

    private fun onFabClick() {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnabled) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), PERMISSION_REQUEST_CODE
                )
            } else {
                requestLocationListener()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestLocationListener() {

        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = LocationListener { location ->
                val locationLatLngEntity = LocationLatLngEntity(
                    location.latitude.toFloat(),
                    location.longitude.toFloat()
                )
                onCurrentLocationChanged(locationLatLngEntity)
            }
        }


        val minTime: Long = 1500
        val minDistance = 100f
        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )
            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, myLocationListener
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationListener()
            } else {
                Toast.makeText(this, "권한을 받지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onCurrentLocationChanged(locationLatLngEntity: LocationLatLngEntity) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    locationLatLngEntity.latitude.toDouble(),
                    locationLatLngEntity.longitude.toDouble()
                ), CAMERA_ZOOM_LEVEL
            )
        )

        loadReverseGeoInformation(locationLatLngEntity)
        removeLocationListener()
    }

    private fun loadReverseGeoInformation(locationLatLngEntity: LocationLatLngEntity) {
        launch(coroutineContext) {
            try {
                val response = NetworkManager.apiService.getReverseGeoCode(
                    appKey = getString(R.string.tmap),
                    lat = locationLatLngEntity.latitude.toDouble(),
                    lon = locationLatLngEntity.longitude.toDouble()
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let { response ->
                        withContext(Dispatchers.Main) {
                            currentSelectMarker = setUpMarker(
                                SearchResultEntity(
                                    fullAddress = response.addressInfo.fullAddress ?: "주소정보 없음",
                                    name = "내 위치",
                                    locationLatLng = locationLatLngEntity
                                )
                            )
                            currentSelectMarker?.showInfoWindow()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MapActivity, "검색하는 과정에서 에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeLocationListener() {
        if (::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener)
        }
    }
}