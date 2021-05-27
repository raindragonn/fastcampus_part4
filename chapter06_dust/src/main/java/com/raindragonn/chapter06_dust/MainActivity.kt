package com.raindragonn.chapter06_dust

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.raindragonn.chapter06_dust.data.Repository
import com.raindragonn.chapter06_dust.data.model.airquality.AirQuality
import com.raindragonn.chapter06_dust.data.model.airquality.Grade
import com.raindragonn.chapter06_dust.data.model.monitoringstation.MonitoringStation
import com.raindragonn.chapter06_dust.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_LOCATION_PERMISSIONS_CODE = 100
        private const val REQUEST_BACKGROUND_ACCESS_LOCATION_PERMISSIONS_CODE = 101
    }

    private val scope = MainScope()
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var cancellationTokenSource: CancellationTokenSource? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bindViews()
        initVariables()
        requestLocationPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource?.cancel()
        scope.cancel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val locationPermissionGranted =
            requestCode == REQUEST_LOCATION_PERMISSIONS_CODE &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

        val backgroundLocationPermissionGranted =
            requestCode == REQUEST_BACKGROUND_ACCESS_LOCATION_PERMISSIONS_CODE &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (backgroundLocationPermissionGranted.not()) {
                requestBackgroundLocationPermissions()
            } else {
                fetchAirQualityData()
            }
        } else {
            if (locationPermissionGranted.not()) {
                toast("위치 권한이 필요합니다.")
                finish()
            } else {
                fetchAirQualityData()
            }
        }

    }

    private fun bindViews() = with(binding) {
        refresh.setOnRefreshListener {
            fetchAirQualityData()
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchAirQualityData() {
        cancellationTokenSource = CancellationTokenSource()

        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource!!.token
        ).addOnSuccessListener { location ->
            scope.launch {
                binding.tvErrorDescription.isVisible = false

                try {
                    val monitoringStation =
                        Repository.getNearByMonitoringStation(location.latitude, location.longitude)

                    val airQuality =
                        Repository.getLatestAirQualityData(monitoringStation!!.stationName!!)

                    displayAirQualityData(monitoringStation, airQuality!!)
                } catch (e: Exception) {
                    e.printStackTrace()

                    binding.tvErrorDescription.isVisible = true
                    binding.clContents.alpha = 0F
                } finally {
                    binding.pbLoading.isVisible = false
                    binding.refresh.isRefreshing = false
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayAirQualityData(
        monitoringStation: MonitoringStation,
        airequality: AirQuality
    ) {
        with(binding) {
            clContents.animate()
                .alpha(1F)
                .start()

            tvStationName.text = monitoringStation.stationName
            tvStationAddress.text = monitoringStation.addr
            tvStationAddress.isSelected = true

            (airequality.khaiGrade ?: Grade.UNKWON).let { grade ->
                clContents.setBackgroundResource(grade.colorResId)

                tvTotalGradeLabel.text = grade.label
                tvTotalGradeEmoji.text = grade.emoji
            }

            with(airequality) {
                tvFineDustInformation.text =
                    "미세먼지: $pm10Value ㎍/㎥ ${pm10Grade?.emoji ?: Grade.UNKWON.emoji}"

                tvUltraFineDustInformation.text =
                    "초미세먼지: $pm25Value ㎍/㎥ ${pm25Grade?.emoji ?: Grade.UNKWON.emoji}"

                with(itemSo2) {
                    tvLabel.text = "아황산가스"
                    tvGradeEmoji.text = "${so2Grade ?: Grade.UNKWON}"
                    tvValue.text = "$so2Value ppm "
                }

                with(itemCo) {
                    tvLabel.text = "일산화탄소"
                    tvGradeEmoji.text = "${coGrade ?: Grade.UNKWON}"
                    tvValue.text = "$coValue ppm"
                }

                with(itemO3) {
                    tvLabel.text = "오존"
                    tvGradeEmoji.text = "${o3Grade ?: Grade.UNKWON}"
                    tvValue.text = "$o3Value ppm"
                }

                with(itemNo2) {
                    tvLabel.text = "이산화질소"
                    tvGradeEmoji.text = "${no2Grade ?: Grade.UNKWON}"
                    tvValue.text = "$no2Value ppm"
                }

            }
        }
    }

    private fun initVariables() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun requestLocationPermissions() {
        // 포그라운드 위치정보 퍼미션과 백그라운드 위치정보 퍼미션을 동시에 받는다면 둘다 무시된다.

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSIONS_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundLocationPermissions() {
        // 포그라운드 위치정보 퍼미션과 백그라운드 위치정보 퍼미션을 동시에 받는다면 둘다 무시된다.

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            REQUEST_BACKGROUND_ACCESS_LOCATION_PERMISSIONS_CODE
        )
    }
}