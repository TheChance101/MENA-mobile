package net.thechance.mena.trends.presentation.screen.category_publish.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.trends.presentation.navigation.Route
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

@Factory(binds = [CategoryPublishArgs::class])
class CategoryPublishArgsImpl(
    savedStateHandle: SavedStateHandle
) : CategoryPublishArgs {
    override val trendId: String = savedStateHandle.toRoute<Route.CategoriesPublish>().trendId
    override val description: String = savedStateHandle.toRoute<Route.CategoriesPublish>().description
}
