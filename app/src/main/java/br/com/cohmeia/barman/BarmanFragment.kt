package br.com.cohmeia.barman

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.cohmeia.R
import br.com.cohmeia.barman.checkout.Checkout
import br.com.cohmeia.barman.checkout.CheckoutProduct
import br.com.cohmeia.barman.checkout.Product
import br.com.cohmeia.common.hide
import br.com.cohmeia.common.show
import br.com.cohmeia.common.tintIn
import br.com.cohmeia.login.LoginActivity
import br.com.cohmeia.login.LoginRepository
import br.com.cohmeia.navigation.Navigation
import br.com.cohmeia.sync.SyncRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_barman.*
import kotlinx.android.synthetic.main.fragment_barman.employee_cpf_textView
import kotlinx.android.synthetic.main.fragment_barman.employee_name_textView
import kotlinx.android.synthetic.main.fragment_barman.sync_status_imageView
import kotlinx.android.synthetic.main.fragment_barman.toolbar
import kotlinx.android.synthetic.main.layout_checkout_resume.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class BarmanFragment : Fragment(), BarmanContract.View {

    private val barmanPresenter: BarmanContract.Presenter by inject()
    private val loginRepository: LoginRepository by inject()
    private val syncRepository: SyncRepository by inject()

    private val productAdapter = ProductCellAdapter()
    private val checkoutProductAdapter = CheckoutProductsAdapter()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_barman, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupBuyButton()
        setupClearButton()
        setupBottomSheet()
        setupProductsList(view)
        setupCartProductsList(view)

    }

    private fun setupToolbar() {
        sync_status_imageView.setOnClickListener { navigator?.openSync() }
        toolbar.inflateMenu(R.menu.menu_barman)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_logout -> {
                    TemporaryCart.clear()
                    loginRepository.logout()
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
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

    private fun setupBuyButton() {
        buy_button.setOnClickListener {
            barmanPresenter.checkout()
        }
    }

    private fun setupClearButton() {
        clear_checkout_button.setOnClickListener {
            barmanPresenter.clearCheckout()
        }
    }

    private fun setupBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(checkout_bottomSheet)
        bottomSheetBehavior.apply {
            isHideable = false
        }

        checkout_total_textView.setOnClickListener {
            bottomSheetBehavior.state = if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun setupProductsList(view: View) {
        with(products_recyclerView) {
            layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
            adapter = productAdapter
            setHasFixedSize(true)
        }

        productAdapter.addAction = { product, quantity ->
            barmanPresenter.addProduct(product, quantity)
        }

        barmanPresenter.attachView(this)
        CoroutineScope(Dispatchers.Main).launch {
            barmanPresenter.loadProductList()
        }
    }

    private fun setupCartProductsList(view: View) {
        with(checkout_resume_recyclerView) {
            layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
            adapter = checkoutProductAdapter
            setHasFixedSize(false)
        }

        checkoutProductAdapter.removeAction = { checkoutProduct, _ ->
            barmanPresenter.removeCheckoutProduct(checkoutProduct)
        }

        if (TemporaryCart.items.isNotEmpty() && checkoutProductAdapter.itemCount <= 0){
            TemporaryCart.items.forEach {
                barmanPresenter.addProduct(it.product, it.quantity)
            }
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

    override fun onResume() {
        super.onResume()
        barmanPresenter.attachView(this)
        setupSyncIcon()
    }

    override fun onPause() {
        super.onPause()
        barmanPresenter.detachView()
    }

    //region BarmanContract.View
    override fun resetCheckoutList() {
        checkoutProductAdapter.removeAll()
    }

    override fun updateTotalValue(totalValue: String) {
        checkout_total_textView.text = "Total: $totalValue"
    }

    override fun goToCheckout(checkout: Checkout) {
        navigator?.openWithdrawOperation(checkout)
    }

    override fun showProductList(product: List<Product>) {
        productAdapter.addProducts(product)
    }

    override fun removeProductFromCheckout(checkoutProduct: CheckoutProduct) {
        checkoutProductAdapter.remove(checkoutProduct)
    }

    override fun addCheckoutList(checkoutProduct: CheckoutProduct) {
        checkoutProductAdapter.add(checkoutProduct)
    }

    override fun updateCheckoutList(checkoutProduct: CheckoutProduct, index: Int) {
        checkoutProductAdapter.update(checkoutProduct, index)
    }

    override fun showEmptyView() {
        checkout_empty_textView.show()
    }

    override fun hideEmptyView() {
        checkout_empty_textView.hide()
    }

    override fun showClearButton() {
        clear_checkout_button.show()
    }

    override fun hideClearButton() {
        clear_checkout_button.hide()
    }

    override fun showEmptyCartFeedback() {
        Toast.makeText(context, "Selecione um produto", Toast.LENGTH_LONG).show()
    }
    //endregion

    companion object {
        const val TAG = "BarmanFragment"

        @JvmStatic
        fun newInstance() = BarmanFragment()
    }
}
