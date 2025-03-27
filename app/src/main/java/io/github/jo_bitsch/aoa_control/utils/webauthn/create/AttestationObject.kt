package io.github.jo_bitsch.aoa_control.utils.webauthn.create

import io.github.jo_bitsch.aoa_control.utils.webauthn.helper.AuthData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.cbor.ByteString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * This is incomplete and requires us to ignore unknown keys
 * in particular, we are not handling
 * * the attStmt, which would ensure that the credential was created on a specific Token type
 * * the fmt, which tells us the format of the attStmt
 * at all right now
 */
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class AttestationObject(
    val fmt: String,

    @ByteString
    @Serializable(with = AuthDataAsByteArraySerializer::class)
    val authData: AuthData

    // val attStmt
)

object AuthDataAsByteArraySerializer: KSerializer<AuthData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AuthData")
    override fun serialize(
        encoder: Encoder,
        value: AuthData
    ) {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun deserialize(decoder: Decoder): AuthData {
        return AuthData(decoder.decodeSerializableValue(ByteArraySerializer()))
    }
}
