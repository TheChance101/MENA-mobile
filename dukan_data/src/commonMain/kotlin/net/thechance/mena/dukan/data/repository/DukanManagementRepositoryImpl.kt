package net.thechance.mena.dukan.data.repository

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.thechance.mena.dukan.data.dto.dukan.DukanActivationStatusResponse
import net.thechance.mena.dukan.data.dto.dukan.DukanCategoryResponse
import net.thechance.mena.dukan.data.dto.dukan.DukanColorsResponse
import net.thechance.mena.dukan.data.dto.dukan.DukanDetailsDto
import net.thechance.mena.dukan.data.dto.dukan.DukanNameResponse
import net.thechance.mena.dukan.data.dto.dukan.DukanStylesResponse
import net.thechance.mena.dukan.data.dto.dukan.MyDukanStatusDto
import net.thechance.mena.dukan.data.mapper.toActivationStatus
import net.thechance.mena.dukan.data.mapper.toCategoryList
import net.thechance.mena.dukan.data.mapper.toColorsList
import net.thechance.mena.dukan.data.mapper.toCreateDukanRequest
import net.thechance.mena.dukan.data.mapper.toDukan
import net.thechance.mena.dukan.data.mapper.toMyDukanStatus
import net.thechance.mena.dukan.data.util.constants.EndPoints.DUKAN_BASE_PATH
import net.thechance.mena.dukan.data.util.network.DukanApi
import net.thechance.mena.dukan.data.util.network.buildSinglePartFormData
import net.thechance.mena.dukan.data.util.network.safeApiCall
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.model.MyDukanStatus
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository

class DukanManagementRepositoryImpl(
    private val client: DukanApi,
) : DukanManagementRepository {
    override suspend fun createDukan(dukan: Dukan) {
        safeApiCall<Unit> {
            client.getClient().post(
                urlString = "$DUKAN_BASE_PATH/create"
            ) {
                contentType(ContentType.Application.Json)
                setBody(dukan.toCreateDukanRequest())
            }
        }
    }

    override suspend fun isDukanNameTaken(name: String): Boolean {
        return safeApiCall<DukanNameResponse> {
            client.getClient().get("$DUKAN_BASE_PATH/available") {
                parameter("name", name)
            }.body()
        }.available.not()
    }

    override suspend fun getMyDukanStatus(): MyDukanStatus {
        return safeApiCall<MyDukanStatusDto> {
            client.getClient().get(
                urlString = "$DUKAN_BASE_PATH/statues"
            )
        }.toMyDukanStatus()
    }

    override suspend fun getDukanDetailsByDukanId(dukanId: String): Dukan {
        return safeApiCall<DukanDetailsDto> {
            client.getClient().get("$DUKAN_BASE_PATH/$dukanId")
        }.toDukan()
    }

    override suspend fun getDukanStyles(): List<Dukan.Style> {
        return safeApiCall<DukanStylesResponse> {
            client.getClient().get(
                urlString = "$DUKAN_BASE_PATH/styles"
            )
        }.styles.map {
            Dukan.Style.valueOf(it)
        }
    }

    override suspend fun getDukanColors(): List<Color> {
        return safeApiCall<DukanColorsResponse> {
            client.getClient().get(
                urlString = "$DUKAN_BASE_PATH/colors"
            )
        }.colors.toColorsList()
    }

    override suspend fun getCategories(): List<Category> {
        return safeApiCall<DukanCategoryResponse> {
            client.getClient().get("$DUKAN_BASE_PATH/categories")
        }.categories.toCategoryList()
    }

    override suspend fun uploadDukanImage(
        fileName: String, fileBytes: ByteArray
    ): String {
        return safeApiCall {
            client.getClient().post("$DUKAN_BASE_PATH/image") {
                setBody(
                    buildSinglePartFormData(fileName, fileBytes, "file")
                )
            }
        }
    }

    override suspend fun updateFavoriteDukanStatus(dukanId: String) {
        return safeApiCall<Unit> {
            client.getClient().post("$DUKAN_BASE_PATH/$dukanId/toggle_favorite")
        }
    }

    override suspend fun getDukanActivationStatus(): Dukan.ActivationStatus {
        return safeApiCall<DukanActivationStatusResponse> {
            client.getClient().get("$DUKAN_BASE_PATH/activation-status")
        }.toActivationStatus()
    }
}