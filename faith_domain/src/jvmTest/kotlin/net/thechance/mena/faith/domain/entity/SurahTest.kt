package net.thechance.mena.faith.domain.entity

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import kotlin.test.Test
import kotlin.test.assertEquals

class SurahTest {

    @Test
    fun `verify that Surah data class has not been modified`() {
        val actualHash = calculateFileMd5Hash(File(SURAH_FILE_PATH))
        assertEquals(FILE_HASH, actualHash, "Surah.kt file has been modified!")
    }

    private companion object {
        const val FILE_HASH = "9d8cb4694b0e7e74899c8d62a5cedc97"
        const val SURAH_FILE_PATH =
            "src/commonMain/kotlin/net/thechance/mena/faith/domain/entity/Surah.kt"
    }
}

private fun calculateFileMd5Hash(file: File): String {
    val buffer = ByteArray(4 * 1024)
    val md = MessageDigest.getInstance("MD5")
    FileInputStream(file).use { fis ->
        var bytesRead: Int
        while (fis.read(buffer).also { bytesRead = it } != -1) {
            md.update(buffer, 0, bytesRead)
        }
    }
    return md.digest().joinToString("") { "%02x".format(it) }
}
