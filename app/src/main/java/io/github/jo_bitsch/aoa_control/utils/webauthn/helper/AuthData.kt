package io.github.jo_bitsch.aoa_control.utils.webauthn.helper

import io.github.jo_bitsch.aoa_control.utils.webauthn.create.AttestedCredentialData
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and

class AuthData(
    bytes: ByteArray
) {
    @Suppress("unused")
    val rpIdHash = bytes.sliceArray(0..<32)

    val flags = bytes[32]

    @Suppress("unused")
    val userPresent = (flags and 0x1) == 0x1.toByte()

    @Suppress("unused")
    val userVerified = (flags and 0x4) == 0x4.toByte()

    @Suppress("unused")
    val backupEligible = (flags and 0x8) == 0x8.toByte()

    @Suppress("unused")
    val backedUp = (flags and 0x10) == 0x10.toByte()

    val attestedCredentialDataIncluded = (flags and 0x40) == 0x40.toByte()
    val extensionDataIncluded = (flags and 0x80.toByte()) == 0x80.toByte()

    init {
        require(!(attestedCredentialDataIncluded and extensionDataIncluded))
    }

    val counter: Int
    init {
        val buffer = ByteBuffer.wrap(bytes.sliceArray(33..36))
        buffer.order(ByteOrder.BIG_ENDIAN)
        counter = buffer.int
    }

    val attestedCredentialData =
        if (attestedCredentialDataIncluded)
            AttestedCredentialData(bytes.sliceArray(37..<bytes.size))
        else null

    val extensions =
        if (extensionDataIncluded)
            bytes.sliceArray(37..<bytes.size)
        else byteArrayOf()
}