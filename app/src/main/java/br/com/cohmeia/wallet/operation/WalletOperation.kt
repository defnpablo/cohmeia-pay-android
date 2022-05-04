package br.com.cohmeia.wallet.operation

import br.com.cohmeia.barman.checkout.Checkout
import br.com.cohmeia.wallet.operation.recharge.RechargePaymentType
import java.io.Serializable

sealed class WalletOperation : Serializable

object CreateWallet : WalletOperation(), Serializable
data class RestoreWallet(val tagId: String, val tagBalance: String) : WalletOperation(), Serializable
data class RechargeWallet(val amount: Int, val paymentType: RechargePaymentType) : WalletOperation(), Serializable
data class WithdrawWallet(val checkout: Checkout) : WalletOperation(), Serializable
