package br.com.cohmeia.admin.inventory

interface InventoryContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showEmptyMessage()
        fun showProducts(products: List<InventoryProduct>)
        fun showRegister()
        fun hideRegister()
        fun showNameError()
        fun showValueError()
        fun showCodeError()
        fun removeProductFromList(product: InventoryProduct)
        fun showNewProduct(product: InventoryProduct)
        fun clearLastRegister()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun selectedAddNewProduct()
        fun cancelProductRegister()
        suspend fun registerProduct(name: String, value: Int, code: String, imagePath: String)
        suspend fun loadProducts()
        suspend fun removeProduct(product: InventoryProduct)
    }

}