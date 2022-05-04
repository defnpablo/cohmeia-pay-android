package br.com.cohmeia.wallet.repository

import android.content.Intent
import br.com.cohmeia.barman.checkout.Checkout
import br.com.cohmeia.wallet.operation.recharge.RechargePaymentType

interface WalletRepository {
    fun recharge(
        intent: Intent,
        currentAmount: Int,
        add: Int,
        paymentType: RechargePaymentType,
        callback: (Result) -> Unit
    )

    fun withdraw(intent: Intent, currentAmount: Int, checkout: Checkout, callback: (Result) -> Unit)
    fun create(intent: Intent, initialBalance:Int = 0, callback: (Result) -> Unit)
}