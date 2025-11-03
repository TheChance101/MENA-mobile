package net.thechance.mena.admin_panel.presentation.utils

fun getDisplayedPages(currentPage: Int, totalPages: Int): List<Int?> {
    val pages = mutableListOf<Int?>()

    if (totalPages <= 7) {
        for (i in 0 until totalPages) pages.add(i)
    } else {
        val start = maxOf(1, currentPage - 2)
        val end = minOf(totalPages - 2, currentPage + 2)

        pages.add(0)

        if (start > 1) pages.add(null)

        for (i in start..end) pages.add(i)

        if (end < totalPages - 2) pages.add(null)

        pages.add(totalPages - 1)
    }
    return pages
}
