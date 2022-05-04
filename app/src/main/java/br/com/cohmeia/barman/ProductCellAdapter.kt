package br.com.cohmeia.barman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cohmeia.R
import br.com.cohmeia.barman.checkout.CheckoutProduct
import br.com.cohmeia.barman.checkout.Product
import br.com.cohmeia.common.MoneyConverter
import br.com.cohmeia.common.pathToUri
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cell_product.view.*

class ProductCellAdapter :
    RecyclerView.Adapter<ProductCellAdapter.ViewHolder>() {

    private var products = listOf<CheckoutProduct>()
    var addAction: (Product, Int) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_product, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkoutProduct = products[position]
        holder.bind(checkoutProduct).also {
            it.cell_add_product_button.setOnClickListener {
                addAction(checkoutProduct.product, checkoutProduct.quantity)
            }
        }
    }

    fun addProducts(products: List<Product>) {
        this.products = products.map {
            CheckoutProduct(1, it)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(product: CheckoutProduct): View {
            with(itemView) {
                cell_product_name_textView.text = product.product.name
                cell_product_price_textView.text = MoneyConverter.formatValueInCents(product.product.valueInCents)
                cell_product_quantity_textView.text = product.quantity.toString()
                cell_product_increase_quantity_button.setOnClickListener {
                    if (product.quantity < 999) {
                        product.quantity++
                        cell_product_quantity_textView.text = product.quantity.toString()
                    }
                }
                cell_product_decrease_quantity_button.setOnClickListener {
                    if (product.quantity > 1) {
                        product.quantity--
                        cell_product_quantity_textView.text = product.quantity.toString()
                    }
                }
                cell_add_product_button.setOnClickListener {

                }

                product.product.imageUri.takeIf { it.isNotEmpty() }?.let {
                    cell_product_imageView.imageTintList = null
                    Picasso.get().load(it.pathToUri()).into(cell_product_imageView)
                }

            }
            return itemView
        }

    }
}