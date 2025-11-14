package net.thechance.mena.admin_panel.data.local

import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi

@Single
class InMemoryDukanDataStore {
    private var dukan: Dukan? = null

    fun storeDukan(value: Dukan) {
        dukan = value
    }

    @OptIn(ExperimentalUuidApi::class)
    fun getDukan(): Dukan? = dukan

    fun clear() {
        dukan = null
    }
}
