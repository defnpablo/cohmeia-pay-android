package br.com.cohmeia.wallet.operation.read

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.After
import org.junit.Before
import org.junit.Test

class ReadWalletPresenterTest {

    private lateinit var presenter: ReadWalletPresenter
    private val view = mock<ReadWalletContract.View>()

    @Before
    fun setUp() {
        presenter = ReadWalletPresenter()
        presenter.attachView(view)
    }

    @Test
    fun `show balance for created wallet`() {
        presenter.readWallet(12)
        verify(view).showBalanceDialog(12)
    }

    @Test
    fun `show balance for not created wallet should ask to create`() {
        presenter.readWallet(-1)
        verify(view).showCreateWalletDialog()
    }

    @After
    fun tearDown() {
        presenter.detachView()
        verifyNoMoreInteractions(view)
    }
}