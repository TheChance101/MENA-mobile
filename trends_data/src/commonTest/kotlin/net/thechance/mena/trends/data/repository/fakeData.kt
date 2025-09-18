package net.thechance.mena.trends.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thechance.mena.trends.data.dto.CategoryDto
import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.data.dto.RemoteResponse
import net.thechance.mena.trends.data.mapper.toEntity
import net.thechance.mena.trends.domain.entity.Reel


val fakeReelDtoList = RemoteResponse(
    pageNumber = 1,
    results = listOf(
        ReelDto(
            id = "1",
            reelImageUrl = "https://example.com/reel1.jpg",
            videoUrl = "https://example.com/reel1.mp4",
            description = "Funny reel about Kotlin Multiplatform",
            createdAt = "2025-09-16T15:06:57.507394",
            likesCount = 120,
            viewsCount = 1500,
            categories = listOf(
                CategoryDto(id = "1", name = "Comedy", emoji = "😂"),
                CategoryDto(id = "2", name = "Tech", emoji = "💻")
            )
        )
    ),
    totalResults = 1
)

val fakeReelList : List<Reel> = fakeReelDtoList.results.map { it.toEntity() }



fun createReelsHttpClient(
    reelsJson: String? = null
) = HttpClient(MockEngine { request ->
    when (request.url.encodedPath) {
        "/trends/reels" -> respond(
            content = reelsJson ?: """
                {
                  "pageNumber": "1",
                  "results": [
                    {
                      "reelId": "1",
                      "thumbnailUrl": "https://example.com/reel1.jpg",
                      "videoUrl": "https://example.com/reel1.mp4",
                      "description": "Funny reel about Kotlin Multiplatform",
                      "createdAt": "2025-09-16T15:06:57.507394",
                      "likesCount": 120,
                      "viewsCount": 1500,
                      "categories": [
                        {"id":"1","name":"Comedy","emoji":"😂"},
                        {"id":"2","name":"Tech","emoji":"💻"}
                      ]
                    }
                  ],
                  "totalResults": 1
                }
            """.trimIndent(),
            status = HttpStatusCode.OK,
            headers = headersOf("Content-Type", "application/json")
        )
        else -> respond(
            content = "",
            status = HttpStatusCode.BadRequest,
            headers = headersOf("Content-Type", "application/json")
        )
    }
}) {
    install(ContentNegotiation) {
        json( Json { ignoreUnknownKeys = true })
    }
    install(DefaultRequest) {
        contentType(ContentType.Application.Json)
    }
}

