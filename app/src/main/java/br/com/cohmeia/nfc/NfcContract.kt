package br.com.cohmeia.nfc

import br.com.cohmeia.wallet.operation.WalletOperation

interface NfcContract {

    interface View {
        fun pauseReadCapability()
        fun resumeReadCapability()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun setCurrentOperation(operation: WalletOperation?)
        fun cancelOperation()
        fun getCurrentOperation() : WalletOperation?
    }

}