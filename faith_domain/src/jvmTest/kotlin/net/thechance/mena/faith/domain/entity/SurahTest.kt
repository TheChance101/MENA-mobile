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
        assertEquals(
            FILE_HASH,
            actualHash,
            message = "Surah.kt file has been modified! actual hash:${actualHash}\n Please check the changes."
        )
    }

    private companion object {
        // NOTE: This MD5 hash is calculated from the Surah.kt file with LF line endings.
        // Do not change unless Surah.kt itself is intentionally modified.
        // If you run the test locally on Windows and see a different hash, make sure
        // the file uses LF line endings (see .gitattributes).
        const val FILE_HASH = "3ee45be83f266197549c3a15534bfe18"
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
