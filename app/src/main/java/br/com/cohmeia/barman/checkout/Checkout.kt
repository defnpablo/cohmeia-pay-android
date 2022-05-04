package br.com.cohmeia.barman.checkout

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class Checkout(
    val checkoutProducts: List<CheckoutProduct>,
    val totalInCents: Int
) : Serializable