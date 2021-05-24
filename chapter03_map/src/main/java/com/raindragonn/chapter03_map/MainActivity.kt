package com.raindragonn.chapter03_map

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.raindragonn.chapter03_map.databinding.ActivityMainBinding
import com.raindragonn.chapter03_map.model.LocationLatLngEntity
import com.raindragonn.chapter03_map.model.SearchResultEntity
import com.raindragonn.chapter03_map.response.search.Pois
import com.raindragonn.chapter03_map.util.NetworkManager
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SearchRecyclerAdapter
    private val TAG: String = MainActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        job = Job()

        initAdapter()
        initViews()
        initData()
    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
        adapter.setSearchResultListener {
            Toast.makeText(
                this,
                "빌딩이름: ${it.name} 주소: ${it.fullAddress} 위도/경도 ${it.locationLatLng}",
                Toast.LENGTH_SHORT
            )
                .show()
            MapActivity.startMap(this, it)
        }
    }

    private fun initViews() = with(binding) {
        tvEmptyResult.isVisible = false
        rvSearch.adapter = adapter
        rvSearch.layoutManager = LinearLayoutManager(this@MainActivity)

        etSearch.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                val keyword = etSearch.text.toString()
                onSearchKeyword(keyword)
                return@setOnKeyListener true
            } else {
                return@setOnKeyListener false
            }
        }

        etSearch.setText("강남역")

        btnSearch.setOnClickListener {
            val keyword = etSearch.text.toString()
            onSearchKeyword(keyword)
        }
    }

    private fun onSearchKeyword(keyword: String) {
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO) {
                    val response = NetworkManager.apiService.getSearchLocation(
                        appKey = getString(R.string.tmap),
                        keyword = keyword
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo.pois)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@MainActivity,
                    "검색과정에 에러가 발생했습니다. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initData() {
        adapter.setList(emptyList())
    }

    private fun setData(data: Pois) {
        val dataList = data.poi.map {
            SearchResultEntity(
                fullAddress = it.getMainAddress(),
                name = it.name ?: "빌딩명 없음",
                locationLatLng = LocationLatLngEntity(
                    latitude = it.noorLat,
                    longitude = it.noorLon
                )
            )
        }

        adapter.setList(dataList)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}