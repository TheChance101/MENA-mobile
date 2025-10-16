import net.thechance.mena.core_chat.data.source.local.database.MessageLocalDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.mapper.toLocalDto
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageStatusTest {
    
    @Test
    fun `toEntity should convert MessageLocalDto MessageStatus to MessageStatus correctly`() {
        assertEquals(MessageStatus.LOADING, MessageLocalDto.MessageStatus.LOADING.toDomain())
        assertEquals(MessageStatus.SENT, MessageLocalDto.MessageStatus.SENT.toDomain())
        assertEquals(MessageStatus.FAILED, MessageLocalDto.MessageStatus.FAILED.toDomain())
        assertEquals(MessageStatus.READ, MessageLocalDto.MessageStatus.READ.toDomain())
    }

    @Test
    fun `toLocalDto should convert MessageStatus to MessageLocalDto MessageStatus correctly`() {
        assertEquals(MessageLocalDto.MessageStatus.LOADING, MessageStatus.LOADING.toLocalDto())
        assertEquals(MessageLocalDto.MessageStatus.SENT, MessageStatus.SENT.toLocalDto())
        assertEquals(MessageLocalDto.MessageStatus.FAILED, MessageStatus.FAILED.toLocalDto())
        assertEquals(MessageLocalDto.MessageStatus.READ, MessageStatus.READ.toLocalDto())
    }
}