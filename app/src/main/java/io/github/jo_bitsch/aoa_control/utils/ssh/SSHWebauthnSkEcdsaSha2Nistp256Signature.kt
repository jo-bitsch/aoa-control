package io.github.jo_bitsch.aoa_control.utils.ssh

import java.nio.ByteBuffer
import java.nio.ByteOrder

class SSHWebauthnSkEcdsaSha2Nistp256Signature(
    val signature: ByteArray,
    val flags: Byte,
    val signCount: Int,
    /**
     * https origin making the request, e.g. https://www.mindrot.org
     */
    val origin: String,
    val clientDataJSON: ByteArray,
    val extensionData: ByteArray
) {
    companion object {
        const val SSH_SIG_NAME = "webauthn-sk-ecdsa-sha2-nistp256@openssh.com"
    }


    fun x962toSSHSignature(): ByteArray {
        /*
$ base64 -d | openssl asn1parse -inform der
MEUCIQCO1Cm4SA2xiG5FdKDHCJorueiS04wCsqHhiRDbbgITYAIgMKMFirgC2SSFmxrh7z9PzUqr0bK1HZ6Zn8vZVhETnyQ=
0:d=0  hl=2 l=  69 cons: SEQUENCE
2:d=1  hl=2 l=  33 prim: INTEGER           :8ED429B8480DB1886E4574A0C7089A2BB9E892D38C02B2A1E18910DB6E021360
37:d=1  hl=2 l=  32 prim: INTEGER           :30A3058AB802D924859B1AE1EF3F4FCD4AABD1B2B51D9E999FCBD95611139F24
 */
        // therefore we need to reformat it to ssh format (mpint r | mpint s)
        // mpint: https://datatracker.ietf.org/doc/html/rfc4251#section-5 must prepend 0 byte to positive numbers having MSB set as well
        // ASN.1 representation
        // 30 // sequence
        // 45 // 0x45 bytes follow as part of sequence (valid would be 0x44,0x45,0x46)
        // 02 // int
        // 21 // 0x21 bytes of the in follow (0x00 is prepended because the first bit is set so this is not interpreted as a negative number
        //    // valid is 0x20 and 0x21
        // 008ed429b8480db1886e4574a0c7089a2bb9e892d38c02b2a1e18910db6e021360
        // 02 // int
        // 20 // 0x20 bytes follow
        // 30a3058ab802d924859b1ae1ef3f4fcd4aabd1b2b51d9e999fcbd95611139f24

        require(signature[0] == 0x30.toByte()) { "doesn't start with sequence" }
        require((signature[1] + 2) == signature.size) { "size of signature broken" }
        require((signature[2]) == 0x02.toByte()) { "r is not an integer" }
        val rLength = signature[3].toInt()
        require(rLength == 0x20 || rLength == 0x21) { "r is not the expected length of an ecsda signature" }
        val r = signature.sliceArray(3..<(4 + rLength))

        require((signature[4 + rLength]) == 0x02.toByte()) { "s is not an integer" }
        val sLength = signature[4 + rLength + 1].toInt()
        require(sLength == 0x20 || sLength == 0x21) { "s is not the expected length of an ecsda signature" }
        require(signature.size == 6 + rLength + sLength) { "signature length doesn't match content" }
        val s = signature.sliceArray((4 + rLength + 1)..<signature.size)
        // TODO: https://datatracker.ietf.org/doc/html/rfc4251#section-5 must prepend 0 byte to positive numbers having MSB set.
        val sshSig = byteArrayOf(0, 0, 0) + r + byteArrayOf(0, 0, 0) + s
        return sshSig
    }


    /**
     * https://github.com/openssh/openssh-portable/blob/67a115e7a56dbdc3f5a58c64b29231151f3670f5/PROTOCOL.u2f#L222
     *  string		"webauthn-sk-ecdsa-sha2-nistp256@openssh.com"
     * 	string		ecdsa_signature
     * 	byte		flags
     * 	uint32		counter
     * 	string		origin
     * 	string		clientData
     * 	string		extensions
     */
    fun toByteArray(): ByteArray {
        val sshSig = x962toSSHSignature()
        val encodedOrigin = origin.encodeToByteArray()
        val buffer = ByteBuffer.allocate(
            4 + SSH_SIG_NAME.length +
            4 + sshSig.size +
            1 + 4 +
            4 + encodedOrigin.size +
            4 + clientDataJSON.size +
            4 + extensionData.size
        )
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.putInt(SSH_SIG_NAME.length)
        buffer.put(SSH_SIG_NAME.encodeToByteArray()) // constant ASCII
        buffer.putInt(sshSig.size)
        buffer.put(sshSig)
        buffer.put(flags)
        buffer.putInt(signCount)
        buffer.putInt(encodedOrigin.size)
        buffer.put(encodedOrigin)
        buffer.putInt(clientDataJSON.size)
        buffer.put(clientDataJSON)
        buffer.putInt(extensionData.size)
        buffer.put(extensionData)

        return buffer.array()
    }
}