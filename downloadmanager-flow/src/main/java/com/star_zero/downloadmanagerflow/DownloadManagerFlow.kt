package com.star_zero.downloadmanagerflow

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.getSystemService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
internal class DownloadManagerFlow(private val context: Context) {

    private val downloadManager: DownloadManager = context.getSystemService()!!

    fun execute(request: DownloadManager.Request) = callbackFlow<DownloadManagerState> {
        val id = downloadManager.enqueue(request)

        var completed = false
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)

                if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE && downloadId == id) {
                    getStatus(id, true)?.let {
                        offer(it)
                    }
                    completed = true
                    channel.close()
                }
            }
        }

        context.registerReceiver(
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )

        launch {
            // Cancel if parent scope is cancelled.
            while (true) {
                getStatus(id, false)?.let {
                    offer(it)
                }
                delay(STATUS_CHECK_INTERVAL_MILLI_SECONDS)
            }
        }

        awaitClose {
            if (!completed) {
                downloadManager.remove(id)
            }
            context.unregisterReceiver(receiver)
        }
    }

    private fun getStatus(
        id: Long,
        isCompleted: Boolean
    ): DownloadManagerState? {
        val query = DownloadManager.Query().setFilterById(id)
        return downloadManager.query(query).use { c ->
            if (c.moveToFirst()) {
                when (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_PENDING -> {
                        DownloadManagerState(id, DownloadManagerState.Status.PENDING)
                    }
                    DownloadManager.STATUS_RUNNING -> {
                        DownloadManagerState(
                            id,
                            DownloadManagerState.Status.RUNNING,
                            c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)),
                            c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        )
                    }
                    DownloadManager.STATUS_PAUSED -> {
                        DownloadManagerState(
                            id,
                            DownloadManagerState.Status.PAUSED,
                            reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))
                        )
                    }

                    DownloadManager.STATUS_SUCCESSFUL -> {
                        if (isCompleted) {
                            DownloadManagerState(id, DownloadManagerState.Status.SUCCESS)
                        } else {
                            null
                        }
                    }
                    DownloadManager.STATUS_FAILED -> {
                        if (isCompleted) {
                            DownloadManagerState(
                                id,
                                DownloadManagerState.Status.FAILED,
                                reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))
                            )
                        } else {
                            null
                        }
                    }
                    else -> {
                        DownloadManagerState(id, DownloadManagerState.Status.UNKNOWN)
                    }
                }
            } else {
                if (isCompleted) {
                    DownloadManagerState(id, DownloadManagerState.Status.CANCELLED)
                } else {
                    null
                }
            }
        }
    }

    companion object {
        private const val STATUS_CHECK_INTERVAL_MILLI_SECONDS = 1000L
    }
}

@ExperimentalCoroutinesApi
fun downloadManager(
    context: Context,
    request: DownloadManager.Request
): Flow<DownloadManagerState> {
    return DownloadManagerFlow(context).execute(request)
}
