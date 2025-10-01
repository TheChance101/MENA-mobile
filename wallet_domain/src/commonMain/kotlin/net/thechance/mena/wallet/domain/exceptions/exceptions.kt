package net.thechance.mena.wallet.domain.exceptions

open class WalletException(message: String = "Wallet Exception") : Exception(message)

class NoInternetException(message:  String = "No Internet") : WalletException(message)

class UnknownException(message:  String = "Unknown") : WalletException(message)

class NoTransactionsFoundException(message:  String = "No Transactions Found") : WalletException(message)
