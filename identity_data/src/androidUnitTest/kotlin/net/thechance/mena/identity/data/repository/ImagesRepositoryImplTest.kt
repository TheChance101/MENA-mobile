package net.thechance.mena.identity.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.dataSource.local.memory.ImageCacheManager
import net.thechance.mena.identity.data.dataSource.local.storage.ImagesGalleryManager
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull


class ImagesRepositoryImplTest {

    private lateinit var imageCacheManager: ImageCacheManager
    private lateinit var imageGalleryManager: ImagesGalleryManager
    private lateinit var imagesRepository: ImagesRepositoryImpl

    @BeforeTest
    fun setup() {
        imageCacheManager = mockk()
        imageGalleryManager = mockk()
        imagesRepository = ImagesRepositoryImpl(imageCacheManager, imageGalleryManager)
    }

    @Test
    fun `getCachedImage should delegate call to imageCacheManager and return cached image if it exists`() {
        val key = "profile_image"
        val expectedBytes = byteArrayOf()
        every { imageCacheManager.getCachedImage(key) } returns expectedBytes

        val result = imagesRepository.getCachedImage(key)

        assert(result === expectedBytes)
    }

    @Test
    fun `getCachedImage should delegate call to imageCacheManager and return null if no image exists`() {
        val key = "profile_image"

        every { imageCacheManager.getCachedImage(key) } returns null

        val result = imagesRepository.getCachedImage(key)

        assertNull(result)
    }

    @Test
    fun `cacheImage should delegate call to imageCacheManager`() {
        val key = "profile_image"
        val imageBytes = byteArrayOf()
        every { imageCacheManager.cacheImage(key, imageBytes) } returns Unit

        imagesRepository.cacheImage(key, imageBytes)

        verify(exactly = 1) { imageCacheManager.cacheImage(key, imageBytes) }
    }

    @Test
    fun `removeCachedImage should delegate call to imageCacheManager`() {
        val key = "profile_image"

        every { imageCacheManager.removeCachedImage(key) } returns Unit

        imagesRepository.removeCachedImage(key)

        verify(exactly = 1) { imageCacheManager.removeCachedImage(key) }


    }

    @Test
    fun `saveImageToGallery should delegate call to imageGalleryManager`() = runTest {
        coEvery { imageGalleryManager.saveImage(any()) } just runs
        val byteArray = ByteArray(100)

        imagesRepository.saveImageToGallery(byteArray)

        coVerify(exactly = 1) {  imageGalleryManager.saveImage(byteArray) }
    }
}