package br.com.cohmeia.barman.checkout

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class Product(
    val code: String,
    val name: String,
    val valueInCents: Int,
    val imageUri: String,
) : Serializable