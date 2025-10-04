package net.thechance.mena.trends.presentation.shared.util.video_util

import android.media.MediaDataSource

class ByteArrayMediaSource(
    private val videoData: ByteArray
): MediaDataSource() {

    override fun readAt(
        position: Long, buffer: ByteArray, offset: Int, size: Int
    ): Int {
        if (position >= videoData.size) return -1
        val length = minOf(size, videoData.size - position.toInt())
        System.arraycopy(videoData, position.toInt(), buffer, offset, length)
        return length
    }

    override fun getSize(): Long = videoData.size.toLong()

    override fun close() {}
}