package net.thechance.mena.wallet.presentation.screen.helper

import net.thechance.mena.wallet.presentation.utils.StringProvider
import org.jetbrains.compose.resources.StringResource

class FakeStringProvider : StringProvider {
    override suspend fun getString(resource: StringResource, vararg args: Any): String {
        return "Fake String ${args.joinToString()}"
    }
}