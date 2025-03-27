package io.github.jo_bitsch.aoa_control.utils.webauthn.create

import io.github.jo_bitsch.aoa_control.utils.webauthn.helper.ByteArrayAsUrlSafeBase64StringSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class ResponseContent(

    @Serializable(with = ByteArrayAsUrlSafeBase64StringSerializer::class)
    val clientDataJSON: ByteArray,

    @Serializable(with = AttestationObjectAsStringSerializer::class)
    val attestationObject: AttestationObject
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponseContent

        if (!clientDataJSON.contentEquals(other.clientDataJSON)) return false
        if (attestationObject != other.attestationObject) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clientDataJSON.contentHashCode()
        result = 31 * result + attestationObject.hashCode()
        return result
    }
}

object AttestationObjectAsStringSerializer: KSerializer<AttestationObject> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("AttestationObject", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: AttestationObject
    ) = TODO("Not yet implemented")

    @OptIn(ExperimentalEncodingApi::class, ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): AttestationObject {
        val attestationObject =
            Base64.Default.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)
                .decode(decoder.decodeString())
        val format = Cbor { ignoreUnknownKeys = true }
        return format.decodeFromByteArray<AttestationObject>(attestationObject)
    }

}
