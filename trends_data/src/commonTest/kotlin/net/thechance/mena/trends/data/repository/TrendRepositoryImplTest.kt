package net.thechance.mena.trends.data.repository

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isSuccess
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.trends.data.local.database.UserEngagement
import net.thechance.mena.trends.data.local.database.UserEngagementDao
import net.thechance.mena.trends.data.remote.mapper.toUserEngagement
import net.thechance.mena.trends.data.remote.repository.TrendsRepositoryImpl
import net.thechance.mena.trends.data.repository.util.VideoFileHandlerMock
import net.thechance.mena.trends.data.repository.util.addViewReelResponse
import net.thechance.mena.trends.data.repository.util.createReelsHttpClient
import net.thechance.mena.trends.data.repository.util.deleteReelResponse
import net.thechance.mena.trends.data.repository.util.fakeReelList
import net.thechance.mena.trends.data.repository.util.fakeReelUrls
import net.thechance.mena.trends.data.repository.util.getReelUrlsResponse
import net.thechance.mena.trends.data.repository.util.getReelsResponse
import net.thechance.mena.trends.data.repository.util.sendEngagements
import net.thechance.mena.trends.data.repository.util.toggleLikeReelResponse
import net.thechance.mena.trends.data.repository.util.updateReelResponse
import net.thechance.mena.trends.data.repository.util.uploadReelResponse
import net.thechance.mena.trends.data.repository.util.uploadReelThumbnailResponse
import net.thechance.mena.trends.domain.model.TrendUpdates
import net.thechance.mena.trends.domain.model.TrendWatchSession
import net.thechance.mena.trends.domain.model.UploadTrendProgress
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class TrendRepositoryImplTest {

    private var networkClient: HttpClient = createReelsHttpClient { getReelsResponse() }
    private var uploadClient: HttpClient = createReelsHttpClient { uploadReelResponse() }
    private val userRepository: UserRepository = mock()
    private val userEngagementDao: UserEngagementDao = mock()

    private val videoHandler = VideoFileHandlerMock()
    private var repository = TrendsRepositoryImpl(
        networkClient,
        uploadClient,
        videoHandler,
        userRepository,
        userEngagementDao
    )

    @Test
    fun `should return list of reels mapped to entity successfully when the user has already reels`() =
        runTest {
            repository = TrendsRepositoryImpl(
                networkClient,
                uploadClient,
                videoHandler,
                userRepository,
                userEngagementDao
            )

            val reels = repository.getAllCurrentUserTrends(pageNumber = 1)

            assertThat(reels).isEqualTo(fakeReelList)
        }

    @Test
    fun `should return feed reels mapped to entity successfully`() = runTest {
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        val result = repository.getFeedTrends(page = 1)

        assertThat(result).isEqualTo(fakeReelList)
    }

    @Test
    fun `should delete reel successfully when valid id provided`() = runTest {
        networkClient = createReelsHttpClient { deleteReelResponse() }
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )
        val result = runCatching { repository.deleteTrendById("1") }

        assertThat(result).isSuccess()
    }

    @Test
    fun `should update reel successfully`() = runTest {
        networkClient =
            createReelsHttpClient { updateReelResponse("1", "Updated description", listOf("cat1")) }
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        val result = runCatching {
            repository.updateTrendById(
                id = "1",
                description = "Updated description",
                categoryIds = listOf("cat1")
            )
        }

        assertThat(result).isSuccess()
    }

    @Test
    fun `uploadReel should call fileReader with correct file path`() = runTest {
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        repository.uploadTrend(FAKE_FILE_PATH, FAKE_SIZE)

        verifySuspend { videoHandler.readFile(FAKE_FILE_PATH) }
    }

    @Test
    fun `uploadReel should fail when request fails with request timeout`() = runTest {
        uploadClient = createReelsHttpClient {
            uploadReelResponse(status = HttpStatusCode.RequestTimeout)
        }
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        assertFails {
            repository.uploadTrend(FAKE_FILE_PATH, FAKE_SIZE)
        }
    }

    @Test
    fun `uploadReel should fail when server send error`() = runTest {
        uploadClient = createReelsHttpClient {
            uploadReelResponse(status = HttpStatusCode.InternalServerError)
        }

        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        assertFails {
            repository.uploadTrend(FAKE_FILE_PATH, FAKE_SIZE)
        }
    }

    @Test
    fun `should upload reel thumbnail successfully when valid id provided`() = runTest {
        uploadClient = createReelsHttpClient { uploadReelThumbnailResponse() }

        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        val result = runCatching {
            repository.uploadTrendThumbnail("1", FAKE_BYTES)
        }

        assertThat(result).isSuccess()
    }

    @Test
    fun `observeUploadReelProgress emits new value when uploadReel is called`() = runTest {
        uploadClient = createReelsHttpClient { uploadReelResponse() }
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        val emissions = mutableListOf(
            UploadTrendProgress(0, 0)
        )
        val job = launch {
            repository.observeUploadTrendProgress().collect { emissions.add(it) }
        }

        repository.uploadTrend(FAKE_FILE_PATH, FAKE_SIZE)
        job.cancel()

        assertThat(emissions.size).isGreaterThan(0)
    }

    @Test
    fun `upload reel thumbnail should fail when server send error`() = runTest {
        uploadClient = createReelsHttpClient {
            uploadReelThumbnailResponse(status = HttpStatusCode.InternalServerError)
        }
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        assertFails {
            repository.uploadTrendThumbnail("1", FAKE_BYTES)
        }
    }

    @Test
    fun `getReelDuration should call getDuration`() = runTest {
        repository.getTrendDuration(FAKE_FILE_PATH)

        verifySuspend { videoHandler.getDuration(FAKE_FILE_PATH) }
    }

    @Test
    fun `getReelDuration should return reel duration exactly as getDuration`() = runTest {
        val duration = repository.getTrendDuration(FAKE_FILE_PATH)

        assertThat(duration).isEqualTo(FAKE_DURATION)
    }

    @Test
    fun `getReelThumbnail should call extractVideoFrame`() = runTest {
        repository.extractTrendThumbnail(FAKE_FILE_PATH)

        verifySuspend { videoHandler.extractVideoFrame(FAKE_FILE_PATH) }
    }

    @Test
    fun `getReelDuration should return thumbnail bytearray when extractVideoFrame called`() =
        runTest {
            val thumbnailByteArray = repository.extractTrendThumbnail(FAKE_FILE_PATH)

            assertThat(thumbnailByteArray?.size).isEqualTo(2)
        }

    @Test
    fun `should throw exception when API fails in getAllReels`() = runTest {
        networkClient = createReelsHttpClient { throw Exception("Network Error") }
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        val result = runCatching { repository.getAllCurrentUserTrends(pageNumber = 1) }

        assertThat(result.isFailure).isEqualTo(true)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Network Error")
    }

    @Test
    fun `should toggle like when toggleLike called successfully`() = runTest {
        networkClient = createReelsHttpClient {
            toggleLikeReelResponse()
        }
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        val result = repository.addTrendLike(REEL_ID)

        assertThat(result).isEqualTo(fakeReelList.first())
    }

    @Test
    fun `should add view when addView called successfully`() = runTest {
        networkClient = createReelsHttpClient {
            addViewReelResponse()
        }

        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        val result = runCatching {
            repository.addTrendView(REEL_ID)
        }

        assertThat(result).isSuccess()
    }


    @Test
    fun `should get Reel Urls when getReelUrls called successfully`() = runTest {
        networkClient = createReelsHttpClient {
            getReelUrlsResponse()
        }

        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        val result = repository.getTrendUrls(REEL_ID)


        assertThat(result.videoUrl).isEqualTo(fakeReelUrls.videoPath)
        assertThat(result.thumbnailUrl).isEqualTo(fakeReelUrls.thumbnailPath)
    }

    @Test
    fun `should get Favorites Reel when getFavorites called successfully`() = runTest {
        networkClient = createReelsHttpClient {
            getReelsResponse()
        }
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        val result = repository.getFavoriteTrends(1)

        assertThat(result).isEqualTo(fakeReelList)

    }

    @Test
    fun `should return the specific favorite reel given a trendId`() = runTest {
            networkClient = createReelsHttpClient {
                getReelsResponse()
            }
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

            val result = repository.getFavoriteTrends(1, trendId = REEL_ID)

            assertThat(result).isEqualTo(fakeReelList)
    }

    @Test
    fun `should save user engagement for each reel in room database when insert function in dao called `() =
        runTest {
            everySuspend { userRepository.getUser() } returns user
            everySuspend { userEngagementDao.insertEngagement(any()) } returns 1
            repository.saveUserEngagementWithTrend(trendWatchSession)

            verifySuspend {
                userEngagementDao.insertEngagement(
                    trendWatchSession.toUserEngagement(
                        user.first().id.toString()
                    )
                )
            }
        }

    @Test
    fun `should call getUserEngagementsBeforeGivenTime when getFeedReels success`() = runTest {
        everySuspend { userRepository.getUser() } returns user

        networkClient = createReelsHttpClient { getReelsResponse() }
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        repository.getFeedTrends(page = 1)

        verifySuspend { userEngagementDao.getUserEngagementsBeforeGivenTime(any(), any()) }
    }

    @Test
    fun `should call sendEngagements if dao returns list with at least one item`() = runTest {
        everySuspend { userRepository.getUser() } returns user
        everySuspend { userEngagementDao.getUserEngagementsBeforeGivenTime(any(), any()) } returns fakeEngagements

        networkClient = createReelsHttpClient {
            getReelsResponse()
            sendEngagements()
        }
        repository = TrendsRepositoryImpl(
            networkClient,
            uploadClient,
            videoHandler,
            userRepository,
            userEngagementDao
        )

        repository.getFeedTrends(page = 1)

        verifySuspend { userEngagementDao.deleteUserEngagementsBeforeGivenTime(any(), any()) }
    }

    @Test
    fun `observeTrendUpdates should emit values when sendTrendUpdates is called`() = runTest {
        repository.observeTrendUpdates().test {
            repository.sendTrendUpdates(trendUpdates)
            assertThat(awaitItem()).isEqualTo(trendUpdates)
        }
    }

    private companion object {
        const val FAKE_SIZE = 1000L
        val FAKE_BYTES = ByteArray(FAKE_SIZE.toInt()) { 1 }
        const val FAKE_FILE_PATH = "path/to/file"
        const val FAKE_DURATION = 1000L
        const val REEL_ID = "12345"

        val trendWatchSession = TrendWatchSession(
            trendId = "1",
            watchStartTime = LocalDateTime(2024, 12, 10, 15, 30),
            watchEndTime = LocalDateTime(2024, 12, 10, 15, 31),
            videoDurationInMilliseconds = 60000,
            percentageOfVideoWatched = 100.0f
        )

        val user = flowOf(
            User(
                id = Uuid.random(),
                firstName = "Hend",
                lastName = "Sayed",
                profileImageUrl = "https://example.com/profile.jpg",
                username = "hend123",
                birthDate = LocalDate(1995, 11, 15),
                gender = Gender.FEMALE
            )
        )

        val fakeEngagements = listOf(
            UserEngagement(
                id = 1,
                userId = Uuid.random().toString(),
                trendId = "trend123",
                watchStartTime = LocalDateTime(2025, 11, 20, 1, 1),
                watchEndTime = LocalDateTime(2025, 11, 20, 1, 2),
                percentageOfVideoWatched = 100f,
                videoDurationInMilliseconds = 1000L
            )
        )

        val trendUpdates = TrendUpdates(
            trendId = "1",
            isLiked = true,
            likesCount = 5,
            viewsCount = 10,
            isDeleted = false
        )
    }
}