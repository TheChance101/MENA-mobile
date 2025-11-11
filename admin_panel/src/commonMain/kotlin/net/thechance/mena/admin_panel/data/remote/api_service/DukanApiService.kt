package net.thechance.mena.admin_panel.data.remote.api_service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import net.thechance.mena.admin_panel.data.remote.dto.DukanPagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.dukan.DukanDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ProductDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ShelfDto

interface DukanApiService {
    @GET(DUKAN_DETAILS_ENDPOINT)
    suspend fun getDukanDetails(
        @Path("dukanId") dukanId: String
    ): Response<DukanDto>

    @GET(DUKAN_SHELVES_ENDPOINT)
    suspend fun getDukanShelves(
        @Path("dukanId") dukanId: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<DukanPagedResponse<ShelfDto>>

    @GET(SHELF_PRODUCTS_ENDPOINT)
    suspend fun getShelfProducts(
        @Path("shelfId") shelfId: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<DukanPagedResponse<ProductDto>>

    private companion object {
        const val DUKAN_ADMIN_BASE = "dukan/admin"
        const val DUKAN_DETAILS_ENDPOINT = "$DUKAN_ADMIN_BASE/{dukanId}"
        const val DUKAN_SHELVES_ENDPOINT = "$DUKAN_ADMIN_BASE/shelf/{dukanId}"
        const val SHELF_PRODUCTS_ENDPOINT = "$DUKAN_ADMIN_BASE/shelf/{shelfId}/products"
    }
}