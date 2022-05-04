package br.com.cohmeia.analytics

import br.com.cohmeia.wallet.operation.WalletOperation

interface CohmeiaAnalytics {
    fun logOperationRequested(walletOperation: WalletOperation)
    fun logOperationCancel(walletOperation: WalletOperation)
    fun logErrorCreatingWallet()
    fun logErrorRechargingWallet(tagCurrentAmount: Int, rechargeAmount: Int)
    fun logErrorWritingTagForOperation(walletOperation: WalletOperation)
}