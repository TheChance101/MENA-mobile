package net.thechance.mena.faith.presentation.util.provider

import org.jetbrains.compose.resources.StringResource

interface ResourceProvider {
    suspend fun getString(resource: StringResource, vararg args: Any): String
}

class DefaultResourceProvider : ResourceProvider {
    override suspend fun getString(resource: StringResource, vararg args: Any): String =
        org.jetbrains.compose.resources.getString(resource, *args)
}