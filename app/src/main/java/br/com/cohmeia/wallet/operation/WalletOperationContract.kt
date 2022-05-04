package br.com.cohmeia.wallet.operation

import android.content.Intent
import br.com.cohmeia.wallet.repository.Result

interface WalletOperationContract {

    interface View {
        fun showCreateView()
        fun showRechargeView(rechargeAmount: Int)
        fun showWithdrawView(withdrawAmount: Int)
        fun showReadView()
        fun close()
        fun showResult(result: Result)
        fun showRestoreView(tagId: String, tagBalance: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun setupWalletOperation(walletOperation: WalletOperation)
        fun processWalletData(intent: Intent, walletOperation: WalletOperation, tagData: String?)
        fun cancelOperation()
    }

}