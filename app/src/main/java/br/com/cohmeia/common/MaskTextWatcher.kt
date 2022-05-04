package br.com.cohmeia.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class MaskTextWatcher(
    private val mask: String,
    private val ediTxt: EditText) : TextWatcher{

    private var old = ""

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val str = unmask(s.toString())
        var result = ""
        if (old == str) {
            return
        }

        var i = 0
        for (m in mask.toCharArray()) {
            if (m != '#' && str.length >= old.length) {
                result += m
                continue
            }
            try {
                result += str.get(i)
            } catch (e: Exception) {
                break
            }

            i++
        }

        old = unmask(result)
        ediTxt.setText(result)
        ediTxt.setSelection(ediTxt.text.toString().length)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(arg0: Editable) {}

    private fun unmask(s: String?): String {
        return s?.replace("[.]".toRegex(), "")?.replace("[-]".toRegex(), "")?.replace("[/]".toRegex(), "")
            ?.replace("[(]".toRegex(), "")?.replace("[)]".toRegex(), "")?.replace("[ ]".toRegex(), "")
            ?: ""
    }

}