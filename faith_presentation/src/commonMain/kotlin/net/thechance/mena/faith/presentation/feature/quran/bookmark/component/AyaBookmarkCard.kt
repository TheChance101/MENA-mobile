package net.thechance.mena.faith.presentation.feature.quran.bookmark.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDirection
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.aya
import mena.faith_presentation.generated.resources.history
import mena.faith_presentation.generated.resources.time_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.components.DotSeparator
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.designSystem.theme.quran
import net.thechance.mena.faith.presentation.feature.quran.bookmark.TimeAgo
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.getTimeAgo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AyaBookmarkCard(
    surahName: String,
    ayaNumber: Int,
    createdAt: TimeAgo,
    ayaText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().background(
            color = Theme.colorScheme.background.surfaceLow,
            shape = RoundedCornerShape(Theme.radius.md)
        ).padding(Theme.spacing._12),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        BookmarkHeader(
            surahName = surahName,
            ayaNumber = ayaNumber,
            createdAt = createdAt.getTimeAgo()
        )

        Text(
            text = ayaText,
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.quran.medium.copy(
                textDirection = TextDirection.ContentOrRtl
            )
        )
    }
}

@Composable
private fun BookmarkHeader(
    surahName: String,
    ayaNumber: Int,
    createdAt: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SurahAndAyaInfo(
            surahName = surahName,
            ayaNumber = ayaNumber
        )
        TimeInfo(createdAt = createdAt)
    }
}

@Composable
private fun SurahAndAyaInfo(
    surahName: String,
    ayaNumber: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = surahName,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.medium,
        )

        DotSeparator(modifier = Modifier.padding(horizontal = Theme.spacing._8))

        Text(
            text = stringResource(Res.string.aya, ayaNumber),
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.medium,
        )
    }
}

@Composable
private fun TimeInfo(createdAt: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(Res.drawable.history),
            contentDescription = stringResource(Res.string.time_icon),
            tint = Theme.colorScheme.shadeSecondary,
        )
        Text(
            text = createdAt,
            color = Theme.colorScheme.shadeSecondary,
            modifier = Modifier.padding(start = Theme.spacing._2),
            style = Theme.typography.label.small
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            AyaBookmarkCard(
                surahName = "Al-Maidah",
                ayaNumber = 3,
                createdAt = TimeAgo(),
                ayaText = "حُرِّمَتْ عَلَيْكُمُ الْمَيْتَةُ وَالدَّمُ وَلَحْمُ الْخِنْزِيرِ وَمَا أُهِلَّ بِهِ لِغَيْرِ اللَّهِ وَالْمُنْخَنِقَةُ وَالْمَوْقُوذَةُ وَالْمُتَرَدِّيَةُ وَالنَّطِيحَةُ وَمَا أَكَلَ السَّبُعُ إِلَّا مَا ذَكَّيْتُمْ وَمَا ذُبِحَ عَلَى النُّصُبِ وَأَن تَسْتَقْسِمُوا بِالْأَزْلَامِ ۚ ذَٰلِكُمْ فِسْقٌ ۗ الْيَوْمَ يَئِسَ الَّذِينَ كَفَرُوا مِن دِينِكُمْ فَلَا تَخْشَوْهُمْ وَاخْشَوْنِ ۚ الْيَوْمَ أَكْمَلْتُ لَكُمْ دِينَكُمْ وَأَتْمَمْتُ عَلَيْكُمْ نِعْمَتِي وَرَضِيتُ لَكُمُ الْإِسْلَامَ دِينًا ۚ فَمَنِ اضْطُرَّ فِي مَخْمَصَةٍ غَيْرَ مُتَجَانِفٍ لِّإِثْمٍ فَإِنَّ اللَّهَ غَفُورٌ رَّحِيمٌ",
            )
        }
    }
}
