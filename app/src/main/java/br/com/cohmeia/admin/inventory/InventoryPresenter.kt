package br.com.cohmeia.admin.inventory

import br.com.cohmeia.login.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventoryPresenter(
    private val inventoryRepository: InventoryRepository,
    private val loginRepository: LoginRepository,
) : InventoryContract.Presenter {

    private var view: InventoryContract.View? = null

    override fun attachView(view: InventoryContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun selectedAddNewProduct() {
        view?.showRegister()
    }

    override fun cancelProductRegister() {
        view?.hideRegister()
    }

    override suspend fun registerProduct(name: String, value: Int, code: String, imagePath: String) {
        val validProductName = validName(name)
        val validValue = validValue(value)
        val validCode = validCode(code)

        val validData = validProductName && validValue && validCode
        if (validData) {
            withContext(Dispatchers.IO) {
                val productId = inventoryRepository.saveProduct(
                    InventoryProduct(
                        name = name,
                        valueInCents = value,
                        code = code,
                        imagePath = imagePath
                    ),
                    loginRepository.getCurrentUser()?.userCpf ?: "-1"
                )
                withContext(Dispatchers.Main) {
                    view?.hideRegister()
                    view?.clearLastRegister()
                    view?.showNewProduct(InventoryProduct(productId.toInt(), name, value, code, imagePath))
                }
            }
        }
    }

    private fun validName(name: String): Boolean {
        val notBlank = name.isNotBlank()
        if (notBlank.not()) {
            view?.showNameError()
        }
        return notBlank
    }

    private fun validValue(value: Int): Boolean {
        val valid = value > 0
        if (valid.not()) {
            view?.showValueError()
        }
        return valid
    }

    private fun validCode(code: String): Boolean {
        val notBlank = code.isNotBlank()
        if (notBlank.not()) {
            view?.showCodeError()
        }
        return notBlank
    }

    override suspend fun loadProducts() {
        view?.showLoading()
        withContext(Dispatchers.IO) {
            val repoProducts = inventoryRepository.getProduct()
            withContext(Dispatchers.Main) {
                view?.hideLoading()
                if (repoProducts.isNotEmpty()) {
                    view?.showProducts(repoProducts)
                } else {
                    view?.showEmptyMessage()
                }
            }
        }
    }

    override suspend fun removeProduct(product: InventoryProduct) {
        inventoryRepository.removeProduct(product)
        withContext(Dispatchers.Main) {
            view?.removeProductFromList(product)
        }
    }

}