package br.com.cohmeia.barman

import br.com.cohmeia.barman.checkout.Checkout
import br.com.cohmeia.barman.checkout.CheckoutProduct
import br.com.cohmeia.barman.checkout.Product

interface BarmanContract {

    interface View {
        fun resetCheckoutList()
        fun updateTotalValue(totalValue: String)
        fun goToCheckout(checkout: Checkout)
        fun showProductList(product: List<Product>)
        fun removeProductFromCheckout(checkoutProduct: CheckoutProduct)
        fun addCheckoutList(checkoutProduct: CheckoutProduct)
        fun updateCheckoutList(checkoutProduct: CheckoutProduct, index: Int)
        fun showEmptyView()
        fun hideEmptyView()
        fun showEmptyCartFeedback()
        fun showClearButton()
        fun hideClearButton()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        suspend fun loadProductList()
        fun addProduct(product: Product, quantity: Int)
        fun removeCheckoutProduct(checkoutProduct: CheckoutProduct)
        fun clearCheckout()
        fun checkout()
    }
}