package com.aad.storyapp.helper

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import com.aad.storyapp.R
import com.bumptech.glide.Glide
import com.google.gson.Gson
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/****************************************************
 * Created by Indra Muliana
 * On Saturday, 24/09/2022 10.15
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

private const val TAG = "Utils"
private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun String.withDateFormat(): String {
    try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = format.parse(this) as Date

        return DateFormat.getDateInstance(DateFormat.FULL).format(date)
    } catch (e: Exception) {
        return ""
    }
}

fun <T> ArrayList<T>.addAllFiltered(items: ArrayList<T>) {
    this.addAll(items.filterNot {
        this.contains(it)
    })
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        // .apply(RequestOptions().override(500, 500))
        .placeholder(ContextCompat.getDrawable(this.context, R.drawable.img_placeholder))
        .error(ContextCompat.getDrawable(this.context, R.drawable.img_placeholder))
        .centerCrop()
        .into(this)
}

fun Window.setupView(supportActionBar: ActionBar?) {
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
        setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
    supportActionBar?.hide()
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun <T> errorBodyConverter(clazz: Class<T>, jsonString: String?): T {
    return Gson().fromJson(jsonString, clazz)
}

fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

// For Intent Camera Feature
fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

// For CameraX Feature
fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStamp.jpg")
}

fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)

        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

    return file
}

fun getAddressName(context: Context, lat: Double, lon: Double): String? {
    // Use real device
    var addressName: String? = null
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val list = geocoder.getFromLocation(lat, lon, 1)
        if (list != null && list.size != 0) {
            addressName = list[0].getAddressLine(0)
            Log.d(TAG, "getAddressName: $addressName")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return addressName
}