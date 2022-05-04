package br.com.cohmeia.wallet.operation.recharge

enum class RechargePaymentType(val description: String, val typeId: Int) {
    CASH("Dinheiro", 0),
    DEBIT("Débito", 1),
    CREDIT("Crédito", 2),
    STAFF("Produção", 3);

    companion object {
        fun valuesAsString(): Array<CharSequence> {
            return values().map {
                it.description
            }.toTypedArray()
        }
    }
}