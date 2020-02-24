package com.star_zero.downloadmanagerflow.sample

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.star_zero.downloadmanagerflow.DownloadManagerState
import com.star_zero.downloadmanagerflow.downloadManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            lifecycleScope.launch {
                val request = DownloadManager.Request(Uri.parse(URL))
                    .setDestinationInExternalFilesDir(
                        this@MainActivity,
                        Environment.DIRECTORY_DOWNLOADS,
                        FILE_NAME
                    )

                Log.d(TAG, "Start")

                downloadManager(this@MainActivity, request).collect { state ->
                    when (state.status) {
                        DownloadManagerState.Status.RUNNING -> {
                            val downloadedBytes = state.downloadedBytes
                            val totalBytes = state.totalBytes

                            Log.d(TAG, "Running $downloadedBytes/$totalBytes")
                        }
                        DownloadManagerState.Status.SUCCESS -> {
                            // Download success
                            Log.d(TAG, "Success")
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        private const val URL = "TODO"
        private const val FILE_NAME = "TODO"
    }
}
