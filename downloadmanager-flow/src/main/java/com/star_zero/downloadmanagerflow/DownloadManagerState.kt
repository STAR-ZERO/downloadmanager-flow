package com.star_zero.downloadmanagerflow

data class DownloadManagerState(
    val id: Long,
    val status: Status,
    /**
     * totalBytes is set only when status is RUNNING.
     * It will be -1 until to determine total bytes.
     */
    val totalBytes: Int = 0,
    /** downloadedBytes is set only when status is RUNNING */
    val downloadedBytes: Int = 0,
    /** reason is set only when status is PAUSED or FAILED */
    val reason: Int = 0
) {
    enum class Status {
        PENDING,
        RUNNING,
        PAUSED,
        SUCCESS,
        FAILED,
        CANCELLED,
        UNKNOWN
    }
}
