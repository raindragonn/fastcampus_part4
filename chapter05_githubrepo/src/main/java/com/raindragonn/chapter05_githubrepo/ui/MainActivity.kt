package com.raindragonn.chapter05_githubrepo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.raindragonn.chapter05_githubrepo.databinding.ActivityMainBinding
import com.raindragonn.chapter05_githubrepo.model.room.GithubRepoEntity
import com.raindragonn.chapter05_githubrepo.ui.adapter.RepositoryAdapter
import com.raindragonn.chapter05_githubrepo.util.GithubDatabase
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private val TAG: String = MainActivity::class.java.simpleName

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main
    private val mJob = Job()

    private lateinit var binding: ActivityMainBinding
    private val repositoryDao by lazy {
        GithubDatabase.getInstance(applicationContext).repositoryDao()
    }
    private val adapter: RepositoryAdapter by lazy { RepositoryAdapter(repositoryItemClickListener) }
    private val repositoryItemClickListener: (GithubRepoEntity) -> Unit = {
        startActivity(Intent(this, RepositoryActivity::class.java).apply {
            putExtra(RepositoryActivity.EXTRA_REPOSITORY_OWNER, it.owner.login)
            putExtra(RepositoryActivity.EXTRA_REPOSITORY_NAME, it.name)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

//        launch {
//            addMockData()
//            val githubRepositories = loadGithubRepositories()
//            withContext(coroutineContext) {
//                Log.d(TAG, "dev_log: ${githubRepositories.toString()}")
//            }
//        }
    }


    override fun onResume() {
        super.onResume()
        launch(coroutineContext) {
            loadLikedRepositoriesList()
        }
    }

    private suspend fun loadLikedRepositoriesList() = withContext(Dispatchers.IO) {
        val repoList = repositoryDao.getHistories()
        withContext(Dispatchers.Main) {
            setData(repoList)
        }
    }

    private fun setData(repoList: List<GithubRepoEntity>) = with(binding) {
        if (repoList.isEmpty()) {
            tvEmptyResult.isVisible = true
            rvLiked.isVisible = false
        } else {
            tvEmptyResult.isVisible = false
            rvLiked.isVisible = true
            adapter.submitList(repoList)
        }
    }

    private fun initViews() = with(binding) {
        rvLiked.adapter = adapter

        fabSearch.setOnClickListener { onStartSearchActivity() }


    }

    private fun onStartSearchActivity() {
        startActivity(Intent(this, SearchActivity::class.java))
    }

//    private suspend fun addMockData() = withContext(Dispatchers.IO) {
//        val mockData = (0..9).map {
//            GithubRepoEntity(
//                fullName = "name $it",
//                owner = GithubOwner(
//                    "login",
//                    "avatarUrl"
//                ),
//                name = "repo $it",
//                description = null,
//                language = null,
//                updatedAt = Date().toString(),
//                stargazersCount = it
//            )
//        }
//        repositoryDao.insertAll(mockData)
//    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}