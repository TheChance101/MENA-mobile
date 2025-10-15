package net.thechance.mena.wallet.presentation.navigation.navType

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.thechance.mena.wallet.presentation.utils.StorageLocation

object StorageLocationNavType : NavType<StorageLocation>(isNullableAllowed = false) {
    override fun put(
        bundle: SavedState,
        key: String,
        value: StorageLocation
    ) {
        bundle.write {
            putString(key, Json.encodeToString(StorageLocationSerializer, value))
        }
    }

    override fun get(
        bundle: SavedState,
        key: String
    ): StorageLocation {
        return bundle.read {
            Json.decodeFromString(deserializer = StorageLocationSerializer, getString(key))
        }
    }

    override fun parseValue(value: String): StorageLocation {
        return Json.decodeFromString(StorageLocationSerializer, value)
    }

    override fun serializeAsValue(value: StorageLocation): String {
        return Json.encodeToString(StorageLocationSerializer, value)
    }
}

object StorageLocationSerializer : KSerializer<StorageLocation> {
    override val descriptor = PrimitiveSerialDescriptor("StorageLocation", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: StorageLocation) {
        val json = when (value) {
            is StorageLocation.Cache -> """{"type":"cache","fileName":"${value.fileName}"}"""
            is StorageLocation.Downloads -> """{"type":"downloads","fileName":"${value.fileName}"}"""
        }
        encoder.encodeString(json)
    }

    override fun deserialize(decoder: Decoder): StorageLocation {
        val json = decoder.decodeString()
        val jsonObject = Json.parseToJsonElement(json).jsonObject

        return when (jsonObject["type"]?.jsonPrimitive?.content) {
            "cache" -> StorageLocation.Cache(
                fileName = jsonObject["fileName"]?.jsonPrimitive?.content ?: ""
            )
            "downloads" -> StorageLocation.Downloads(
                fileName = jsonObject["fileName"]?.jsonPrimitive?.content ?: "",
            )
            else -> throw IllegalArgumentException("Unknown StorageLocation type")
        }
    }
}
