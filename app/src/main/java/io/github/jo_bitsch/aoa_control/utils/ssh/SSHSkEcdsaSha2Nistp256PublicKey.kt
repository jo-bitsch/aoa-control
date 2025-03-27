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
     * SSH itself would use the prefix "ssh:"
     * webauthn uses the origin, without a prefix, so just a hostname such as "www.mindrot.org"
     */
    val application: String,
    val userName: String
) {
    companion object {
        const val SSH_ALG_NAME = "sk-ecdsa-sha2-nistp256@openssh.com"
        const val SSH_CRV_NAME = "nistp256"

    }

    /**
     * also known as SSH pubkey string as opposed to SSH pubkey blob
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun toAuthorizedKeysFormat(): String {
        val applicationBytes = application.encodeToByteArray() // user provided, might not be ASCII
        val buffer = ByteBuffer.allocate(
            4 + SSH_ALG_NAME.length +
            4 + SSH_CRV_NAME.length +
            4 + 1 + x.size + y.size +
            4 + applicationBytes.size
        )
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.putInt(SSH_ALG_NAME.length)
        buffer.put(SSH_ALG_NAME.encodeToByteArray())  // is constant -> ASCII
        buffer.putInt(SSH_CRV_NAME.length)
        buffer.put(SSH_CRV_NAME.encodeToByteArray())  // is constant -> ASCII
        buffer.putInt(1 + x.size + y.size)
        buffer.put(0x4)
        buffer.put(x)
        buffer.put(y)
        buffer.putInt(applicationBytes.size)
        buffer.put(applicationBytes)

        return "$SSH_ALG_NAME ${Base64.Default.encode(buffer.array())} $userName@$application" +
                "using AOAControl"

    }
}