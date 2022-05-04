package br.com.cohmeia.wallet.repository

import androidx.annotation.StringRes
import br.com.cohmeia.R

sealed class WalletError(@StringRes val messageError: Int)

object WalletNotCreated : WalletError(R.string.error_wallet_not_created)
object WalletAlreadyCreated : WalletError(R.string.error_wallet_already_created)
object WalletCorrupted : WalletError(R.string.error_wallet_corrupted)

data class InsufficientBalance(
    val balanceInCents: Int,
    val checkoutValueInCents: Int
) : WalletError(R.string.error_insufficient_balance)
