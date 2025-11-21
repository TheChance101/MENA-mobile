package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.model.Reciter
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchRecitersUseCaseTest {

    private val useCase = SearchRecitersUseCase()


    @Test
    fun `invoke should return all reciters when query is blank`() {
        val result = useCase("", dummyReciters)
        assertEquals(dummyReciters.size, result.size)
        assertTrue(result.containsAll(dummyReciters))
    }

    @Test
    fun `invoke should filter reciters by exact name`() {
        val result = useCase("Abdul Basit Abdul Samad", dummyReciters)
        assertEquals(1, result.size)
        assertEquals("Abdul Basit Abdul Samad", result.first().name)
    }

    @Test
    fun `invoke should filter reciters by partial name case-insensitive`() {
        val result = useCase("abdul", dummyReciters)
        assertEquals(1, result.size)
        assertEquals("Abdul Basit Abdul Samad", result.first().name)
    }

    @Test
    fun `invoke should return empty list when no reciter matches query`() {
        val result = useCase("NonExistentName", dummyReciters)
        assertEquals(0, result.size)
    }

    @Test
    fun `invoke should return multiple reciters if multiple match query`() {
        val extendedReciters = dummyReciters + listOf(
            Reciter(4, "Abdul Rahman Al-Sudais", "عبد الرحمن السديس", "Murattal")
        )
        val result = useCase("Abdul", extendedReciters)
        assertEquals(2, result.size)
        val names = result.map { it.name }
        assertTrue(names.contains("Abdul Basit Abdul Samad"))
        assertTrue(names.contains("Abdul Rahman Al-Sudais"))
    }

    private val dummyReciters = listOf(
        Reciter(1, "Abdul Basit Abdul Samad", "عبد الباسط عبد الصمد", "Murattal"),
        Reciter(2, "Mahmoud Khalil Al-Hussary", "محمود خليل الحصري", "Murattal"),
        Reciter(3, "Mishary Rashid Alafasy", "مشاري بن راشد العفاسي", "Murattal")
    )

}
