package com.raindragonn.chapter05_githubrepo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.raindragonn.chapter05_githubrepo.databinding.ActivitySearchBinding
import com.raindragonn.chapter05_githubrepo.model.room.GithubRepoEntity
import com.raindragonn.chapter05_githubrepo.network.NetworkManager
import com.raindragonn.chapter05_githubrepo.ui.adapter.RepositoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity(), CoroutineScope {
    private val TAG: String = SearchActivity::class.java.simpleName

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    private val mJob by lazy { Job() }
    private lateinit var binding: ActivitySearchBinding
    private val adapter: RepositoryAdapter by lazy { RepositoryAdapter(repositoryItemClickListener) }

    private val repositoryItemClickListener: (GithubRepoEntity) -> Unit = {
        startActivity(Intent(this, RepositoryActivity::class.java).apply {
            putExtra(RepositoryActivity.EXTRA_REPOSITORY_OWNER,it.owner.login)
            putExtra(RepositoryActivity.EXTRA_REPOSITORY_NAME,it.name)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initRecyclerView()
        initViews()
    }

    private fun initRecyclerView() = with(binding) {
        rvSearch.adapter = adapter
        rvSearch.layoutManager = LinearLayoutManager(this@SearchActivity)
    }

    private fun initViews() = with(binding) {
        tvEmptyResult.isVisible = false

        btnSearch.setOnClickListener {
            searchKeyword(etSearch.text.toString())
        }
    }

    private fun searchKeyword(keyword: String) = launch {
        val response = NetworkManager.apiService.searchRepositories(keyword)

        if (response.isSuccessful) {
            response.body()?.let {
                adapter.submitList(it.items)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}