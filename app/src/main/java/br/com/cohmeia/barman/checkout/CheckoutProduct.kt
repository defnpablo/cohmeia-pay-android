package br.com.cohmeia.barman.checkout

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class CheckoutProduct(
    var quantity: Int,
    val product: Product
) : Serializable