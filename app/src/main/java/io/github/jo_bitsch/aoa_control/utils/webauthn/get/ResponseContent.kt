package io.github.jo_bitsch.aoa_control.utils.webauthn.get

import io.github.jo_bitsch.aoa_control.utils.webauthn.helper.AuthData
import io.github.jo_bitsch.aoa_control.utils.webauthn.helper.ByteArrayAsUrlSafeBase64StringSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class ResponseContent(
    /**
     * {
     *   "type" : "webauthn.get",
     *   "challenge" : "T1xCsnxM2DNL2KdK5CLa6fMhD7OBqho6syzInk_n-Uo",
     *   "origin" : "android:apk-key-hash:MLLzDvYxQ4EKTwC6U6ZVVrFQtH8GcV-1d444FK9HvaI",
     *   "androidPackageName" : "com.google.credentialmanager.sample"
     * }
     *
     */
    @Serializable(with = ByteArrayAsUrlSafeBase64StringSerializer::class)
    val clientDataJSON: ByteArray,

    @Serializable(with = AuthDataAsStringSerializer::class)
    val authenticatorData: AuthData,

    @Serializable(with = ByteArrayAsUrlSafeBase64StringSerializer::class)
    val signature: ByteArray,

    @Serializable(with = ByteArrayAsUrlSafeBase64StringSerializer::class)
    val userHandle: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponseContent

        if (!clientDataJSON.contentEquals(other.clientDataJSON)) return false
        if (authenticatorData != other.authenticatorData) return false
        if (!signature.contentEquals(other.signature)) return false
        if (!userHandle.contentEquals(other.userHandle)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clientDataJSON.contentHashCode()
        result = 31 * result + authenticatorData.hashCode()
        result = 31 * result + signature.contentHashCode()
        result = 31 * result + userHandle.contentHashCode()
        return result
    }
}

object AuthDataAsStringSerializer: KSerializer<AuthData> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("AuthData", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: AuthData
    ) = TODO("Not yet implemented")

    @OptIn(ExperimentalEncodingApi::class)
    override fun deserialize(decoder: Decoder): AuthData {
        return AuthData(
            Base64.Default.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)
                .decode(decoder.decodeString())
        )
    }

}
