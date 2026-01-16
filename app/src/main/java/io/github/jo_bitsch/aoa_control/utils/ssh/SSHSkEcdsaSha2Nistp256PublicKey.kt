package io.github.jo_bitsch.aoa_control.utils.ssh

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Specification here: https://github.com/openssh/openssh-portable/blob/master/PROTOCOL.u2f
 */
class SSHSkEcdsaSha2Nistp256PublicKey(
    val x: ByteArray,
    val y: ByteArray,
    /**
     * SSH itself would use the prefix "ssh:".
     * WebAuthn uses the origin, without a prefix, so just a hostname such as "www.mindrot.org"
     */
    val application: String,
    val userName: String
) {
    companion object {
        const val SSH_ALG_NAME = "sk-ecdsa-sha2-nistp256@openssh.com"
        const val SSH_CRV_NAME = "nistp256"
        const val UNCOMPRESSED_POINT_MARKER = 0x4.toByte()
    }

    /**
     * also known as SSH pubkey string as opposed to SSH pubkey blob
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun toAuthorizedKeysFormat(): String {
        val applicationBytes = application.encodeToByteArray() // user provided, might not be ASCII
        val buffer = ByteBuffer.allocate(
            4 + SSH_ALG_NAME.length +  // default algorithm name
            4 + SSH_CRV_NAME.length +            // the curve name "nistp256"
            4 + 1 + x.size + y.size +            // the public key in uncompressed form
            4 + applicationBytes.size            // the application name
        )
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.putInt(SSH_ALG_NAME.length)
        buffer.put(SSH_ALG_NAME.encodeToByteArray())  // is constant -> ASCII
        buffer.putInt(SSH_CRV_NAME.length)
        buffer.put(SSH_CRV_NAME.encodeToByteArray())  // is constant -> ASCII
        buffer.putInt(1 + x.size + y.size)
        buffer.put(UNCOMPRESSED_POINT_MARKER)
        buffer.put(x)
        buffer.put(y)
        buffer.putInt(applicationBytes.size)
        buffer.put(applicationBytes)

        return "$SSH_ALG_NAME ${Base64.encode(buffer.array())} $userName@$application" +
                "using AOAControl"

    }
}