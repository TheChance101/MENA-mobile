package net.thechance.mena.admin_panel.domain.repository.deposit

interface DepositRepository {
    suspend fun deposit(phoneNumber :String , amount :Double)
}