package net.thechance.mena.admin_panel.presentation.utils

fun getDisplayedPages(currentPage: Int, totalPages: Int): List<PageItem> {
    if (totalPages <= 7) return (0 until totalPages).map { PageItem.Page(it) }

    val pages = mutableListOf<PageItem>()

    val firstPage = 0
    val lastPage = totalPages - 1
    val windowStart = maxOf(1, currentPage - 2)
    val windowEnd = minOf(totalPages - 2, currentPage + 2)

    pages.add(PageItem.Page(firstPage))

    if (windowStart > firstPage + 1) pages.add(PageItem.Ellipsis)

    for (i in windowStart..windowEnd) pages.add(PageItem.Page(i))

    if (windowEnd < lastPage - 1) pages.add(PageItem.Ellipsis)

    pages.add(PageItem.Page(lastPage))

    return pages
}

sealed class PageItem {
    data class Page(val number: Int) : PageItem()
    data object Ellipsis : PageItem()
}