package net.thechance.mena.faith.presentation.utils.extentions.paging

import app.cash.paging.compose.LazyPagingItems

fun <T : Any> LazyPagingItems<T>.isNotEmpty(): Boolean = itemSnapshotList.isNotEmpty()

fun <T : Any> LazyPagingItems<T>.isEmpty(): Boolean = itemSnapshotList.isEmpty()
