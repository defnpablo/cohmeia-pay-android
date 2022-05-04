package br.com.cohmeia.navigation

import br.com.cohmeia.barman.checkout.Checkout
import br.com.cohmeia.nfc.core.NfcData
import br.com.cohmeia.wallet.operation.recharge.RechargePaymentType
import br.com.cohmeia.wallet.repository.Result

interface Navigation {
    fun openRechargeOperation(cash: Int, paymentType: RechargePaymentType){}
    fun openCreateWalletOperation() {}
    fun goBack()
    fun openWithdrawOperation(checkout: Checkout) {}
    fun openResult(result: Result) {}
    fun openBarman() {}
    fun openCashier() {}
    fun openLogin() {}
    fun goHome() {}
    fun openSync() {}
    fun openBackup() {}
    fun openRestoreOperation(nfcData: NfcData) {}
}