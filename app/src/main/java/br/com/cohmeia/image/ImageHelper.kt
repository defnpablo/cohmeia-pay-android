package br.com.cohmeia.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.ImageView
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageHelper(private val context: Context) {

    private var currentPhotoPath: String = ""

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    fun saveExternalImage(bitmap: Bitmap) {
        createImageFile()?.let {
            val fOut = FileOutputStream(it)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
        }
    }

    fun lastPhotoUriPath() = currentPhotoPath

    fun clearLastPhotoUriPath() {
        currentPhotoPath = ""
    }

    companion object {
        const val FILE_PROVIDER = "br.com.cohmeia.fileprovider"
    }

}