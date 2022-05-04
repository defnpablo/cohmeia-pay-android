package br.com.cohmeia.cashier

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.cohmeia.R
import br.com.cohmeia.common.tintIn
import br.com.cohmeia.login.LoginActivity
import br.com.cohmeia.login.LoginRepository
import br.com.cohmeia.navigation.Navigation
import br.com.cohmeia.sync.SyncRepository
import br.com.cohmeia.wallet.operation.create.CreateWalletContract
import br.com.cohmeia.wallet.operation.create.CreateWalletPresenter
import br.com.cohmeia.wallet.operation.recharge.RechargeContract
import br.com.cohmeia.wallet.operation.recharge.RechargePaymentType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_recharge.*
import kotlinx.android.synthetic.main.fragment_recharge.employee_cpf_textView
import kotlinx.android.synthetic.main.fragment_recharge.employee_name_textView
import kotlinx.android.synthetic.main.fragment_recharge.sync_status_imageView
import kotlinx.android.synthetic.main.fragment_recharge.toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import timber.log.Timber


class CashierFragment : Fragment(),
    RechargeContract.View,
    CreateWalletContract.View {

    private val rechargePresenter: RechargeContract.Presenter by inject()
    private val syncRepository: SyncRepository by inject()
    private var createPresenter = CreateWalletPresenter(this)
    private val loginRepository: LoginRepository by inject()

    private var navigator: Navigation? = null

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

    override fun onResume() {
        super.onResume()
        rechargePresenter.attachView(this)
        setupSyncIcon()
    }

    override fun onPause() {
        super.onPause()
        rechargePresenter.detachView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            rechargePresenter.clear()
        }

        return inflater.inflate(R.layout.fragment_recharge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_1.setOnClickListener { rechargePresenter.addCash(1) }
        button_2.setOnClickListener { rechargePresenter.addCash(2) }
        button_5.setOnClickListener { rechargePresenter.addCash(5) }
        button_10.setOnClickListener { rechargePresenter.addCash(10) }
        button_20.setOnClickListener { rechargePresenter.addCash(20) }
        button_50.setOnClickListener { rechargePresenter.addCash(50) }
        button_100.setOnClickListener { rechargePresenter.addCash(100) }
        clear_button.setOnClickListener { rechargePresenter.clear() }
        recharge_button.setOnClickListener { rechargePresenter.validateRecharge() }
        sync_status_imageView.setOnClickListener { navigator?.openSync() }

        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.menu_logout -> {
                    loginRepository.logout()
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
                    true
                }
                R.id.menu_create -> {
                    createPresenter.createWallet()
                    true
                }
                R.id.menu_sync -> {
                    navigator?.openSync()
                    true
                }
                R.id.menu_restore -> {
                    navigator?.openBackup()
                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }

        loginRepository.getCurrentUser()?.let {
            employee_name_textView.text = it.employeeName
            employee_cpf_textView.text = it.userCpf
        }
    }

    private fun setupSyncIcon() {
        CoroutineScope(Dispatchers.IO).launch {
            val lastSyncDate = syncRepository.getLastSyncDate()
            withContext(Dispatchers.Main) {
                if (sync_status_imageView != null) {
                    with(sync_status_imageView) {
                        lastSyncDate?.let {
                            if (it.success) tintIn(R.color.success) else tintIn(R.color.error)
                        } ?: tintIn(R.color.colorControlActivated)
                    }
                }
            }
        }
    }


    companion object {

        const val TAG = "recharge_frag_tag"

        @JvmStatic
        fun newInstance() = CashierFragment()
    }

    //region RechargeContract.View
    override fun updateCashView(cash: Int) {
        cash_textView.text = "$ $cash"
    }

    override fun startNfcRecharge(cash: Int, paymentType: RechargePaymentType) {
        navigator?.openRechargeOperation(cash, paymentType)
    }

    override fun showInvalidRechargeMessage() {
        context?.let { Toast.makeText(it, "Valor inválido", Toast.LENGTH_LONG).show() }
    }

    override fun showPaymentMethodChooser() {
        val paymentTypes = RechargePaymentType.values()
        var selectedPayment = paymentTypes[0]
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog_Cohmeia)
            .setTitle("Forma de pagamento")
            .setSingleChoiceItems(RechargePaymentType.valuesAsString(), 0) { _, position ->
                selectedPayment = paymentTypes[position]
            }
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Confirmar") { _, _ ->
                Timber.d("paying with $selectedPayment")
                rechargePresenter.doRecharge(selectedPayment)
            }
            .show()
    }
    //endregion

    //region CreateWalletContract.View
    override fun startNfcCreation() {
        navigator?.openCreateWalletOperation()
    }
    //endregion
}
