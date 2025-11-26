package net.thechance.mena.dukan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.collectLatest
import net.thechance.mena.dukan.presentation.screen.categoryDukans.CategoryDukansScreen
import net.thechance.mena.dukan.presentation.screen.checkout.CheckoutScreen
import net.thechance.mena.dukan.presentation.screen.createDukan.CreateDukanScreen
import net.thechance.mena.dukan.presentation.screen.createProduct.CreateProductScreen
import net.thechance.mena.dukan.presentation.screen.createShelf.CreateShelfScreen
import net.thechance.mena.dukan.presentation.screen.dukanCart.DukanCartScreen
import net.thechance.mena.dukan.presentation.screen.dukanCategories.DukanCategoriesScreen
import net.thechance.mena.dukan.presentation.screen.dukanDetails.DukanDetailsScreen
import net.thechance.mena.dukan.presentation.screen.dukanLocation.DukanLocationScreen
import net.thechance.mena.dukan.presentation.screen.editProduct.EditProductScreen
import net.thechance.mena.dukan.presentation.screen.main.MainScreen
import net.thechance.mena.dukan.presentation.screen.manageDukan.ManageDukanScreen
import net.thechance.mena.dukan.presentation.screen.manageShelf.ManageShelfScreen
import net.thechance.mena.dukan.presentation.screen.pendingDukan.PendingDukanScreen
import net.thechance.mena.dukan.presentation.screen.productDetails.ProductDetailsScreen
import net.thechance.mena.dukan.presentation.screen.search.SearchScreen
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsScreen
import net.thechance.mena.dukan.presentation.util.LocalImageLoader
import net.thechance.mena.dukan.presentation.util.provideImageLoader
import net.thechance.mena.wallet.api.WalletApi
import org.koin.compose.koinInject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import net.thechance.mena.identity.api.IdentityFeatureApi as IdentityApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun DukanNavHost(
    walletApi: WalletApi = koinInject(),
    identityApi: IdentityApi = koinInject(),
    updateBottomNavigationVisibility: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val coilImageLoader = provideImageLoader()

    LaunchedEffect(Unit) {
        navController.currentBackStack.collectLatest {
            if (navController.currentDestination?.route in routsWithBottomNavigation) {
                updateBottomNavigationVisibility(true)
            } else {
                updateBottomNavigationVisibility(false)
            }
        }
    }

    CompositionLocalProvider(
        LocalNavController provides navController, LocalImageLoader provides coilImageLoader
    ) {
        NavHost(
            navController = navController,
            startDestination = DukanRoute.MainScreenRoute,
        ) {
            composable<DukanRoute.MainScreenRoute> {
                MainScreen()
            }

            composable<DukanRoute.CreateDukanScreenRoute> {
                CreateDukanScreen()
            }

            composable<DukanRoute.CreateShelfScreenRoute> {
                CreateShelfScreen()
            }

            composable<DukanRoute.ManageDukanScreenRoute> {
                ManageDukanScreen()
            }

            composable<DukanRoute.PendingScreenRoute> { backStackEntry ->
                val route: DukanRoute.PendingScreenRoute =
                    backStackEntry.toRoute()
                PendingDukanScreen(
                    dukanName = route.dukanName,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<DukanRoute.ManageShelfScreenRoute> {
                ManageShelfScreen()
            }
            composable<DukanRoute.CreateProductScreenRoute> {
                CreateProductScreen()
            }
            composable<DukanRoute.EditProductScreenRoute> {
                EditProductScreen()
            }
            composable<DukanRoute.DukanDetails> {
                DukanDetailsScreen()
            }
            composable<DukanRoute.ShelfDetails> {
                ShelfDetailsScreen()
            }
            composable<DukanRoute.DukanCategoriesScreenRoute> {
                DukanCategoriesScreen()
            }
            composable<DukanRoute.DukansScreenRoute> {
                CategoryDukansScreen()
            }
            composable<DukanRoute.DukanCart> {
                DukanCartScreen()
            }
            composable<DukanRoute.ProductDetails> {
                ProductDetailsScreen()
            }
            composable<DukanRoute.CheckoutScreenRoute> {
                CheckoutScreen()
            }
            composable<DukanRoute.SearchScreenRoute> {
                SearchScreen()
            }
            composable<DukanRoute.AddressesRoute> {
                identityApi.NavigateToAddressesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<DukanRoute.ConfirmPaymentScreenRoute> { backStackEntry ->
                val route: DukanRoute.ConfirmPaymentScreenRoute = backStackEntry.toRoute()
                walletApi.ConfirmPaymentEntry(
                    transactionId = Uuid.parse(route.transactionId),
                    navigateBack = { navController.popBackStack(DukanRoute.DukanDetails(route.dukanId), false) },
                    updateBottomNavigationVisibility = {
                        true
                    }
                )
            }
            composable<DukanRoute.DukanLocation> {
                DukanLocationScreen()
            }
        }
    }
}

private val routsWithBottomNavigation = listOf(
    DukanRoute.MainScreenRoute::class.qualifiedName
)