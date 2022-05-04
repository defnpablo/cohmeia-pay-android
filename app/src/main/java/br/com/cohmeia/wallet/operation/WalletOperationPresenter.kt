package br.com.cohmeia.wallet.operation

import android.content.Intent
import br.com.cohmeia.wallet.repository.Error
import br.com.cohmeia.wallet.repository.Result
import br.com.cohmeia.wallet.repository.WalletNotCreated
import br.com.cohmeia.wallet.repository.WalletRepository

class WalletOperationPresenter(
    private val repository: WalletRepository
) : WalletOperationContract.Presenter {

    private var view: WalletOperationContract.View? = null

    override fun attachView(view: WalletOperationContract.View) {
        if (this.view == null) {
            this.view = view
        }
    }

    override fun detachView() {
        view = null
    }

    override fun setupWalletOperation(walletOperation: WalletOperation) {
        when (walletOperation) {
            is CreateWallet -> setupCreateWalletView()
            is RechargeWallet -> setupRechargeView(walletOperation.amount)
            is WithdrawWallet -> setupWithdrawView(walletOperation.checkout.totalInCents)
            is RestoreWallet -> setupRestoreWalletView(walletOperation.tagId, walletOperation.tagBalance)
        }
    }

    override fun processWalletData(intent: Intent, walletOperation: WalletOperation, tagData: String?) {
        when (walletOperation) {
            is CreateWallet -> repository.create(intent) {
                processResponse(it)
            }
            is RechargeWallet -> {
                if (tagData == null || tagData.isEmpty()) {
                    processResponse(Error(WalletNotCreated))
                    return
                }

                repository.recharge(
                    intent,
                    tagData.toInt(),
                    walletOperation.amount,
                    walletOperation.paymentType
                ) {
                    processResponse(it)
                }
            }
            is WithdrawWallet -> {
                if (tagData == null || tagData.isEmpty()) {
                    processResponse(Error(WalletNotCreated))
                    return
                }

                repository.withdraw(
                    intent,
                    tagData.toInt(),
                    walletOperation.checkout
                ) {
                    processResponse(it)
                }
            }
            is RestoreWallet -> repository.create(intent, walletOperation.tagBalance.toInt()) {
                processResponse(it)
            }
        }
    }

    override fun cancelOperation() {
        view?.close()
    }

    private fun setupCreateWalletView() {
        view?.showCreateView()
    }

    private fun setupWithdrawView(amount: Int) {
        view?.showWithdrawView(amount)
    }

    private fun setupRechargeView(amount: Int) {
        view?.showRechargeView(amount)
    }

    private fun setupRestoreWalletView(tagId: String, tagBalance: String) {
        view?.showRestoreView(tagId, tagBalance)
    }

    private fun processResponse(it: Result) {
        view?.showResult(it)
    }

}
