package com.raindragonn.chapter05_githubrepo.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import com.raindragonn.chapter05_githubrepo.BuildConfig
import com.raindragonn.chapter05_githubrepo.databinding.ActivitySignInBinding
import com.raindragonn.chapter05_githubrepo.network.NetworkManager
import com.raindragonn.chapter05_githubrepo.util.AuthTokenProvider
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SignInActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mJob

    private val TAG: String = SignInActivity::class.java.simpleName
    private val authTokenProvider by lazy { AuthTokenProvider(this) }
    private lateinit var binding: ActivitySignInBinding
    private lateinit var mJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        mJob = Job()
        setContentView(binding.root)

        if (checkAuthCodeExist()) {
            launchMainActivity()
        } else {
            initViews()
        }
    }

    private fun initViews() = with(binding) {
        btnLogin.setOnClickListener { onLoginGithub() }
    }

    private fun checkAuthCodeExist(): Boolean = authTokenProvider.token.isNullOrEmpty().not()

    private fun launchMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
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
                getAccessToken(token).join()
                dismissProgress()
                if (checkAuthCodeExist()) {
                    launchMainActivity()
                }
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

    private fun getAccessToken(code: String) = launch(coroutineContext) {
        try {
            withContext(Dispatchers.IO) {
                val response = NetworkManager.authService.getAccessToken(
                    clientId = BuildConfig.GITHUB_CLIENT_ID,
                    clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
                    code = code
                )
                if (response.isSuccessful) {
                    val accessToken = response.body()?.accessToken ?: ""

                    if (accessToken.isNotEmpty()) {
                        withContext(coroutineContext) {
                            authTokenProvider.updateToken(accessToken)
                        }
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            "accessToken이 존재하지 않습니다.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this@SignInActivity,
                "로그인 과정에서 에러가 발생했습니다. : ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}