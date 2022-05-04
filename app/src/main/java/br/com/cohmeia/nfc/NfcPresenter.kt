package br.com.cohmeia.nfc

import br.com.cohmeia.wallet.operation.WalletOperation

class NfcPresenter : NfcContract.Presenter {

    private var view: NfcContract.View? = null
    private var currentOperation: WalletOperation? = null

    override fun attachView(view: NfcContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun setCurrentOperation(operation: WalletOperation?) {
        currentOperation = operation
        view?.pauseReadCapability()
    }

    override fun cancelOperation() {
        currentOperation = null
        view?.resumeReadCapability()
    }

    override fun getCurrentOperation(): WalletOperation? {
        return currentOperation
    }
}