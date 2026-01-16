package io.github.jo_bitsch.aoa_control.utils

import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.PublicKeyCredential
import io.github.jo_bitsch.aoa_control.utils.ssh.SSHSkEcdsaSha2Nistp256PublicKey
import io.github.jo_bitsch.aoa_control.utils.ssh.SSHWebauthnSkEcdsaSha2Nistp256Signature
import io.github.jo_bitsch.aoa_control.utils.webauthn.create.RegistrationResponse
import io.github.jo_bitsch.aoa_control.utils.webauthn.get.authenticationResponse


fun CreatePublicKeyCredentialResponse.sshAuthorizedKey(
    userName: String,
    rpId: String,
): String {
    val r = RegistrationResponse.decodeFromCreatePublicKeyCredentialResponse(this)
    val publicKey =
        r.response.attestationObject.authData.attestedCredentialData?.credentialPublicKey
    require(publicKey != null)
    val sshPublicKey = SSHSkEcdsaSha2Nistp256PublicKey(publicKey.x,publicKey.y, rpId, userName)
    return sshPublicKey.toAuthorizedKeysFormat()

}

fun PublicKeyCredential.sshSignature(
    origin: String
): ByteArray {
    val r = this.authenticationResponse()
    val s = SSHWebauthnSkEcdsaSha2Nistp256Signature(
        r.response.signature,
        r.response.authenticatorData.flags,
        r.response.authenticatorData.counter,
        origin,
        r.response.clientDataJSON,
        r.response.authenticatorData.extensions
    )
    return s.toByteArray()
}


