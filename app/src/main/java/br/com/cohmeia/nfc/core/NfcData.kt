package br.com.cohmeia.nfc.core

data class NfcData(
    val tagId: String,
    val content: String
) {

    override fun toString(): String {
        return "$tagId|$content"
    }

}

fun String.toNfcData(): NfcData? {
    val split = split("|")
    if (split.size != 2) return null

    return NfcData(split[0], split[1])
}