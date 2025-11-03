package net.thechance.mena.admin_panel.presentation.utils

fun getDisplayedPages(currentPage: Int, totalPages: Int): List<Int?> {
    if (totalPages <= 7) return (0 until totalPages).toList()

    val pages = mutableListOf<Int?>()

    val firstPage = 0
    val lastPage = totalPages - 1
    val windowStart = maxOf(1, currentPage - 2)
    val windowEnd = minOf(totalPages - 2, currentPage + 2)

    pages.add(firstPage)

    if (windowStart > firstPage + 1) pages.add(null)

    for (i in windowStart..windowEnd) pages.add(i)

    if (windowEnd < lastPage - 1) pages.add(null)

    pages.add(lastPage)

    return pages
}
