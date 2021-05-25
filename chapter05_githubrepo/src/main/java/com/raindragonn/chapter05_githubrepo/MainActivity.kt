package com.raindragonn.chapter05_githubrepo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import com.raindragonn.chapter05_githubrepo.databinding.ActivityMainBinding
import com.raindragonn.chapter05_githubrepo.network.NetworkManager
import com.raindragonn.chapter05_githubrepo.util.AuthTokenProvider
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mJob

    private val TAG: String = MainActivity::class.java.simpleName
    private val authTokenProvider by lazy { AuthTokenProvider(this) }
    private lateinit var binding: ActivityMainBinding
    private lateinit var mJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        mJob = Job()
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() = with(binding) {
        btnLogin.setOnClickListener { onLoginGithub() }
    }


    //todo https://github.com/login/oauth/authorize?client_id=
    private fun onLoginGithub() {
        val loginUri = Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .build()

        //커스텀탭으로 이동
        CustomTabsIntent.Builder().build().also {
            it.launchUrl(this, loginUri)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.data?.getQueryParameter("code")?.let { token ->
            //todo getAccessToken
            launch(coroutineContext) {
                showProgress()
                getAccessToken(token)
                dismissProgress()
            }
        }
    }

    private suspend fun showProgress() = withContext(coroutineContext) {
        with(binding) {
            btnLogin.isVisible = false
            pbLoading.isVisible = true
            tvLoading.isVisible = true
        }
    }

    private suspend fun dismissProgress() = withContext(coroutineContext) {
        with(binding) {
            btnLogin.isVisible = true
            pbLoading.isVisible = false
            tvLoading.isVisible = false
        }
    }

    private suspend fun getAccessToken(code: String) = withContext(Dispatchers.IO) {
        val response = NetworkManager.authApiService.getAccessToken(
            clientId = BuildConfig.GITHUB_CLIENT_ID,
            clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
            code = code
        )

        if (response.isSuccessful) {
            val accessToken = response.body()?.accessToken ?: ""

            if (accessToken.isNotEmpty()) {
                authTokenProvider.updateToken(accessToken)
            } else {
                Toast.makeText(this@MainActivity, "accessToken이 존재하지 않습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}