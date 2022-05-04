package br.com.cohmeia.nfc.core

import android.util.Base64
import timber.log.Timber
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object NfcSecurity {
    private const val key = "dmFjYSBhbWFyZWxhIHB1bG91IHBlbGEgcG9ydGE="
    private val cbcKey = key.toByteArray().sliceArray(0..31)
    private val iv = key.toByteArray().sliceArray(0..15)
    private val ivSpec = IvParameterSpec(iv)

    fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(cbcKey, "AES")

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        return Base64.encodeToString(cipher.doFinal(plaintext.toByteArray()), Base64.DEFAULT).also {
            Timber.d("Encrypting data : $plaintext to $it")
        }
    }

    fun decrypt(plaintext: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(cbcKey, "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decoded = Base64.decode(plaintext.toByteArray(), Base64.DEFAULT)
        return String(cipher.doFinal(decoded), Charsets.UTF_8).also {
            Timber.d("Decrypting data : $plaintext to $it")
        }
    }
}

