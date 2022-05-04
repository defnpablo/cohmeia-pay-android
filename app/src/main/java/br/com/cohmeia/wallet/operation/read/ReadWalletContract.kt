package br.com.cohmeia.wallet.operation.read

interface ReadWalletContract {

    interface View {
        fun showBalanceDialog(balance: Int)
        fun showCreateWalletDialog()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun readWallet(balance: Int)
    }

}