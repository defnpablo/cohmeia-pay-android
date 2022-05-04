package br.com.cohmeia.wallet.repository

import java.io.Serializable

sealed class Result : Serializable

data class Success(val payload: String) : Result()
data class Error(val errorType: WalletError) : Result()
