package net.thechance.mena.faith.data.repository

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import net.thechance.mena.faith.data.remote.dto.bookmark.AyahBookmarkDto
import net.thechance.mena.faith.data.remote.dto.bookmark.AyahBookmarkResponse

internal val fakeBookmarkList = AyahBookmarkResponse(
    ayahBookmarks = listOf(
        AyahBookmarkDto(
            id = "1",
            surahId = 1,
            ayahNumber = 1,
            createdAt = "2023-01-01T00:00:00Z"
        ),
        AyahBookmarkDto(
            id = "2",
            surahId = 2,
            ayahNumber = 5,
            createdAt = "2023-02-01T00:00:00Z"
        )
    )
)

internal fun MockRequestHandleScope.getBookmarksResponse(
    body: AyahBookmarkResponse = fakeBookmarkList
) = respond(
    content = jsonSerialization.encodeToString(
        AyahBookmarkResponse.serializer(),
        body
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

internal fun MockRequestHandleScope.postBookmarkResponse(
    body: AyahBookmarkDto = fakeBookmarkList.ayahBookmarks.first()
) = respond(
    content = jsonSerialization.encodeToString(
        AyahBookmarkDto.serializer(),
        body
    ),
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)

internal fun MockRequestHandleScope.deleteBookmarkResponse() = respond(
    content = "{}",
    status = HttpStatusCode.OK,
    headers = jsonHeaders
)
