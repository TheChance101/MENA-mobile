package net.thechance.mena.identity.presentation.util

import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.Permissions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.DefinitionOptions
import org.koin.core.module.dsl.onOptions
import org.koin.core.qualifier.named

inline fun <reified T : Any, reified P1 : Any, reified P2 : Any, reified P3 : Any>
        Module.factoryOfOrNull(
    crossinline constructor: (P1, P2, P3?) -> T,
    noinline options: DefinitionOptions<T>? = null,
) {
    factory { constructor(get(), get(), getOrNull()) }.onOptions(options)
}
inline fun <reified T : Any, reified P1 : Any, reified P2 : Any, reified P3 : Any , reified P4 : Any>
        Module.factoryOfOrNull(
    crossinline constructor: (P1, P2, P3 , P4?) -> T,
    noinline options: DefinitionOptions<T>? = null,
) {
    factory { constructor(get(), get() , get(), getOrNull()) }.onOptions(options)
}

internal fun KoinComponent.getKoinPermissionController(permission: Permissions): PermissionController {
    val permissionController by inject<PermissionController>(named(permission.name))
    return permissionController
}