package net.thechance.mena.dukan.data.util.network

import io.ktor.client.HttpClient

interface DukanApi {
    fun getClient(): HttpClient
    fun reset()
}