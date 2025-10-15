package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.trends.presentation.shared.base.ErrorState

internal data class UserReelState(
    val reels: Flow<PagingData<UserReelUiState>> = flowOf(),
    val fakeReels: List<UserReelUiState> = listOf(
        UserReelUiState(
            id = "1",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            description = "Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself. When one sunny day three rodents rudely harass him, something snaps... and the rabbit ain't no bunny anymore! In the typical cartoon tradition he prepares the nasty rodents a comical revenge.",
            likesCount = 120,
            viewsCount = 1500,
            username = "big_buck_bunny",
            profileImage = "https://cdn.pixabay.com/photo/2024/05/26/10/15/bird-8788491_1280.jpg",
            createdAt = "13 August 2025",
            isCurrentUserOwner = false
        ),
        UserReelUiState(
            id = "2",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            description = "The first Blender Open Movie from 2006",
            likesCount = 95,
            viewsCount = 1100,
            username = "elephant_dream",
            profileImage = "https://orange.blender.org/wp-content/themes/orange/images/media/gallery/ed_head.jpg",
            createdAt = "15 September 2025",
            isCurrentUserOwner = true
        ),
        UserReelUiState(
            id = "3",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            description = "Sintel is an independently produced short film, initiated by the Blender Foundation as a means to further improve and validate the free/open source 3D creation suite Blender.",
            likesCount = 200,
            viewsCount = 2500,
            username = "sintel_movie",
            profileImage = "https://static.vecteezy.com/system/resources/thumbnails/036/324/708/small/ai-generated-picture-of-a-tiger-walking-in-the-forest-photo.jpg",
            createdAt = "20 October 2025",
            isCurrentUserOwner = false
        )
    ),
    val isLoading: Boolean = false,
    val error: ErrorState? = null,
    val isConfirmationDialogVisible: Boolean = false,
    val isReelDeleted: Boolean? = null,
    val isDescriptionExpanded: Boolean = false,
)

data class UserReelUiState( //TODO get user info
    val id : String = "",
    val videoUrl: String= "",
    val description: String = "",
    val likesCount: Int = 0,
    val viewsCount: Int = 0,
    val username : String = "",
    val profileImage : String = "",
    val createdAt: String?= null,
    val isCurrentUserOwner: Boolean = false
)