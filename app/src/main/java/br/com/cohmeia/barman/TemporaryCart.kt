package br.com.cohmeia.barman

import br.com.cohmeia.barman.checkout.CheckoutProduct

object TemporaryCart {

    var items = listOf<CheckoutProduct>()

    fun clear() {
        items = emptyList()
    }

}