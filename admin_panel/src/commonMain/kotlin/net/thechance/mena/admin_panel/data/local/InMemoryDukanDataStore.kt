package net.thechance.mena.admin_panel.data.local

import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import org.koin.core.annotation.Single

@Single
class InMemoryDukanDataStore {
    private var dukan: Dukan? = null

    fun storeDukan(value: Dukan) {
        dukan = value
    }

    fun getDukan(): Dukan? = dukan

    fun clear() {
        dukan = null
    }
}
