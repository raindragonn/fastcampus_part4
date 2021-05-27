package com.raindragonn.chapter06_imagesearch

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raindragonn.chapter06_imagesearch.data.Repository
import com.raindragonn.chapter06_imagesearch.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val photoAdapter: PhotoAdapter by lazy { PhotoAdapter() }
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        bindViews()
        fetchRandomPhotos()
    }

    private fun initViews() {
        with(binding) {
            rv.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            rv.adapter = photoAdapter
        }
    }

    private fun bindViews() {
        with(binding) {
            etSearch.setOnEditorActionListener { editText, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 키보드 내리기 밑 커서 깜빡임 지우기
                    currentFocus?.let { view ->
                        val inputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        inputMethodManager?.hideSoftInputFromWindow(
                            view.windowToken,
                            0
                        )
                        view.clearFocus()
                    }
                }

                fetchRandomPhotos(editText.text.toString())
                true
            }
            refreshLayout.setOnRefreshListener {
                fetchRandomPhotos(etSearch.text.toString())
            }
        }
    }

    private fun fetchRandomPhotos(query: String? = null) = scope.launch {
        try {
            Repository.getRandomPhotos(query)?.let { photos ->
                binding.tvErrorDescription.isVisible = false
                photoAdapter.setList(photos)
            }
            binding.rv.isVisible = true
        } catch (e: Exception) {
            binding.rv.isVisible = false
            binding.tvErrorDescription.isVisible = true
        } finally {
            binding.shimmerLayout.isVisible = false
            binding.refreshLayout.isRefreshing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

}