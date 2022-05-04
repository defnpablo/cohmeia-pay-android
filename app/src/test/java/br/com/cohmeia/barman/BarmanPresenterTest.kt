package br.com.cohmeia.barman

import br.com.cohmeia.barman.repository.ProductRepository
import br.com.cohmeia.barman.checkout.Checkout
import br.com.cohmeia.barman.checkout.CheckoutProduct
import br.com.cohmeia.barman.checkout.Product
import com.nhaarman.mockitokotlin2.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class BarmanPresenterTest {

    private val productA = Product(1, "Prod_A", 1000)
    private val quantityA = 1

    private val productB = Product(2, "Prod_B", 2500)
    private val quantityB = 2

    private val checkoutSingle = Checkout(listOf(CheckoutProduct(quantityA, productA)), 1000)
    private val checkoutMultiple = Checkout(
        listOf(
            CheckoutProduct(quantityA, productA),
            CheckoutProduct(quantityB, productB)
        ), 6000
    )

    private lateinit var presenter: BarmanPresenter
    private val repository = mock<ProductRepository>()
    private val view = mock<BarmanContract.View>()

    @Before
    fun setUp() {
        presenter = BarmanPresenter(repository)
        presenter.attachView(view)
    }

    @Test
    fun `attach view update view`() {
        presenter.addProduct(productA, 1)
        presenter.attachView(view)
        verify(view, atLeastOnce()).hideEmptyView()
        verify(view, atLeastOnce()).showClearButton()
        verify(view, atLeastOnce()).updateTotalValue(any())
    }

    @Test
    fun `load product list`() {
        presenter.loadProductList()
        verify(repository).getProducts()
        verify(view).showProductList(any())
        verifyNoMoreInteractions(repository, view)
    }

    @Test
    fun `go to checkout with at least one product selected`() {
        presenter.addProduct(productA, quantityA)
        presenter.checkout()
        verify(view).goToCheckout(eq(checkoutSingle))
    }

    @Test
    fun `go to checkout with multiple product selected`() {
        presenter.addProduct(productA, quantityA)
        presenter.addProduct(productB, quantityB)
        presenter.checkout()
        verify(view).goToCheckout(eq(checkoutMultiple))
    }

    @Test
    fun `try to go checkout with no product selected`() {
        presenter.checkout()
        verify(view).showEmptyCartFeedback()
        verifyNoMoreInteractions(repository, view)
    }

    @Test
    fun `clear checkout`() {
        presenter.addProduct(productA,1)
        presenter.addProduct(productB,2)
        presenter.clearCheckout()
        verify(view).resetCheckoutList()
        verify(view, atLeastOnce()).updateTotalValue(any())
        verify(view).showEmptyView()
        verify(view).hideClearButton()
    }

    @After
    fun tearDown() {
        presenter.detachView()
    }
}