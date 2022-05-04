package br.com.cohmeia.common

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import br.com.cohmeia.R
import kotlinx.android.synthetic.main.fragment_recharge.*
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

//region View Extensions
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}
//endregion

//region Int Extensions
fun Int.toCents(): Int {
    return this * 100
}
//endregion

//region String Extensions
fun String.pathToUri(): Uri {
    return Uri.fromFile(File(this))
}

fun String.onlyLetters(): String {
    return filter { it.isDigit() }
}

//endregion

//region Context Extensions
fun Context.rawColor(@ColorRes colorRes: Int) : Int {
    return ContextCompat.getColor(this, colorRes)
}
//endregion

//region ImageView Extensions
fun ImageView.tintIn(@ColorRes color: Int) {
    setColorFilter(context.rawColor(color), android.graphics.PorterDuff.Mode.SRC_IN)
}
//endregion

//region Date Extensions
fun Date.toFormat(pattern: String): String {
    return try {
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        simpleDateFormat.format(this)
    } catch (e: Exception) {
        Timber.e("Error parsing date $this to $pattern")
        ""
    }
}

//endregion