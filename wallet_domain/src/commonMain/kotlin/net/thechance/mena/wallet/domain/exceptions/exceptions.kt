package net.thechance.mena.wallet.domain.exceptions

open class WalletException(message: String = "Wallet Exception") : Exception(message)

class NoInternetException(message: String = "No Internet") : WalletException(message)

class UnknownNetworkException(message: String = "Unknown") : WalletException(message)

class NoDataFoundException(message: String = "No Data Found") : WalletException(message)
class BlockedReceiverException(message: String = "Receiver is blocked") : WalletException(message)

