package net.thechance.mena.faith.domain.exception

sealed class FaithException : Throwable() {
    data object UnknownException : FaithException()
    data object NoInternetException : FaithException()
    data object UnauthorizedException : FaithException()
    data object NetworkException : FaithException()
}

