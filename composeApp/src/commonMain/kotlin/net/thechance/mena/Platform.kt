package net.thechance.mena

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform