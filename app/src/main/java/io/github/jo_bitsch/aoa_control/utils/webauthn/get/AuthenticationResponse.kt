package io.github.jo_bitsch.aoa_control.utils.webauthn.get

import androidx.credentials.PublicKeyCredential
import io.github.jo_bitsch.aoa_control.utils.webauthn.helper.ByteArrayAsUrlSafeBase64StringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AuthenticationResponse(
    val id: String,
    val type: String,
    @Serializable(with = ByteArrayAsUrlSafeBase64StringSerializer::class)
    val rawId: ByteArray,
    val authenticatorAttachment: String? = null,
    val response: ResponseContent
) {
    init {
        require(type == "public-key")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthenticationResponse

        if (id != other.id) return false
        if (type != other.type) return false
        if (!rawId.contentEquals(other.rawId)) return false
        if (authenticatorAttachment != other.authenticatorAttachment) return false
        if (response != other.response) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + rawId.contentHashCode()
        result = 31 * result + (authenticatorAttachment?.hashCode() ?: 0)
        result = 31 * result + response.hashCode()
        return result
    }
}

fun PublicKeyCredential.authenticationResponse(): AuthenticationResponse {
    val format = Json { ignoreUnknownKeys = true }
    return format.decodeFromString<AuthenticationResponse>(this.authenticationResponseJson)
}
