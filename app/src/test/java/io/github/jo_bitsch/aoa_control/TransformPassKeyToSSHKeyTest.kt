package io.github.jo_bitsch.aoa_control

import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.PublicKeyCredential
import io.github.jo_bitsch.aoa_control.utils.sshAuthorizedKey
import io.github.jo_bitsch.aoa_control.utils.sshSignature
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals


@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class TransformPassKeyToSSHKeyTest {

    @Test
    fun handleCreatePublicKeyResponse() {
        val userName = "jo"
        val rpId = "credential-manager-test.example.com"

        @Suppress("SpellCheckingInspection")
        val testResponse = CreatePublicKeyCredentialResponse(
            """
            {
              "id": "KEDetxZcUfinhVi6Za5nZQ",
              "type": "public-key",
              "rawId": "KEDetxZcUfinhVi6Za5nZQ",
              "response": {
                "clientDataJSON": "eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRlIiwiY2hhbGxlbmdlIjoibmhrUVhmRTU5SmI5N1Z5eU5Ka3ZEaVh1Y01Fdmx0ZHV2Y3JEbUdyT0RIWSIsIm9yaWdpbiI6ImFuZHJvaWQ6YXBrLWtleS1oYXNoOk1MTHpEdll4UTRFS1R3QzZVNlpWVnJGUXRIOEdjVi0xZDQ0NEZLOUh2YUkiLCJhbmRyb2lkUGFja2FnZU5hbWUiOiJjb20uZ29vZ2xlLmNyZWRlbnRpYWxtYW5hZ2VyLnNhbXBsZSJ9",
                "attestationObject": "o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YViUj5r_fLFhV-qdmGEwiukwD5E_5ama9g0hzXgN8thcFGRdAAAAAAAAAAAAAAAAAAAAAAAAAAAAEChA3rcWXFH4p4VYumWuZ2WlAQIDJiABIVgg4RqZaJyaC24Pf4tT-8ONIZ5_Elddf3dNotGOx81jj3siWCAWXS6Lz70hvC2g8hwoLllOwlsbYatNkO2uYFO-eJID6A"
              }
            }
        """.trimIndent()
        )

        val sshKey = testResponse.sshAuthorizedKey(
            userName,
            rpId
        )

        //FidoPublicKeyCredential

        @Suppress("SpellCheckingInspection")
        assert(sshKey.startsWith("sk-ecdsa-sha2-nistp256@openssh.com AAAAInNrLWVjZHNhLXNoYTItbmlzdHAyNTZAb3BlbnNzaC5jb20AAAAIbmlzdHAyNTYAAABBBOEamWicmgtuD3+LU/vDjSGefxJXXX93TaLRjsfNY497Fl0ui8+9IbwtoPIcKC5ZTsJbG2GrTZDtrmBTvniSA+gAAAAjY3JlZGVudGlhbC1tYW5hZ2VyLXRlc3QuZXhhbXBsZS5jb20="))

    }


    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun handleSigParsing() {
        val origin = "credential-manager-app-test.glitch.me"

        @Suppress("SpellCheckingInspection")
        val testResponse = PublicKeyCredential(
            """
            {
              "id": "KEDetxZcUfinhVi6Za5nZQ",
              "type": "public-key",
              "rawId": "KEDetxZcUfinhVi6Za5nZQ",
              "response": {
                "clientDataJSON": "eyJ0eXBlIjoid2ViYXV0aG4uZ2V0IiwiY2hhbGxlbmdlIjoiVDF4Q3NueE0yRE5MMktkSzVDTGE2Zk1oRDdPQnFobzZzeXpJbmtfbi1VbyIsIm9yaWdpbiI6ImFuZHJvaWQ6YXBrLWtleS1oYXNoOk1MTHpEdll4UTRFS1R3QzZVNlpWVnJGUXRIOEdjVi0xZDQ0NEZLOUh2YUkiLCJhbmRyb2lkUGFja2FnZU5hbWUiOiJjb20uZ29vZ2xlLmNyZWRlbnRpYWxtYW5hZ2VyLnNhbXBsZSJ9",
                "authenticatorData": "j5r_fLFhV-qdmGEwiukwD5E_5ama9g0hzXgN8thcFGQdAAAAAA",
                "signature": "MEUCIQCO1Cm4SA2xiG5FdKDHCJorueiS04wCsqHhiRDbbgITYAIgMKMFirgC2SSFmxrh7z9PzUqr0bK1HZ6Zn8vZVhETnyQ",
                "userHandle": "2HzoHm_hY0CjuEESY9tY6-3SdjmNHOoNqaPDcZGzsr0"
              }
            }
        """.trimIndent()
        )
        val result = testResponse
            .sshSignature(origin)
            .toHexString()

        @Suppress("SpellCheckingInspection")
        val expected =
            "0000002b776562617574686e2d736b2d65636473612d736861322d6e69737470323536406f70656e7373682e636f6d0000004900000021008ed429b8480db1886e4574a0c7089a2bb9e892d38c02b2a1e18910db6e0213600000002030a3058ab802d924859b1ae1ef3f4fcd4aabd1b2b51d9e999fcbd95611139f241d000000000000002563726564656e7469616c2d6d616e616765722d6170702d746573742e676c697463682e6d65000000d87b2274797065223a22776562617574686e2e676574222c226368616c6c656e6765223a2254317843736e784d32444e4c324b644b35434c6136664d6844374f4271686f3673797a496e6b5f6e2d556f222c226f726967696e223a22616e64726f69643a61706b2d6b65792d686173683a4d4c4c7a447659785134454b5477433655365a56567246517448384763562d3164343434464b3948766149222c22616e64726f69645061636b6167654e616d65223a22636f6d2e676f6f676c652e63726564656e7469616c6d616e616765722e73616d706c65227d00000000"
        assertEquals(expected, result)
    }


    @Test
    fun handleCreatePublicKeyResponse2() {
        val userName = "test"
        val rpId = "www.mindrot.org"

        @Suppress("SpellCheckingInspection")
        val testResponse = CreatePublicKeyCredentialResponse(
            """
            {
              "id": "UwraYIrsQEiVwTM7ItZ-Bg",
              "type": "public-key",
              "rawId": "UwraYIrsQEiVwTM7ItZ-Bg",
              "authenticatorAttachment": "cross-platform",
              "response": {
                "clientDataJSON": "e3R5cGU6d2ViYXV0aG4uY3JlYXRlLGNoYWxsZW5nZTpPTWRWb1k1SlhUQXc2bDU0LXMzSEJlQWJrWWkyc05sNlVZU3FjczI0Vk1NLG9yaWdpbjpodHRwczovL3d3dy5taW5kcm90Lm9yZyxjcm9zc09yaWdpbjpmYWxzZX0=",
                "attestationObject": "o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YViU1V6s01pZd3Mxhjsesmwd0cYlgVN0FDo2xxz4XGynHAlBAAAAAAAAAAAAAAAAAAAAAAAAAAAAEO9gD2HN_0yrBK0caHu30qKlAQIDJiABIVggbPila5YxXEYdtIkOElzpdVYlt4a3yRHD8KYJBTl3YxwiWCCM9UBsBy7q_4tc4ylLW_o3hrFAvBdmAFmgXMiiUQ-8FA=="
              }
            }
        """.trimIndent()
        )

        val sshKey = testResponse.sshAuthorizedKey(
            userName,
            rpId
        )

        @Suppress("SpellCheckingInspection")
        assert(sshKey.startsWith(
            "sk-ecdsa-sha2-nistp256@openssh.com AAAAInNrLWVjZHNhLXNoYTItbmlzdHAyNTZAb3BlbnNzaC5jb20AAAAIbmlzdHAyNTYAAABBBGz4pWuWMVxGHbSJDhJc6XVWJbeGt8kRw/CmCQU5d2McjPVAbAcu6v+LXOMpS1v6N4axQLwXZgBZoFzIolEPvBQAAAAPd3d3Lm1pbmRyb3Qub3Jn"
        ))

    }


    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun handleSigParsing2() {
        val origin = "https://www.mindrot.org"

        @Suppress("SpellCheckingInspection")
        val testResponse = PublicKeyCredential(
            """
            {
              "id": "UwraYIrsQEiVwTM7ItZ-Bg",
              "type": "public-key",
              "rawId": "UwraYIrsQEiVwTM7ItZ-Bg",
              "authenticatorAttachment": "platform",
              "response": {
                "clientDataJSON": "eyJ0eXBlIjoid2ViYXV0aG4uZ2V0IiwiY2hhbGxlbmdlIjoiVTFOSVUwbEhBQUFBQlhSbGMzUXpBQUFBQUFBQUFBWnphR0UxTVRJQUFBQkFiU0FiN3UtMWliQ084R2N0cklJMVBReTltdG1lRmtMSU9oWUI4OVpIdk1vQU1sZTE2UE1iM0Ixei0teUUtd2hjZWRiaVozdF8tU2ZvSTZWT2VKRkEyUSIsIm9yaWdpbiI6Imh0dHBzOi8vd3d3Lm1pbmRyb3Qub3JnIiwiY3Jvc3NPcmlnaW4iOmZhbHNlfQ==",
                "authenticatorData": "1V6s01pZd3Mxhjsesmwd0cYlgVN0FDo2xxz4XGynHAkZAAAAAA==",
                "signature": "MEYCIQCpTxQfi-4-zggu1_jYdknxWHZWY1o5eCyyz0NBsGSKHgIhAOdo8NlxasadjjY6T6yii1BX2Y1vShF67kPaL1fGZhx9",
                "userHandle": "pNPoNmtn4xQ="
              }
            }
        """.trimIndent()
        )
        val result = testResponse
            .sshSignature(origin)
            .toHexString()

        @Suppress("SpellCheckingInspection")
        val expected =
            "0000002b776562617574686e2d736b2d65636473612d736861322d6e69737470323536406f70656e7373682e636f6d0000004a0000002100a94f141f8bee3ece082ed7f8d87649f1587656635a39782cb2cf4341b0648a1e0000002100e768f0d9716ac69d8e363a4faca28b5057d98d6f4a117aee43da2f57c6661c7d19000000000000001768747470733a2f2f7777772e6d696e64726f742e6f7267000000df7b2274797065223a22776562617574686e2e676574222c226368616c6c656e6765223a2255314e4955306c48414141414258526c6333517a414141414141414141415a7a614745314d544941414142416253416237752d316962434f3847637472494931505179396d746d65466b4c494f68594238395a48764d6f414d6c653136504d623342317a2d2d79452d776863656462695a33745f2d53666f4936564f654a46413251222c226f726967696e223a2268747470733a2f2f7777772e6d696e64726f742e6f7267222c2263726f73734f726967696e223a66616c73657d00000000"
        assertEquals(expected, result)
    }

}