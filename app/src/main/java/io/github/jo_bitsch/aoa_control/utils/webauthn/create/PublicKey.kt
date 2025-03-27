package io.github.jo_bitsch.aoa_control.utils.webauthn.create

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.ByteString
import kotlinx.serialization.cbor.CborLabel

/**
 * this is a EC2 ES256 public key on the NIST P-256 curve
 *
 * This is intended to make Fido2 keys available in ssh.
 * As ssh only supports the webauthn signature for NIST P-256 keys as per
 * https://github.com/openssh/openssh-portable/blob/67a115e7a56dbdc3f5a58c64b29231151f3670f5/PROTOCOL.u2f#L222
 * this is the only key type we currently support.
 */
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class PublicKey(
    @CborLabel(1)
    @SerialName("kty")
    val kty: Int,

    @CborLabel(3)
    @SerialName("alg")
    val alg: Int,

    @CborLabel(-1)
    @SerialName("crv")
    val crv: Int,

    @CborLabel(-2)
    @SerialName("x")
    @ByteString
    val x: ByteArray,

    @CborLabel(-3)
    @SerialName("y")
    @ByteString
    val y: ByteArray
) {
    init {
        // see https://www.iana.org/assignments/cose/cose.xhtml for assigned numbers
        require(kty == 2) {
            "we currently only support EC2 key type"
        }
        require(alg == -7) { //-25?
            "we currently only support ES256 signature algorithm"
        }
        require(crv == 1) {
            "we currently only support p-256 curve"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PublicKey

        if (kty != other.kty) return false
        if (alg != other.alg) return false
        if (crv != other.crv) return false
        if (!x.contentEquals(other.x)) return false
        if (!y.contentEquals(other.y)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = kty
        result = 31 * result + alg
        result = 31 * result + crv
        result = 31 * result + x.contentHashCode()
        result = 31 * result + y.contentHashCode()
        return result
    }
}