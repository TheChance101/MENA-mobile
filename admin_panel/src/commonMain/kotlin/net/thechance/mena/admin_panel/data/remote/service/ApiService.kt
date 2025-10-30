package net.thechance.mena.admin_panel.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import net.thechance.mena.admin_panel.data.remote.dto.UserDto
import net.thechance.mena.admin_panel.domain.entity.user.User

interface ApiService {
    @GET("endpoint")
    suspend fun getAllUsers(): Response<List<User>>
}