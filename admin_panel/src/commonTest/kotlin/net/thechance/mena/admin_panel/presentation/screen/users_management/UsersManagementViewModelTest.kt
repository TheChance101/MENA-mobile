package net.thechance.mena.admin_panel.presentation.screen.users_management

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.PagedResult
import net.thechance.mena.admin_panel.domain.repository.user.UserRepository
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class UsersManagementViewModelTest {
    private val userRepository = mock<UserRepository>(mode = MockMode.autofill)
    private val stringProvider: StringProvider = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: UsersManagementViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        everySuspend {
            userRepository.getUsers(any())
        } returns usersList

        everySuspend {
            userRepository.updateUserStatus(any(), any())
        } returns Unit
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load users successfully on initialization`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.state.test {
            val currentState = awaitItem()
            assertFalse(currentState.isLoading)
            assertEquals(usersList.items.size, currentState.users.size)
            assertNull(currentState.errorState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update sort state when onSortClicked is called with USERNAME`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onSortClicked(UsersManagementScreenState.SortType.USERNAME)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(UsersManagementScreenState.SortType.USERNAME, currentState.sort.type)
                assertEquals(
                    UsersManagementScreenState.SortDirection.DESC,
                    currentState.sort.direction
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should toggle sort direction when onSortClicked is called twice with same type`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onSortClicked(UsersManagementScreenState.SortType.USERNAME)
            advanceUntilIdle()
            viewModel.onSortClicked(UsersManagementScreenState.SortType.USERNAME)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(UsersManagementScreenState.SortType.USERNAME, currentState.sort.type)
                assertEquals(
                    UsersManagementScreenState.SortDirection.ASC,
                    currentState.sort.direction
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should update sort state when onSortClicked is called with LAST_LOGIN_DATE`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onSortClicked(UsersManagementScreenState.SortType.LAST_LOGIN_DATE)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    UsersManagementScreenState.SortType.LAST_LOGIN_DATE,
                    currentState.sort.type
                )
                assertEquals(
                    UsersManagementScreenState.SortDirection.ASC,
                    currentState.sort.direction
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should update sort state when onSortClicked is called with LAST_VISIT_DATE`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onSortClicked(UsersManagementScreenState.SortType.LAST_VISIT_DATE)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    UsersManagementScreenState.SortType.LAST_VISIT_DATE,
                    currentState.sort.type
                )
                assertEquals(
                    UsersManagementScreenState.SortDirection.ASC,
                    currentState.sort.direction
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should reset to ASC when switching between different sort types`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onSortClicked(UsersManagementScreenState.SortType.USERNAME)
            advanceUntilIdle()
            viewModel.onSortClicked(UsersManagementScreenState.SortType.USERNAME)
            advanceUntilIdle()

            viewModel.onSortClicked(UsersManagementScreenState.SortType.LAST_LOGIN_DATE)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(
                    UsersManagementScreenState.SortType.LAST_LOGIN_DATE,
                    currentState.sort.type
                )
                assertEquals(
                    UsersManagementScreenState.SortDirection.ASC,
                    currentState.sort.direction
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should update query and reload users when onSearchQueryChanged is called`() =
        runTest(testDispatcher) {
            initViewModel()

            val searchQuery = " mr meawww"
            viewModel.onSearchQueryChanged(searchQuery)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(searchQuery, currentState.query)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should show block dialog when showBlockDialog is called`() =
        runTest(testDispatcher) {
            initViewModel()

            val userId = usersList.items[0].id
            viewModel.onBlockDialogClicked(userId)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertTrue(currentState.isBlockDialogShown)
                assertEquals(userId, currentState.selectedUserId)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should dismiss block dialog when onDismissBlockDialog is called`() =
        runTest(testDispatcher) {
            initViewModel()

            val userId = usersList.items[0].id
            viewModel.onBlockDialogClicked(userId)
            advanceUntilIdle()

            viewModel.onBlockDialogDismissed()
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertFalse(currentState.isBlockDialogShown)
                assertNull(currentState.selectedUserId)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should show block dialog when onToggleUserStatusClicked for active user`() =
        runTest(testDispatcher) {
            initViewModel()

            val activeUserId = usersList.items[0].id
            val userStatus = User.Status.ACTIVE
            viewModel.onToggleUserStatusClicked(activeUserId, userStatus)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertTrue(currentState.isBlockDialogShown)
                assertEquals(activeUserId, currentState.selectedUserId)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should activate user when onToggleUserStatusClicked for blocked user`() =
        runTest(testDispatcher) {
            initViewModel()

            val blockedUserId = usersList.items[1].id
            val userStatus = User.Status.BLOCKED
            viewModel.onToggleUserStatusClicked(blockedUserId, userStatus)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                val user = currentState.users.find { it.id == blockedUserId }
                assertNotNull(user)
                assertEquals(User.Status.ACTIVE, user.status)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should block user when onConfirmBlock is called`() = runTest(testDispatcher) {
        initViewModel()

        val userId = usersList.items[0].id
        viewModel.onBlockDialogClicked(userId)
        advanceUntilIdle()

        viewModel.onBlockConfirmed()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            val user = currentState.users.find { it.id == userId }
            assertNotNull(user)
            assertEquals(User.Status.BLOCKED, user.status)
            assertFalse(currentState.isBlockDialogShown)
            assertNull(currentState.selectedUserId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should clear query and reload users when onClearQueryClicked is called`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onSearchQueryChanged("something")
            advanceUntilIdle()

            viewModel.onClearQueryClicked()
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals("", currentState.query)
                assertTrue(currentState.users.isNotEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should update page and reload users when onPageChanged is called`() = runTest(testDispatcher) {
        initViewModel()

        val newPage = 3
        viewModel.onPageChanged(newPage)

        viewModel.state.test {
            val currentState = awaitItem()
            skipItems(1)
            advanceUntilIdle()
            assertEquals(newPage, currentState.pageInfo.page)
            assertTrue(currentState.users.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should reload users when onRetryClicked is called`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.onRetryClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertTrue(currentState.users.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun TestScope.initViewModel() {
        viewModel = UsersManagementViewModel(
            userRepository = userRepository,
            dispatcher = testDispatcher,
            stringProvider = stringProvider
        )
        advanceUntilIdle()
    }


    private companion object {
        val usersList =
            PagedResult(
                items = listOf(
                    User(
                        id = Uuid.random(),
                        firstName = "Farah",
                        lastName = "Khalil",
                        phoneNumber = "+970599123456",
                        lastLoginAt = LocalDateTime(2025, 10, 15, 23, 59, 59),
                        lastVisitAt = LocalDateTime(2025, 10, 20, 23, 59, 59),
                        status = User.Status.ACTIVE
                    ),
                    User(
                        id = Uuid.random(),
                        firstName = "Malak",
                        lastName = "Raef",
                        phoneNumber = "+970599654321",
                        lastLoginAt = LocalDateTime(2025, 9, 10, 23, 59, 59),
                        lastVisitAt = LocalDateTime(2025, 9, 25, 23, 59, 59),
                        status = User.Status.BLOCKED
                    ),
                    User(
                        id = Uuid.random(),
                        firstName = "Muhammed",
                        lastName = "Magdy",
                        phoneNumber = "+970599789012",
                        lastLoginAt = LocalDateTime(2025, 10, 1, 23, 59, 59),
                        lastVisitAt = LocalDateTime(2025, 10, 18, 23, 59, 59),
                        status = User.Status.ACTIVE
                    )
                ), totalPages = 10, currentPage = 0,
                totalElements = 25
            )
    }
}