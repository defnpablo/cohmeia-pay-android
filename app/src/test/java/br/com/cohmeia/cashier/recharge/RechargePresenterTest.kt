package br.com.cohmeia.cashier.recharge

import br.com.cohmeia.wallet.operation.recharge.RechargeContract
import br.com.cohmeia.wallet.operation.recharge.RechargePaymentType
import br.com.cohmeia.wallet.operation.recharge.RechargePresenter
import com.nhaarman.mockitokotlin2.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class RechargePresenterTest {

    private lateinit var presenter: RechargePresenter
    private val view = mock<RechargeContract.View>()

    @Before
    fun setUp() {
        presenter = RechargePresenter()
        presenter.attachView(view)
    }

    @Test
    fun `clear selected`() {
        presenter.clear()
        verify(view, times(2)).updateCashView(0)
    }

    @Test
    fun `recharge selected with zero value`() {
        presenter.validateRecharge()
        verify(view).updateCashView(0)
        verify(view).showInvalidRechargeMessage()
    }

    @Test
    fun `recharge selected with non zero value`() {
        val validCashValue = 25
        presenter.addCash(validCashValue)
        presenter.doRecharge(RechargePaymentType.CASH)
        verify(view).updateCashView(0)
        verify(view, times(1)).updateCashView(validCashValue)
        verify(view, times(1)).startNfcRecharge(validCashValue, RechargePaymentType.CASH)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(view)
    }
}