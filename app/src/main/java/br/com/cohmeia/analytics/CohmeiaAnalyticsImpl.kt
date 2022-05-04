package br.com.cohmeia.analytics

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import br.com.cohmeia.wallet.operation.WalletOperation
import com.google.firebase.analytics.FirebaseAnalytics

class CohmeiaAnalyticsImpl(val context: Context) : CohmeiaAnalytics {

    private val EVENT_ERROR_CREATING = "error_creating_wallet"
    private val EVENT_ERROR_RECHARGING = "error_recharging_wallet"
    private val EVENT_ERROR_WRITING_TAG = "error_writing_tag"
    private val EVENT_OPERATION_REQUESTED = "operation_requested"
    private val EVENT_OPERATION_CANCELLED = "operation_cancelled"

    private val KEY_ATTR_WALLET_OPERATION = "wallet_operation"
    private val KEY_ATTR_TAG_CURRENT_AMOUNT = "tag_current_amount"
    private val KEY_ATTR_RECHARGE_AMOUNT = "recharge_amount"

    override fun logOperationRequested(walletOperation: WalletOperation) {
        logEvent(
            EVENT_OPERATION_REQUESTED, bundleOf(
                Pair(KEY_ATTR_WALLET_OPERATION, walletOperation.javaClass.simpleName)
            )
        )
    }

    override fun logOperationCancel(walletOperation: WalletOperation) {
        logEvent(
            EVENT_OPERATION_CANCELLED, bundleOf(
                Pair(KEY_ATTR_WALLET_OPERATION, walletOperation.javaClass.simpleName)
            )
        )
    }

    override fun logErrorCreatingWallet() {
        logEvent(EVENT_ERROR_CREATING)
    }

    override fun logErrorRechargingWallet(tagCurrentAmount: Int, rechargeAmount: Int) {
        logEvent(
            EVENT_ERROR_RECHARGING, bundleOf(
                Pair(KEY_ATTR_TAG_CURRENT_AMOUNT, tagCurrentAmount),
                Pair(KEY_ATTR_RECHARGE_AMOUNT, rechargeAmount)
            )
        )
    }

    override fun logErrorWritingTagForOperation(walletOperation: WalletOperation) {
        logEvent(
            EVENT_ERROR_WRITING_TAG, bundleOf(
                Pair(KEY_ATTR_WALLET_OPERATION, walletOperation.javaClass.simpleName)
            )
        )
    }

    private fun logEvent(event: String, attributes: Bundle? = null) {
        FirebaseAnalytics.getInstance(context).logEvent(event, attributes)
    }

}