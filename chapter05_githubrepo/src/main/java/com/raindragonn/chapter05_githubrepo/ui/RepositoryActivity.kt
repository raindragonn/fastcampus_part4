package com.raindragonn.chapter05_githubrepo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.raindragonn.chapter05_githubrepo.R
import com.raindragonn.chapter05_githubrepo.databinding.ActivityRepositoryBinding
import com.raindragonn.chapter05_githubrepo.model.room.GithubRepoEntity
import com.raindragonn.chapter05_githubrepo.network.NetworkManager
import com.raindragonn.chapter05_githubrepo.util.GithubDatabase
import com.raindragonn.chapter05_githubrepo.util.loadCenterInside
import com.raindragonn.chapter05_githubrepo.util.toast
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RepositoryActivity : AppCompatActivity(), CoroutineScope {
    companion object {
        const val EXTRA_REPOSITORY_OWNER = "EXTRA_REPOSITORY_OWNER"
        const val EXTRA_REPOSITORY_NAME = "EXTRA_REPOSITORY_NAME"
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mJob
    private val mJob = Job()
    private lateinit var binding: ActivityRepositoryBinding

    private val repositoryDao by lazy {
        GithubDatabase.getInstance(applicationContext).repositoryDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repoOwner = intent.getStringExtra(EXTRA_REPOSITORY_OWNER) ?: run {
            toast("Repository Owner 이름이 없습니다.")
            finish()
            return
        }

        val repoName = intent.getStringExtra(EXTRA_REPOSITORY_NAME) ?: run {
            toast("Repository 이름이 없습니다.")
            finish()
            return
        }

        launch {
            loadRepository(repoOwner, repoName)?.let {
                setData(it)
            } ?: run {
                toast("repository 정보가 없습니다.")
                finish()
            }
        }
        showProgress()
    }

    private suspend fun loadRepository(
        repositoryOwner: String,
        repositoryName: String
    ): GithubRepoEntity? = withContext(coroutineContext) {
        var repositoryEntity: GithubRepoEntity? = null
        val response = NetworkManager.apiService.getRepository(
            ownerLogin = repositoryOwner,
            repoName = repositoryName
        )
        if (response.isSuccessful) {
            val body = response.body()
            body?.let { repo ->
                repositoryEntity = repo
            }
        }
        repositoryEntity
    }

    private fun setData(githubRepoEntity: GithubRepoEntity) = with(binding) {
        ivProfile.loadCenterInside(githubRepoEntity.owner.avatarUrl, 42f)
        tvOwnerNameAndRepoName.text = "${githubRepoEntity.owner.login}/${githubRepoEntity.name}"
        tvStargazersCount.text = githubRepoEntity.stargazersCount.toString()
        tvDescription.text = githubRepoEntity.description
        tvUpdateTime.text = githubRepoEntity.updatedAt

        githubRepoEntity.language?.let {
            tvLanguage.isVisible = true
            tvLanguage.text = it
        } ?: run {
            tvLanguage.isVisible = false
            tvLanguage.text = ""
        }
        setLikeState(githubRepoEntity)

        disMissProgress()
    }

    private fun setLikeState(githubRepoEntity: GithubRepoEntity) = launch {
        withContext(Dispatchers.IO) {
            val repository = repositoryDao.getHistory(githubRepoEntity.fullName)
            val isLike = repository != null

            withContext(Dispatchers.Main) {
                setLikeImages(isLike)
                binding.ivLike.setOnClickListener {
                    likeGitHubRepo(githubRepoEntity, isLike)
                }
            }
        }
    }

    private fun setLikeImages(like: Boolean) {
        binding.ivLike.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                if (like) {
                    R.drawable.ic_like
                } else {
                    R.drawable.ic_dislike
                }
            )
        )
    }

    private fun likeGitHubRepo(githubRepoEntity: GithubRepoEntity, isLike: Boolean) = launch {
        withContext(Dispatchers.IO) {
            if (isLike) {
                repositoryDao.remove(githubRepoEntity.fullName)
            } else {
                repositoryDao.insert(githubRepoEntity)
            }
            withContext(Dispatchers.Main) {
                setLikeImages(isLike.not())
            }
        }
    }

    private fun showProgress() = with(binding) { pbLoading.isVisible = true }
    private fun disMissProgress() = with(binding) { pbLoading.isVisible = false }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}