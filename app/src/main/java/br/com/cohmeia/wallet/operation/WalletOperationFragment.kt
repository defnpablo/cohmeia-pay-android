package br.com.cohmeia.wallet.operation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.cohmeia.R
import br.com.cohmeia.analytics.CohmeiaAnalytics
import br.com.cohmeia.barman.CheckoutProductsAdapter
import br.com.cohmeia.common.*
import br.com.cohmeia.navigation.Navigation
import br.com.cohmeia.wallet.repository.Result
import kotlinx.android.synthetic.main.fragment_wallet_operation.*
import org.koin.android.ext.android.inject


class WalletOperationFragment : Fragment(),
    WalletOperationContract.View {

    private lateinit var walletOperation: WalletOperation
    private val presenter: WalletOperationPresenter by inject()
    private var navigator: Navigation? = null
    private val analytics: CohmeiaAnalytics by inject()

    private val walletOperationProductAdapter = CheckoutProductsAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Navigation) {
            navigator = context
        } else {
            throw RuntimeException(context.toString() + " must implement Navigation")
        }
    }

    override fun onDetach() {
        super.onDetach()
        navigator = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            try {
                walletOperation = it.getSerializable(EXTRA_OPERATION) as WalletOperation
            } catch (e: Exception) {
                throw RuntimeException("operation must not be null")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wallet_operation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)
        presenter.setupWalletOperation(walletOperation)
        analytics.logOperationRequested(walletOperation)

        cancel_operation_button.setOnClickListener { presenter.cancelOperation() }

        with(wallet_operation_recyclerView) {
            layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
            adapter = walletOperationProductAdapter.also {
                it.enableRemove = false
            }
            setHasFixedSize(false)
        }

        if (walletOperation is WithdrawWallet) {
            walletOperationProductAdapter.addAll((walletOperation as WithdrawWallet).checkout.checkoutProducts)
        }

    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }


    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    fun handleWalletIntent(intent: Intent, walletData: String?) {
        presenter.processWalletData(intent, walletOperation, walletData)
    }

    //region WalletContract.View
    override fun showCreateView() {
        operation_textView.text = "Criar comanda"
        operation_imageView.setImageResource(R.drawable.ic_account_balance_wallet_black_24dp)
    }

    override fun showRechargeView(rechargeAmount: Int) {
        operation_textView.text = "Recarregar\n${MoneyConverter.formatValueInCents(rechargeAmount)}"
        operation_imageView.setImageResource(R.drawable.ic_monetization_on_black_24dp)
    }

    override fun showWithdrawView(withdrawAmount: Int) {
        operation_textView.text = "Cobrar ${MoneyConverter.formatValueInCents(withdrawAmount)}"
        operation_imageView.setImageResource(R.drawable.ic_credit_card_black_24dp)
    }

    override fun showRestoreView(tagId: String, tagBalance: String) {
        operation_textView.text = "Restaurar\nID:$tagId\n${MoneyConverter.formatValueInCents(tagBalance.toInt())}"
        operation_imageView.setImageResource(R.drawable.ic_baseline_restore_24)
    }

    override fun showReadView() {
        operation_textView.text = "Consultar comanda"
    }

    override fun close() {
        analytics.logOperationCancel(walletOperation)
        navigator?.goBack()
    }

    override fun showResult(result: Result) {
        navigator?.openResult(result)
    }

    //endregion
    companion object {

        const val TAG = "wallet_frag_tag"

        @JvmStatic
        fun newInstance(walletOperation: WalletOperation) = WalletOperationFragment()
            .apply {
                arguments = Bundle().apply {
                    putSerializable(EXTRA_OPERATION, walletOperation)
                }
            }
    }

}
