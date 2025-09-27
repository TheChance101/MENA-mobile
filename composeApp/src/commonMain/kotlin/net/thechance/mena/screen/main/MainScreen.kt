package net.thechance.mena.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import net.thechance.mena.designsystem.presentation.component.bottomNavigation.BottomNavigationBar
import net.thechance.mena.designsystem.presentation.component.bottomNavigation.MenaTap
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.screen.main.tabs.DukanTab
import net.thechance.mena.screen.main.tabs.FaithTab
import net.thechance.mena.screen.main.tabs.HomeTab
import net.thechance.mena.screen.main.tabs.ProfileTab
import net.thechance.mena.screen.main.tabs.TrendsTab

class MainScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(HomeTab) {
            val tabNavigator = LocalTabNavigator.current
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        onItemClick = {
                            when (it.tab) {
                                MenaTap.HOME -> tabNavigator.current = HomeTab
                                MenaTap.DUKAN -> tabNavigator.current = DukanTab
                                MenaTap.TRENDS -> tabNavigator.current = TrendsTab
                                MenaTap.FAITH -> tabNavigator.current = FaithTab
                                MenaTap.PROFILE -> tabNavigator.current = ProfileTab
                            }
                        }
                    )
                }
            ) {
                Box(Modifier.fillMaxSize()) {
                    CurrentTab()
                }
            }
        }
    }
}