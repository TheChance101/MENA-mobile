package net.thechance.mena.faith.domain.exception

open class FaithException() : Throwable()
class UnknownException() : FaithException()
class NoInternetException() : FaithException()
class UnauthorizedException() : FaithException()
class NetworkException() : FaithException()

