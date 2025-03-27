package io.github.jo_bitsch.aoa_control.utils.webauthn.create

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray

@OptIn(ExperimentalSerializationApi::class)
class AttestedCredentialData(
    bytes: ByteArray
) {
    @Suppress("unused")
    val aaguid: ByteArray = bytes.sliceArray(0..15)
    val credentialId: ByteArray
    val credentialPublicKey: PublicKey

    init {
        val credentialIdLength =
            bytes.sliceArray(16..17).fold(0) { r, x -> (r * 256) + x.toUInt().toInt() }
        credentialId = bytes.sliceArray(18..<(18 + credentialIdLength))
        val credentialPublicKeyCbor = bytes.sliceArray((18 + credentialIdLength)..<bytes.size)
        val format = Cbor { ignoreUnknownKeys = true; preferCborLabelsOverNames = true }
        credentialPublicKey = format.decodeFromByteArray<PublicKey>(credentialPublicKeyCbor)
    }
}
