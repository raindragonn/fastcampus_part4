package com.raindragonn.chapter06_imagesearch

import android.Manifest
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.raindragonn.chapter06_imagesearch.data.Repository
import com.raindragonn.chapter06_imagesearch.data.model.PhotoResponse
import com.raindragonn.chapter06_imagesearch.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1001
    }

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val photoAdapter: PhotoAdapter by lazy { PhotoAdapter(photoItemClickListener) }
    private val scope = MainScope()
    private val photoItemClickListener: (PhotoResponse) -> Unit
        get() = { photo ->
            checkPermissionAndDownload(photo)
        }

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

    private fun checkPermissionAndDownload(photo: PhotoResponse) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            showDownloadPhotoConfirmationDialog(photo)
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                showDownloadPhotoConfirmationDialog(photo)
            } else {
                requestWriteStoragePermission()
            }
        }
    }

    private fun showDownloadPhotoConfirmationDialog(photo: PhotoResponse) {
        AlertDialog.Builder(this)
            .setMessage("이 사진을 저장하시겠습니까?")
            .setPositiveButton("저장") { dialog, _ ->
                downloadPhoto(photoUrl = photo.urls?.full)
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->

                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun downloadPhoto(photoUrl: String?) {
        photoUrl ?: run {
            Toast.makeText(this@MainActivity, "유효하지 않은 이미지 입니다.", Toast.LENGTH_SHORT).show()
            return
        }


        Glide.with(this)
            .asBitmap()
            .load(photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(
                object : CustomTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        Snackbar.make(binding.root, "다운로드 중..", Snackbar.LENGTH_INDEFINITE).show()
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        saveBitmapToMediaStore(resource)

                        val wallpaperManager = WallpaperManager.getInstance(this@MainActivity)
                        val snackBar = Snackbar.make(
                            binding.root,
                            "다운로드 완료",
                            Snackbar.LENGTH_SHORT
                        )

                        if (wallpaperManager.isWallpaperSupported
                            && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                    && wallpaperManager.isSetWallpaperAllowed)
                        ) {
                            snackBar.setAction("배경 화면으로 저장") {
                                try {
                                    wallpaperManager.setBitmap(resource)
                                } catch (e: Exception) {
                                    Snackbar.make(binding.root, "배경화면 저장 실패", Snackbar.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            snackBar.duration = Snackbar.LENGTH_INDEFINITE
                        }
                        snackBar.show()
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Snackbar.make(binding.root, "다운로드 실패", Snackbar.LENGTH_SHORT).show()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                }
            )
    }

    private fun saveBitmapToMediaStore(bitmap: Bitmap) {
        val fileName = "${System.currentTimeMillis()}.jpg"
        val resolver = applicationContext.contentResolver
        val imageCollectionUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                저장을 하는동안 접근을 못하게함(파일이 커서 느려질수 있는경우)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollectionUri, imageDetails)

        imageUri ?: return

        resolver.openOutputStream(imageUri).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, imageDetails, null, null)
        }
    }

    private fun requestWriteStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val writeExternalStorageGranted =
            requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

}