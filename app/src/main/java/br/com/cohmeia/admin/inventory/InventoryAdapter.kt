package br.com.cohmeia.admin.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cohmeia.R
import br.com.cohmeia.common.MoneyConverter
import br.com.cohmeia.common.pathToUri
import br.com.cohmeia.image.ImageHelper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cell_inventory.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class InventoryAdapter : RecyclerView.Adapter<InventoryAdapter.ViewHolder>(), KoinComponent {

    private val productList = mutableListOf<InventoryProduct>()
    private var removeAction: (InventoryProduct) -> Unit = {}

    fun onRemoveSelected(action: (InventoryProduct) -> Unit) {
        removeAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
        holder.view.remove_inventory_product_button.setOnClickListener {
            removeAction(product)
        }
    }

    fun addAll(products: List<InventoryProduct>) {
        this.productList.addAll(products)
        notifyDataSetChanged()
    }

    fun remove(product: InventoryProduct) {
        val position = productList.indexOf(product)
        if (position >= 0) {
            productList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun add(product: InventoryProduct) {
        productList.add(product)
        notifyItemInserted(productList.size - 1)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(product: InventoryProduct) {
            view.inventory_product_name_textView.text = product.name
            view.inventory_product_value_textView.text =
                MoneyConverter.formatValueInCents(product.valueInCents)

            if (product.imagePath.isNotEmpty()) Picasso.get().load(product.imagePath.pathToUri()).into(view.inventory_product_image_imageView)
        }
    }

}