package net.thechance.mena.trends.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.domain.model.TrendUrls
import net.thechance.mena.trends.domain.model.TrendWatchSession
import net.thechance.mena.trends.domain.model.UploadTrendProgress

interface TrendsRepository {
    suspend fun deleteTrendById(id: String)
    suspend fun getAllCurrentUserTrends(pageNumber: Int, trendId: String? = null): List<Trend>
    suspend fun updateTrendById(id: String, description: String, categoryIds: List<String>)
    suspend fun getFeedTrends(page: Int, trendId: String? = null): List<Trend>
    suspend fun uploadTrend(filePath: String, size: Long): String
    fun observeUploadTrendProgress(): SharedFlow<UploadTrendProgress>
    suspend fun uploadTrendThumbnail(trendId: String, thumbnail: ByteArray)
    suspend fun getTrendDuration(filePath: String): Long?
    suspend fun extractTrendThumbnail(filePath: String, timeInMillis: Long = 0L): ByteArray?
    suspend fun addTrendLike(trendId: String): Trend
    suspend fun removeTrendLike(trendId: String)
    suspend fun addTrendView(trendId: String)
    suspend fun getTrendUrls(trendId: String): TrendUrls
    suspend fun saveUserEngagementWithTrend(trendWatchSession: TrendWatchSession)
    suspend fun getFavoriteTrends(pageNumber: Int, trendId: String? = null): List<Trend>
}