package net.thechance.mena.faith.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import net.thechance.mena.faith.data.remote.model.PageResponse
import net.thechance.mena.faith.data.remote.model.bookmark.AddBookmarkRequest
import net.thechance.mena.faith.data.remote.model.bookmark.AyahBookmarkDto

interface BookmarkApiService {

    @POST("faith/ayah/bookmarks")
    suspend fun addBookmark(@Body request: AddBookmarkRequest): Response<Unit>

    @GET("faith/ayah/bookmarks")
    suspend fun getBookmarks(
        @Query("page") pageNumber: Int,
        @Query("size") pageSize: Int
    ): Response<PageResponse<AyahBookmarkDto>>

    @DELETE("faith/ayah/bookmarks/{id}")
    suspend fun deleteBookmark(@Path("id") bookmarkId: Int): Response<Unit>
}
