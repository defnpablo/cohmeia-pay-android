package br.com.cohmeia.barman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cohmeia.R
import br.com.cohmeia.barman.checkout.CheckoutProduct
import br.com.cohmeia.common.MoneyConverter
import kotlinx.android.synthetic.main.cell_checkout_resume.view.*
import kotlinx.android.synthetic.main.cell_wallet_operation_checkout.view.*

class CheckoutProductsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val checkoutProducts = mutableListOf<CheckoutProduct>()
    var removeAction: (CheckoutProduct, Int) -> Unit = { _, _ -> }
    var enableRemove = true
    private val checkoutType = 0
    private val walletOperationType = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == checkoutType) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_checkout_resume, parent, false)
            CheckoutViewHolder(view)
        }else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_wallet_operation_checkout, parent, false)
            WalletOperationViewHolder(view)
        }
    }

    override fun getItemCount(): Int = checkoutProducts.size

    override fun getItemViewType(position: Int): Int {
        return if (enableRemove) checkoutType else walletOperationType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val checkoutProduct = checkoutProducts[position]
        if (holder.itemViewType == checkoutType) {
            (holder as CheckoutViewHolder).bind(checkoutProduct).also {
                it.remove_product_imageButton.setOnClickListener {
                    removeAction(checkoutProduct, position)
                }
            }
        } else {
            (holder as WalletOperationViewHolder).bind(checkoutProduct)
        }

    }

    fun add(checkoutProduct: CheckoutProduct) {
        checkoutProducts.add(0, checkoutProduct)
        notifyItemInserted(0)
    }

    fun remove(checkoutProduct: CheckoutProduct) {
        val position = checkoutProducts.indexOf(checkoutProduct)
        checkoutProducts.removeAt(position)
        notifyItemRemoved(position)
    }

    fun update(checkoutProduct: CheckoutProduct, index: Int) {
        checkoutProducts[index] = checkoutProduct
        notifyItemChanged(index)
    }

    fun removeAll() {
        checkoutProducts.clear()
        notifyDataSetChanged()
    }

    fun addAll(checkoutProducts: List<CheckoutProduct>) {
        this.checkoutProducts.addAll(0, checkoutProducts)
        notifyDataSetChanged()
    }

    class CheckoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(checkoutProduct: CheckoutProduct): View {
            itemView.quantity_textView.text = "${checkoutProduct.quantity}x"
            itemView.product_name_textView.text = "${checkoutProduct.product.name}"
            itemView.product_price_total_textView.text =
                "${MoneyConverter.formatValueInCents(checkoutProduct.product.valueInCents)}"
            return itemView
        }

    }

    class WalletOperationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(checkoutProduct: CheckoutProduct): View {
            itemView.wallet_quantity_textView.text = "${checkoutProduct.quantity}x"
            itemView.wallet_product_name_textView.text = "${checkoutProduct.product.name}"
            itemView.wallet_product_price_total_textView.text =
                "${MoneyConverter.formatValueInCents(checkoutProduct.product.valueInCents)}"
            return itemView
        }

    }

}
