package net.thechance.mena.trends.data.remote.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.asSource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.io.buffered
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.trends.data.di.DEFAULT_CLIENT_NAME
import net.thechance.mena.trends.data.di.UPLOAD_CLIENT_NAME
import net.thechance.mena.trends.data.local.database.UserEngagement
import net.thechance.mena.trends.data.local.database.UserEngagementDao
import net.thechance.mena.trends.data.remote.dto.TrendDto
import net.thechance.mena.trends.data.remote.dto.TrendPathUrlsDto
import net.thechance.mena.trends.data.remote.dto.RemotePaginationResponse
import net.thechance.mena.trends.data.remote.dto.SubmitWatchTimeRequest
import net.thechance.mena.trends.data.remote.dto.UpdateTrendRequestDTO
import net.thechance.mena.trends.data.remote.dto.UploadTrendResponse
import net.thechance.mena.trends.data.remote.mapper.toDto
import net.thechance.mena.trends.data.remote.mapper.toEntity
import net.thechance.mena.trends.data.remote.mapper.toTrendUrls
import net.thechance.mena.trends.data.util.NetworkEndpoint.FAVORITE_TREND_ENDPOINT
import net.thechance.mena.trends.data.remote.mapper.toUserEngagement
import net.thechance.mena.trends.data.util.NetworkEndpoint.LIKE_TREND_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkEndpoint.PAGE_PARAMETER
import net.thechance.mena.trends.data.util.NetworkEndpoint.PROFILE_TRENDS_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkEndpoint.TRENDS_FEED_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkEndpoint.REFRESH_TREND_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkEndpoint.THUMBNAIL_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkEndpoint.TRENDS_PATH
import net.thechance.mena.trends.data.util.NetworkEndpoint.VIEW_TREND_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkEndpoint.WATCH_TIME_ENDPOINT
import net.thechance.mena.trends.data.util.NetworkKeys.THUMBNAIL
import net.thechance.mena.trends.data.util.NetworkKeys.THUMBNAIL_MIME_TYPE
import net.thechance.mena.trends.data.util.NetworkKeys.VIDEO
import net.thechance.mena.trends.data.util.VideoFileHandler
import net.thechance.mena.trends.data.util.getLastMidnightTime
import net.thechance.mena.trends.data.util.observeUploading
import net.thechance.mena.trends.data.util.safeApiCall
import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.domain.model.TrendUpdates
import net.thechance.mena.trends.domain.model.TrendUrls
import net.thechance.mena.trends.domain.model.TrendWatchSession
import net.thechance.mena.trends.domain.model.UploadTrendProgress
import net.thechance.mena.trends.domain.repository.TrendsRepository
import org.koin.core.annotation.Named
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalUuidApi::class)
@Single(binds = [TrendsRepository::class])
internal class TrendsRepositoryImpl(
    @Named(DEFAULT_CLIENT_NAME) private val networkClient: HttpClient,
    @Named(UPLOAD_CLIENT_NAME) private val uploadClient: HttpClient,
    @Provided private val videoFileHandler: VideoFileHandler,
    @Provided private val userRepository: UserRepository,
    private val userEngagementDao: UserEngagementDao,
) : TrendsRepository {

    private val observableUploadingFlow: MutableSharedFlow<UploadTrendProgress> = MutableSharedFlow()
    private val trendsUpdates: MutableSharedFlow<TrendUpdates> = MutableSharedFlow()

    override suspend fun deleteTrendById(id: String) {
        safeApiCall<Unit> {
            networkClient.delete("$TRENDS_PATH/$id")
        }
    }

    override suspend fun getAllCurrentUserTrends(pageNumber: Int, trendId: String?): List<Trend> {
        val endpoint = trendId?.let {
            "$PROFILE_TRENDS_ENDPOINT/$it"
        } ?: PROFILE_TRENDS_ENDPOINT

        return safeApiCall<RemotePaginationResponse<TrendDto>> {
            networkClient.get(endpoint) {
                parameter(PAGE_PARAMETER, pageNumber)
            }
        }.results?.map { it.toEntity() }.orEmpty()
    }

    override suspend fun getFeedTrends(page: Int, trendId: String?): List<Trend> {
        val endpoint = trendId?.let {
            "$TRENDS_FEED_ENDPOINT/$it"
        } ?: TRENDS_FEED_ENDPOINT

        return safeApiCall<RemotePaginationResponse<TrendDto>> {
            networkClient.get(endpoint) {
                parameter(PAGE_PARAMETER, page)
            }
        }.results.orEmpty().map(TrendDto::toEntity).also {
            runCatching { sendUserEngagementsBeforeToday() }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun sendUserEngagementsBeforeToday() {
        val userId = userRepository.getUser().first()?.id.toString()
        userEngagementDao.getUserEngagementsBeforeGivenTime(userId, getLastMidnightTime())
            .takeIf { it.isNotEmpty() }
            ?.let { engagements ->
                sendUserEngagementsData(
                    userId = userId,
                    engagements = engagements
                )
            }
    }

    private suspend fun sendUserEngagementsData(
        userId: String,
        engagements: List<UserEngagement>
    ) {
        safeApiCall<Unit> {
            networkClient.post(WATCH_TIME_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(
                    SubmitWatchTimeRequest(
                        userId = userId,
                        watchTimes = engagements.map(UserEngagement::toDto)
                    )
                )
            }
        }.also {
            userEngagementDao.deleteUserEngagementsBeforeGivenTime(userId, getLastMidnightTime())
        }
    }

    override suspend fun updateTrendById(
        id: String,
        description: String,
        categoryIds: List<String>
    ) {
        val request = UpdateTrendRequestDTO(description, categoryIds)
        safeApiCall<Unit> {
            networkClient.patch("$TRENDS_PATH/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    override suspend fun uploadTrend(filePath: String, size: Long): String {
        return safeApiCall<UploadTrendResponse> {
            uploadClient.post(urlString = TRENDS_PATH) {
                setBody(
                    createRequestBody(
                        key = VIDEO,
                        mimeType = videoFileHandler.getMimeType(filePath),
                        input = InputProvider { videoFileHandler.readFile(filePath).buffered() }
                    )
                )
                observeUploading(
                    totalSize = size,
                    onProgress = observableUploadingFlow::emit
                )
            }
        }.trendId.orEmpty()
    }

    override fun observeUploadTrendProgress(): SharedFlow<UploadTrendProgress> {
        return observableUploadingFlow
    }

    override suspend fun uploadTrendThumbnail(trendId: String, thumbnail: ByteArray) {
        safeApiCall<Unit> {
            uploadClient.patch(urlString = "$TRENDS_PATH/$trendId/$THUMBNAIL_ENDPOINT") {
                setBody(
                    createRequestBody(
                        key = THUMBNAIL,
                        mimeType = THUMBNAIL_MIME_TYPE,
                        input = InputProvider { ByteReadChannel(thumbnail).asSource().buffered() }
                    )
                )
            }
        }
    }

    override suspend fun getTrendDuration(filePath: String): Long? {
        return videoFileHandler.getDuration(filePath)
    }

    override suspend fun extractTrendThumbnail(filePath: String, timeInMillis: Long): ByteArray? {
        return videoFileHandler.extractVideoFrame(filePath, timeInMillis)
    }

    override suspend fun addTrendLike(trendId: String): Trend {
        return safeApiCall<TrendDto> {
            networkClient.post(urlString = "$TRENDS_PATH/$trendId/$LIKE_TREND_ENDPOINT")
        }.toEntity()
    }

    override suspend fun removeTrendLike(trendId: String) {
        return safeApiCall {
            networkClient.delete(urlString = "$TRENDS_PATH/$trendId/$LIKE_TREND_ENDPOINT")
        }
    }

    override suspend fun addTrendView(trendId: String) {
        safeApiCall<Unit> {
            networkClient.post(urlString = "$TRENDS_PATH/$trendId/$VIEW_TREND_ENDPOINT")
        }
    }

    override suspend fun getTrendUrls(trendId: String): TrendUrls {
        return safeApiCall<TrendPathUrlsDto> {
            networkClient.get(urlString = "$TRENDS_PATH/$trendId/$REFRESH_TREND_ENDPOINT")
        }.toTrendUrls()
    }

    override suspend fun getFavoriteTrends(
        pageNumber: Int,
        trendId: String?
    ): List<Trend> {
        val endpoint = trendId?.let {
            "$FAVORITE_TREND_ENDPOINT/$it"
        } ?: FAVORITE_TREND_ENDPOINT

        return safeApiCall<RemotePaginationResponse<TrendDto>> {
            networkClient.get(endpoint) {
                parameter(PAGE_PARAMETER, pageNumber)
            }
        }.results.orEmpty().map { it.toEntity() }
    }

    private fun createRequestBody(
        key: String,
        mimeType: String,
        input: InputProvider
    ): MultiPartFormDataContent {
        return MultiPartFormDataContent(
            formData {
                append(
                    key = key,
                    value = input,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, mimeType)
                        append(HttpHeaders.ContentDisposition, "filename=\"\"")
                    }
                )
            }
        )
    }

    override suspend fun saveUserEngagementWithTrend(trendWatchSession: TrendWatchSession) {
        val userId = userRepository.getUser().first()?.id.toString()
        if (trendWatchSession.watchStartTime != null)
            userEngagementDao.insertEngagement(trendWatchSession.toUserEngagement(userId))
    }

    override suspend fun sendTrendUpdates(trendUpdates: TrendUpdates) {
        trendsUpdates.emit(trendUpdates)
    }

    override fun observeTrendUpdates(): SharedFlow<TrendUpdates> {
        return trendsUpdates.asSharedFlow()
    }
}