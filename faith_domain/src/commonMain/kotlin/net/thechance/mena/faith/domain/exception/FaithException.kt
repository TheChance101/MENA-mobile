package net.thechance.mena.faith.domain.exception

sealed class FaithException : Throwable() {
    object UnknownException : FaithException()
    object NoInternetException : FaithException()
    object UnauthorizedException : FaithException()
    object NetworkException : FaithException()
}

