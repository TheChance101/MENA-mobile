package net.thechance.mena.faith.domain.exception

internal open class FaithException() : Exception()
internal class UnknownException() : FaithException()
internal class NoInternetException() : FaithException()
internal class UnAuthorizedException() : FaithException()
internal class NetworkException() : FaithException()

