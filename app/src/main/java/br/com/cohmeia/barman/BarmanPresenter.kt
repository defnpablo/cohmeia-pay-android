package br.com.cohmeia.barman

import br.com.cohmeia.barman.repository.ProductRepository
import br.com.cohmeia.barman.checkout.Checkout
import br.com.cohmeia.barman.checkout.CheckoutProduct
import br.com.cohmeia.barman.checkout.Product
import br.com.cohmeia.common.MoneyConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BarmanPresenter(val repository: ProductRepository) : BarmanContract.Presenter {

    private var view: BarmanContract.View? = null

    private val checkoutProducts = mutableListOf<CheckoutProduct>()

    override fun attachView(view: BarmanContract.View) {
        this.view = view
        if (checkoutProducts.isNotEmpty()) {
            view.hideEmptyView()
            view.showClearButton()
            view.updateTotalValue(checkoutFormattedValue())
        }
    }

    override fun detachView() {
        this.view = null
    }

    override suspend fun loadProductList() {
        withContext(Dispatchers.IO) {
            val products = repository.getProducts()
            withContext(Dispatchers.Main) {
                view?.showProductList(products)
            }
        }
    }

    override fun addProduct(product: Product, quantity: Int) {
        val checkoutProduct = CheckoutProduct(quantity, product)

        val first = checkoutProducts.firstOrNull {
            it.product == product
        }

        if (first != null) {
            first.quantity = first.quantity + quantity
            view?.updateCheckoutList(first, checkoutProducts.indexOf(first))
        } else {
            checkoutProducts.add(0, checkoutProduct)
            view?.addCheckoutList(checkoutProduct)
        }
        view?.updateTotalValue(checkoutFormattedValue())
        view?.hideEmptyView()
        view?.showClearButton()
    }

    private fun checkoutFormattedValue(): String {
        return MoneyConverter.formatValueInCents(totalValue())
    }

    private fun totalValue(): Int {
        var totalValue = 0
        checkoutProducts.forEach {
            totalValue += it.quantity * it.product.valueInCents
        }
        return totalValue
    }

    override fun removeCheckoutProduct(checkoutProduct: CheckoutProduct) {
        checkoutProducts.remove(checkoutProduct)
        view?.removeProductFromCheckout(checkoutProduct)
        if (checkoutProducts.isEmpty()) {
            view?.showEmptyView()
            view?.hideClearButton()
        }
        view?.updateTotalValue(checkoutFormattedValue())
    }

    override fun checkout() {
        if (checkoutProducts.isNotEmpty()) {
            TemporaryCart.items = checkoutProducts
            view?.goToCheckout(Checkout(checkoutProducts.reversed(), totalValue()))
        } else {
            view?.showEmptyCartFeedback()
        }
    }

    override fun clearCheckout() {
        TemporaryCart.clear()
        checkoutProducts.clear()
        view?.resetCheckoutList()
        view?.updateTotalValue(checkoutFormattedValue())
        view?.showEmptyView()
        view?.hideClearButton()
    }
}