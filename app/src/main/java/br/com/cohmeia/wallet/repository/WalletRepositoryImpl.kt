package br.com.cohmeia.wallet.repository

import android.content.Intent
import br.com.cohmeia.analytics.CohmeiaAnalytics
import br.com.cohmeia.barman.checkout.Checkout
import br.com.cohmeia.common.MoneyConverter
import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.database.entities.ProductSaleEntity
import br.com.cohmeia.database.entities.RechargeEntity
import br.com.cohmeia.database.entities.SaleEntity
import br.com.cohmeia.database.entities.WalletEntity
import br.com.cohmeia.login.LoginRepository
import br.com.cohmeia.nfc.core.NfcData
import br.com.cohmeia.nfc.core.NfcService
import br.com.cohmeia.wallet.operation.CreateWallet
import br.com.cohmeia.wallet.operation.RechargeWallet
import br.com.cohmeia.wallet.operation.WithdrawWallet
import br.com.cohmeia.wallet.operation.recharge.RechargePaymentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class WalletRepositoryImpl(
    private val db: CohmeiaDatabase,
    private val loginRepository: LoginRepository,
    private val analytics: CohmeiaAnalytics
) : WalletRepository {

    private val ioDispatcher = CoroutineScope(Dispatchers.IO)

    override fun recharge(
        intent: Intent,
        currentAmount: Int,
        add: Int,
        paymentType: RechargePaymentType,
        callback: (Result) -> Unit
    ) {
        if (currentAmount >= 0 && add > 0) {
            val newAmount = currentAmount + add
            val dataToTag = writeDataToTag(intent, newAmount)
            if (dataToTag != null) {
                ioDispatcher.launch {
                    db.rechargeDao().insert(
                        RechargeEntity(
                            employeeId = loginRepository.getCurrentUser()?.userCpf ?: "-",
                            valueInCents = add,
                            tagId = dataToTag.tagId,
                            paymentMethod = paymentType.name,
                            syncDate = 0,
                        )
                    )
                }
                callback(Success("Recarga concluida com sucesso!\n\n${MoneyConverter.formatValueInCents(newAmount)}"))
            } else {
                analytics.logErrorWritingTagForOperation(RechargeWallet(0, RechargePaymentType.CASH))
                callback(Error(WalletCorrupted))
            }
        } else {
            analytics.logErrorRechargingWallet(currentAmount, add)
            callback(Error(WalletCorrupted))
        }
    }

    override fun withdraw(intent: Intent, currentAmount: Int, checkout: Checkout, callback: (Result) -> Unit) {
        val saleAmount = checkout.totalInCents
        val newAmount = currentAmount - saleAmount
        Timber.d("Withdraw operation :: current amount $currentAmount - withdraw $saleAmount = new balance $newAmount")
        if (newAmount >= 0) {
            val dataToTag = writeDataToTag(intent, newAmount)
            if (dataToTag != null) {
                ioDispatcher.launch {
                    val saleId = db.saleDao().insert(
                        SaleEntity(
                            employeeId = loginRepository.getCurrentUser()?.userCpf ?: "-1",
                            valueInCents = saleAmount,
                            tagId = dataToTag.tagId,
                            syncDate = 0,
                        )
                    )
                    val sale = db.saleDao().getById(saleId)
                    checkout.checkoutProducts.forEach { checkoutProd ->
                        db.productSaleDao().insert(
                            ProductSaleEntity(checkoutProd.product.code, checkoutProd.quantity, sale.externalId, syncDate = 0)
                        )
                    }
                }

                callback(
                    Success(
                        "Cobrança de " +
                                "${MoneyConverter.formatValueInCents(saleAmount)} " +
                                "concluida com sucesso!\n\n " +
                                "Novo saldo ${MoneyConverter.formatValueInCents(newAmount)}"
                    )
                )
            } else {
                analytics.logErrorWritingTagForOperation(WithdrawWallet(Checkout(listOf(), 0)))
                callback(Error(WalletCorrupted))
            }
        } else {
            callback(Error(InsufficientBalance(currentAmount, saleAmount)))
        }
    }

    override fun create(intent: Intent, initialBalance: Int, callback: (Result) -> Unit) {
        val data = NfcService.retrieveNFCMessage(intent)
        if (data != null) {
            callback(Error(WalletAlreadyCreated))
            return
        }

        val dataToTag = writeDataToTag(intent, initialBalance)
        if (dataToTag != null) {
            Timber.d("create wallet ${dataToTag.tagId} | ${dataToTag.content}")
            ioDispatcher.launch {
                db.walletDao().insert(
                    WalletEntity(
                        tagId = dataToTag.tagId,
                        employeeId = loginRepository.getCurrentUser()?.userCpf ?: "-1",
                        syncDate = 0,
                    )
                )
            }
            callback(Success("Carteira criada com sucesso"))
        } else {
            analytics.logErrorWritingTagForOperation(CreateWallet)
            callback(Error(WalletCorrupted))
        }
    }

    private fun writeDataToTag(tagIntent: Intent, newAmount: Int): NfcData? {
        return NfcService.createNFCMessage(newAmount.toString(), tagIntent)
    }
}