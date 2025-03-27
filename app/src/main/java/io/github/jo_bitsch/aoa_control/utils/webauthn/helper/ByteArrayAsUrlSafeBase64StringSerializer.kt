package io.github.jo_bitsch.aoa_control.utils.webauthn.helper

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object ByteArrayAsUrlSafeBase64StringSerializer: KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("String", PrimitiveKind.STRING)

    @OptIn(ExperimentalEncodingApi::class)
    override fun serialize(
        encoder: Encoder,
        value: ByteArray
    ) {
        encoder.encodeString(
            Base64.Default.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL).encode(value)
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun deserialize(decoder: Decoder): ByteArray {
        return Base64.Default.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)
                .decode(decoder.decodeString())
    }

}