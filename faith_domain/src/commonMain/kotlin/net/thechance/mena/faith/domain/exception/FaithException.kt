package net.thechance.mena.faith.domain.exception

sealed class FaithException : Throwable() {
    data object UnknownException : FaithException()
    data object NoInternetException : FaithException()
    data object UnauthorizedException : FaithException()
    data object NetworkException : FaithException()
    data object InvalidLatitudeException : FaithException()
    data object InvalidLongitudeException : FaithException()
    data object FailedToDownloadSurahException : FaithException()
    data object UrlCreationException : FaithException()
    data object FileCreationException : FaithException()
    data object InvalidCoordinates: FaithException()
}

