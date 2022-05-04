package br.com.cohmeia.wallet

import android.content.Intent
import br.com.cohmeia.wallet.operation.CreateWallet
import br.com.cohmeia.wallet.operation.RechargeWallet
import br.com.cohmeia.wallet.operation.WalletOperationContract
import br.com.cohmeia.wallet.operation.WalletOperationPresenter
import br.com.cohmeia.wallet.operation.recharge.RechargePaymentType
import br.com.cohmeia.wallet.repository.*
import com.nhaarman.mockitokotlin2.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.Error

class WalletOperationPresenterTest {

    private val WITHDRAW_VALUE = 90
    private val RECHARGE_VALUE = 123
    private val SUCCESS = Success("Success message")
    private val ERROR = Error(WalletNotCreated)
    private val PAYMENT_TYPE = RechargePaymentType.CASH

    private lateinit var presenter: WalletOperationPresenter

    private val view = mock<WalletOperationContract.View>()
    private val repository = mock<WalletRepository>()
    private val intent = mock<Intent>()

    @Before
    fun setUp() {
        presenter = WalletOperationPresenter(repository)
        presenter.attachView(view)
    }

    @Test
    fun `setup recharge operation`() {
        presenter.setupWalletOperation(RechargeWallet(RECHARGE_VALUE, PAYMENT_TYPE))
        verify(view).showRechargeView(RECHARGE_VALUE)
    }

    @Test
    fun `process recharge operation success`() {
        presenter.processWalletData(intent, RechargeWallet(RECHARGE_VALUE, PAYMENT_TYPE), "0")
        val callbackCaptor = argumentCaptor<(Result) -> Unit>()
        verify(repository).recharge(any(), any(), eq(RECHARGE_VALUE), callbackCaptor.capture()).also {
            callbackCaptor.firstValue(SUCCESS)
        }
        verify(view).showResult(eq(SUCCESS))
    }

    @Test
    fun `process recharge operation with empty tag should fail`() {
        presenter.processWalletData(intent, RechargeWallet(RECHARGE_VALUE,PAYMENT_TYPE), "")
        verify(view).showResult(Error(WalletNotCreated))
    }

    @Test
    fun `process recharge operation with wallet not created should fail`() {
        presenter.processWalletData(intent, RechargeWallet(RECHARGE_VALUE, PAYMENT_TYPE), null)
        verify(view).showResult(Error(WalletNotCreated))
    }

    @Test
    fun `process recharge operation with negative amount should fail`() {
        presenter.processWalletData(intent, RechargeWallet(RECHARGE_VALUE, PAYMENT_TYPE), "-1")
        val callbackCaptor = argumentCaptor<(Result) -> Unit>()
        verify(repository).recharge(any(), any(), eq(RECHARGE_VALUE), callbackCaptor.capture()).also {
            callbackCaptor.firstValue(Error(WalletCorrupted))
        }
        verify(view).showResult(Error(WalletCorrupted))
    }

    @Test
    fun `process recharge operation with negative recharge should fail`() {
        presenter.processWalletData(intent, RechargeWallet(-1, PAYMENT_TYPE), "0")
        val callbackCaptor = argumentCaptor<(Result) -> Unit>()
        verify(repository).recharge(any(), any(), eq(-1), callbackCaptor.capture()).also {
            callbackCaptor.firstValue(Error(WalletCorrupted))
        }
        verify(view).showResult(Error(WalletCorrupted))
    }

    @Test
    fun `setup create wallet operation`() {
        presenter.setupWalletOperation(CreateWallet)
        verify(view).showCreateView()
    }

    @Test
    fun `process create wallet success`() {
        presenter.processWalletData(intent, CreateWallet, null)
        val callbackCaptor = argumentCaptor<(Result) -> Unit>()
        verify(repository).create(any(), callbackCaptor.capture()).also {
            callbackCaptor.firstValue(SUCCESS)
        }
        verify(view).showResult(SUCCESS)
    }

    @Test
    fun `process create wallet error`() {
        presenter.processWalletData(intent, CreateWallet, null)
        val callbackCaptor = argumentCaptor<(Result) -> Unit>()
        verify(repository).create(any(), callbackCaptor.capture()).also {
            callbackCaptor.firstValue(Error(WalletCorrupted))
        }
        verify(view).showResult(Error(WalletCorrupted))
    }

    @Test
    fun `process create wallet with already created wallet should fail`() {
        presenter.processWalletData(intent, CreateWallet, "0")
        val callbackCaptor = argumentCaptor<(Result) -> Unit>()
        verify(repository).create(any(), callbackCaptor.capture()).also {
            callbackCaptor.firstValue(Error(WalletAlreadyCreated))
        }
        verify(view).showResult(Error(WalletAlreadyCreated))
    }

//    @Test
//    fun `process withdraw operation success`() {
//        presenter.setupWalletOperation(WithdrawWallet(WITHDRAW_VALUE))
//        val callbackCaptor = argumentCaptor<(Response) -> Unit>()
//        verify(repository).withdraw(eq(WITHDRAW_VALUE), callbackCaptor.capture()).also {
//            callbackCaptor.firstValue(SUCCESS)
//        }
//        verify(view).showWithdrawView(eq(WITHDRAW_VALUE))
//        verify(view).showSuccess()
//    }
//
//    @Test
//    fun `process withdraw operation error`() {
//        presenter.setupWalletOperation(WithdrawWallet(WITHDRAW_VALUE))
//        val callbackCaptor = argumentCaptor<(Response) -> Unit>()
//        verify(repository).withdraw(eq(WITHDRAW_VALUE), callbackCaptor.capture()).also {
//            callbackCaptor.firstValue(ERROR)
//        }
//        verify(view).showWithdrawView(eq(WITHDRAW_VALUE))
//        verify(view).showError()
//    }
//
//
//    @Test
//    fun `process read wallet success`() {
//        presenter.setupWalletOperation(ReadWallet)
//        val callbackCaptor = argumentCaptor<(Response) -> Unit>()
//        verify(repository).read(callbackCaptor.capture()).also {
//            callbackCaptor.firstValue(SUCCESS)
//        }
//        verify(view).showReadView()
//        verify(view).showSuccess()
//    }
//
//    @Test
//    fun `process read wallet error`() {
//        presenter.setupWalletOperation(ReadWallet)
//        val callbackCaptor = argumentCaptor<(Response) -> Unit>()
//        verify(repository).read(callbackCaptor.capture()).also {
//            callbackCaptor.firstValue(ERROR)
//        }
//        verify(view).showReadView()
//        verify(view).showError()
//    }
//
//    @Test
//    fun `cancel operation`() {
//        presenter.cancelOperation()
//        verify(view, only()).close()
//    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(view, repository)
        presenter.detachView()
    }
}
