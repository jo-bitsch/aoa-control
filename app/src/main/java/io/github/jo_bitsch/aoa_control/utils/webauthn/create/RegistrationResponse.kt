package io.github.jo_bitsch.aoa_control.utils.webauthn.create

import androidx.credentials.CreatePublicKeyCredentialResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RegistrationResponse(
    val id: String,
    val type: String,
    val rawId: String,
    val authenticatorAttachment: String? = null,
    val response: ResponseContent
) {
    /**
     * @throws IllegalArgumentException
     */
    init {
        require(type == "public-key")
    }

    companion object {
        fun decodeFromCreatePublicKeyCredentialResponse(
            input: CreatePublicKeyCredentialResponse
        ): RegistrationResponse {
            val format = Json { ignoreUnknownKeys = true }
            return format.decodeFromString<RegistrationResponse>(input.registrationResponseJson)

        }
    }

}