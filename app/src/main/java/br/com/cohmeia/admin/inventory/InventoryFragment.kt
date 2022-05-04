package br.com.cohmeia.admin.inventory

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.cohmeia.R
import br.com.cohmeia.common.MoneyConverter
import br.com.cohmeia.common.hide
import br.com.cohmeia.common.pathToUri
import br.com.cohmeia.common.show
import br.com.cohmeia.image.ImageHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cell_checkout_resume.*
import kotlinx.android.synthetic.main.fragment_inventory.*
import kotlinx.android.synthetic.main.layout_inventory_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber


class InventoryFragment : Fragment(), InventoryContract.View {

    private val presenter: InventoryContract.Presenter by inject()
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var inventoryProductsAdapter = InventoryAdapter()

    private lateinit var photoResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var imageHelper: ImageHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageHelper = ImageHelper(requireContext())
        photoResult = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                product_image_preview_imageView.show()
                Picasso.get().load(imageHelper.lastPhotoUriPath().pathToUri()).into(product_image_preview_imageView)
            }
        }
        galleryResult = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                product_image_preview_imageView.show()
                val selectedImage = result.data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImage)
                imageHelper.saveExternalImage(bitmap)
                Picasso.get().load(imageHelper.lastPhotoUriPath().pathToUri()).into(product_image_preview_imageView)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBehavior = BottomSheetBehavior.from(inventory_bottomSheet)
        bottomSheetBehavior?.apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        setupRecyclerView()

        CoroutineScope(Dispatchers.Main).launch {
            presenter.loadProducts()
        }

        inventory_fab.setOnClickListener {
            presenter.selectedAddNewProduct()
        }

        cancel_inventory_product_register_button.setOnClickListener {
            presenter.cancelProductRegister()
        }

        product_image_chooser_button.setOnClickListener {
            imageHelper.createImageFile()?.let {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    ImageHelper.FILE_PROVIDER,
                    it
                )
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                photoResult.launch(takePictureIntent)
            }

        }

        product_image_file_chooser_button.setOnClickListener {
            imageHelper.createImageFile()?.let {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    ImageHelper.FILE_PROVIDER,
                    it
                )
                val choosePickIntent = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                galleryResult.launch(choosePickIntent)
            }
        }

        inventory_product_register_button.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                presenter.registerProduct(
                    inventory_product_name_editText.text.toString(),
                    MoneyConverter.stringValueToInt(inventory_product_value_editText.text.toString()),
                    inventory_product_code_editText.text.toString(),
                    imageHelper.lastPhotoUriPath()
                )
            }
        }

        inventory_product_name_editText.addTextChangedListener {
            inventory_product_name_textInputLayout.error = null
        }
        inventory_product_value_editText.addTextChangedListener {
            inventory_product_value_textInputLayout.error = null
        }
        inventory_product_value_editText.addTextChangedListener(object : TextWatcher {
            var changing = false
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (changing) return

                changing = true
                val intValue = MoneyConverter.stringValueToInt(s.toString())
                val formatValueInCents = MoneyConverter.formatValueInCents(intValue)
                inventory_product_value_editText.setText(formatValueInCents)
                inventory_product_value_editText.setSelection(inventory_product_value_editText.text.toString().length)
                changing = false
            }

        })

    }

    private fun setupRecyclerView() {
        inventoryProductsAdapter.onRemoveSelected {
            askToRemoveProduct(it) {
                CoroutineScope(Dispatchers.IO).launch {
                    presenter.removeProduct(it)
                }
            }
        }

        with(inventory_recyclerView) {
            adapter = inventoryProductsAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun askToRemoveProduct(product: InventoryProduct, removeAction: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog_Cohmeia)
            .setTitle("Atenção")
            .setMessage("Deseja remover o produto: ${product.name} - ${product.code}")
            .setNeutralButton("Cancelar"){ dialog, _ -> dialog.dismiss()}
            .setPositiveButton("Remover") {_,_ -> removeAction()}
            .show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.attachView(this)

    }

    override fun onDetach() {
        super.onDetach()
        presenter.detachView()
    }

    //region InventoryContract.View
    override fun showLoading() {
        inventory_progressBar.show()
    }

    override fun hideLoading() {
        inventory_progressBar.hide()
    }

    override fun showEmptyMessage() {
        inventory_empty_textView.show()
    }

    override fun showProducts(products: List<InventoryProduct>) {
        inventory_empty_textView.hide()
        inventoryProductsAdapter.addAll(products)
    }

    override fun showRegister() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        inventory_fab.hide()
        inventory_footer_view.show()
    }

    override fun hideRegister() {
        hideKeyboard()
        Handler().postDelayed({
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            inventory_fab.show()
            inventory_footer_view.hide()
        }, 100)
    }

    override fun showNameError() {
        inventory_product_name_textInputLayout.error = "Nome inválido"
    }

    override fun showValueError() {
        inventory_product_value_textInputLayout.error = "Valor inválido"
    }

    override fun showCodeError() {
        inventory_product_code_textInputLayout.error = "Código inválido"
    }

    override fun removeProductFromList(product: InventoryProduct) {
        inventoryProductsAdapter.remove(product)
        if (inventoryProductsAdapter.itemCount <= 0) {
            showEmptyMessage()
        }
    }

    override fun showNewProduct(product: InventoryProduct) {
        inventoryProductsAdapter.add(product)
        inventory_empty_textView.hide()
    }

    override fun clearLastRegister() {
        imageHelper.clearLastPhotoUriPath()
        product_image_preview_imageView.setImageDrawable(null)
        inventory_product_name_editText.setText("")
        inventory_product_value_editText.setText("")
        inventory_product_code_editText.setText("")
    }
    //endregion

    fun hideKeyboard() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = InventoryFragment()
    }
}
