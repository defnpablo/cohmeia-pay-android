package br.com.cohmeia.nfc

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import br.com.cohmeia.R
import br.com.cohmeia.backup.BackupFragment
import br.com.cohmeia.barman.BarmanFragment
import br.com.cohmeia.navigation.NAVIGATE_CASHIER
import br.com.cohmeia.navigation.Navigation
import br.com.cohmeia.cashier.CashierFragment
import br.com.cohmeia.barman.checkout.Checkout
import br.com.cohmeia.common.*
import br.com.cohmeia.login.*
import br.com.cohmeia.navigation.NAVIGATE_BARMAN
import br.com.cohmeia.navigation.NAVIGATE_LOGIN
import br.com.cohmeia.nfc.core.NfcData
import br.com.cohmeia.nfc.core.NfcService
import br.com.cohmeia.sync.SyncActivity
import br.com.cohmeia.wallet.operation.*
import br.com.cohmeia.wallet.operation.read.ReadWalletContract
import br.com.cohmeia.wallet.operation.recharge.RechargePaymentType
import br.com.cohmeia.wallet.operation.result.ResultFragment
import br.com.cohmeia.wallet.repository.Result
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_consult.view.*
import org.koin.android.ext.android.inject

class NfcActivity : BaseActivity(),
    NfcContract.View,
    ReadWalletContract.View,
    Navigation {

    private var nfcAdapter: NfcAdapter? = null
    private var mPendingIntent: PendingIntent? = null

    private var readWallet = true
    private var readWalletDialog: AlertDialog? = null

    private val nfcPresenter: NfcContract.Presenter by inject()
    private val readWalletPresenter: ReadWalletContract.Presenter by inject()
    private val loginRepository: LoginRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcPresenter.attachView(this)

        mPendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )

        when (intent.getStringExtra(EXTRA_NAVIGATE)) {
            NAVIGATE_BARMAN -> openBarman()
            NAVIGATE_CASHIER -> openCashier()
            NAVIGATE_LOGIN -> openLogin()
            else -> tryToReadWallet()
        }
    }

    private fun tryToReadWallet() {
        when(loginRepository.getCurrentUser()){
            is Cashier ->{
                openCashier()
                Handler().post {
                    handleWalletIntent(intent)
                }
            }
            is Admin -> { finish() }
            else -> openLogin()
        }
    }

    override fun openBarman() {
        supportFragmentManager
            .apply {
                popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            .beginTransaction()
            .replace(R.id.container, BarmanFragment.newInstance(), BarmanFragment.TAG)
            .commit()
    }

    override fun openCashier() {
        supportFragmentManager
            .apply {
                popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            .beginTransaction()
            .replace(R.id.container, CashierFragment.newInstance(), CashierFragment.TAG)
            .commit()
    }

    override fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        nfcPresenter.attachView(this)
        readWalletPresenter.attachView(this)
        nfcAdapter?.enableForegroundDispatch(this, mPendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onStop() {
        super.onStop()
        nfcPresenter.detachView()
        readWalletPresenter.detachView()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleWalletIntent(it) }
    }

    private fun handleWalletIntent(intent: Intent) {
        val data = NfcService.retrieveNFCMessage(intent)

        when (nfcPresenter.getCurrentOperation()) {
            is CreateWallet -> createWallet(intent)
            is RechargeWallet -> doRecharge(intent, data?.content)
            is WithdrawWallet -> withdraw(intent, data?.content)
            is RestoreWallet -> createWallet(intent)
            null -> readWalletPresenter.readWallet(data?.content?.toIntOrNull() ?: -1)
        }
    }

    private fun createWallet(intent: Intent) {
        val fragment = supportFragmentManager.findFragmentByTag(WalletOperationFragment.TAG)
                as? WalletOperationFragment
        fragment?.handleWalletIntent(intent, null)
    }

    private fun doRecharge(intent: Intent, tagCurrentData: String?) {
        val fragment = supportFragmentManager.findFragmentByTag(WalletOperationFragment.TAG)
                as? WalletOperationFragment
        fragment?.handleWalletIntent(intent, tagCurrentData)
    }

    private fun withdraw(intent: Intent, tagCurrentData: String?) {
        val fragment = supportFragmentManager.findFragmentByTag(WalletOperationFragment.TAG)
                as? WalletOperationFragment
        fragment?.handleWalletIntent(intent, tagCurrentData)
    }
//endregion

    //region Navigation
    override fun openRechargeOperation(cash: Int, paymentType: RechargePaymentType) {
        val walletOperation = RechargeWallet(cash.toCents(), paymentType)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                WalletOperationFragment.newInstance(walletOperation),
                WalletOperationFragment.TAG
            )
            .addToBackStack(null)
            .commit()
        nfcPresenter.setCurrentOperation(walletOperation)
    }

    override fun openCreateWalletOperation() {
        val walletOperation = CreateWallet
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                WalletOperationFragment.newInstance(walletOperation),
                WalletOperationFragment.TAG
            )
            .addToBackStack(null)
            .commit()
        nfcPresenter.setCurrentOperation(walletOperation)
    }

    override fun openWithdrawOperation(checkout: Checkout) {
        val walletOperation = WithdrawWallet(checkout)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                WalletOperationFragment.newInstance(walletOperation),
                WalletOperationFragment.TAG
            )
            .addToBackStack(null)
            .commit()
        nfcPresenter.setCurrentOperation(walletOperation)
    }

    override fun openResult(result: Result) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, ResultFragment.newInstance(result), ResultFragment.TAG)
            .addToBackStack(null)
            .commit()
        nfcPresenter.setCurrentOperation(null)
    }

    override fun goBack() {
        supportFragmentManager.popBackStackImmediate()
        nfcPresenter.cancelOperation()
    }

    override fun goHome() {
        when (loginRepository.getCurrentUser()) {
            is Barman -> openBarman()
            is Cashier -> openCashier()
            null -> openLogin()
        }
    }

    override fun openSync() {
        startActivity(Intent(this, SyncActivity::class.java))
    }

    override fun openBackup() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                BackupFragment.newInstance(),
                BackupFragment.TAG
            )
            .addToBackStack(null)
            .commit()
    }

    override fun openRestoreOperation(nfcData: NfcData) {
        val walletOperation = RestoreWallet(nfcData.tagId, nfcData.content)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                WalletOperationFragment.newInstance(walletOperation),
                WalletOperationFragment.TAG
            )
            .addToBackStack(null)
            .commit()
        nfcPresenter.setCurrentOperation(walletOperation)
    }
    //endregion

    //region NfcContract.View
    override fun pauseReadCapability() {
        readWallet = false
    }

    override fun resumeReadCapability() {
        readWallet = true
    }
    //endregion

    //region ReadWalletContract.View
    override fun showBalanceDialog(balance: Int) {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.fragment_consult, null)
        view.wallet_read_imageView.setImageResource(R.drawable.ic_monetization_on_black_24dp)
        view.wallet_read_textView.text = "Saldo atual ${MoneyConverter.formatValueInCents(balance)}"
        view.create_new_wallet_button.hide()

        readWalletDialog?.dismiss()
        readWalletDialog = MaterialAlertDialogBuilder(this)
            .setView(view)
            .show()
    }

    override fun showCreateWalletDialog() {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.fragment_consult, null)
        view.wallet_read_imageView.setImageResource(R.drawable.ic_error_outline_black_24dp)
        view.wallet_read_textView.setText(R.string.error_read_wallet_not_recognized)
        view.create_new_wallet_button.show()
        view.create_new_wallet_button.setOnClickListener {
            readWalletDialog?.dismiss()
            openCreateWalletOperation()
        }

        readWalletDialog?.dismiss()
        readWalletDialog = MaterialAlertDialogBuilder(this)
            .setView(view)
            .show()
    }
    //endregion

    //region Menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }
    //endregion

    override fun onBackPressed() {
        val fragments = supportFragmentManager.fragments
        if (fragments.isNotEmpty() and (fragments.first() is ResultFragment)) {
            goHome()
        } else {
            super.onBackPressed()
        }
    }

}
