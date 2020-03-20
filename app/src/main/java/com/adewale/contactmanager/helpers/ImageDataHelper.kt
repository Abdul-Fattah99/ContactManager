package com.adewale.contactmanager.helpers


import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.Toast
import java.io.*
import java.lang.Exception
import java.util.*

class ImageDataHelper {

    companion object {

        private const val PROFILE_DIRECTORY = "profileImages"

        const val IMAGE_PATH = "data/data/com.adewale.contactmanager/" +
                "app_${PROFILE_DIRECTORY}/"

        /**
         * Saves a bitmap image to internal storage
         *
         * @param bitmap Bitmap image to save
         * @param context Context of the application
         * @return String name of the file saved
         */
        fun saveImage(bitmap: Bitmap, context: Context): String {
            val name = "${UUID.randomUUID()}_profile.png"
            var saveAsync = SaveImageAsyncTask(bitmap, context).execute(name)
            return name
        }

        /**
         * Reads and returns an image from internal storage
         *
         * @param path String Path to where the image is stored
         * @param name String Name of the image
         * @return Bitmap Image that was read
         */
        fun readImage(path: String, name: String): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                val image = File(path, name)
                bitmap = BitmapFactory.decodeStream(FileInputStream(image))
            } catch (e: FileNotFoundException) {
                bitmap = null
                e.printStackTrace()
            }
            return bitmap
        }

        /**
         * Deletes an image from internal storage
         *
         * @param path String Path to where the image is stored
         * @param name String Name of the image
         */
        fun deleteImage(path: String, name: String) {
            val deleteAsyncTask = DeleteImageAsyncTask(path, name).execute()
        }

        /**
         * Saves an image to local storage using a background thread
         *
         * @param btmp Bitmap image to save
         * @param cntxt Context of the application
         */
        private class SaveImageAsyncTask(btmp: Bitmap, cntxt: Context): AsyncTask<String, Unit, Unit>() {
            @SuppressLint("StaticFieldLeak")
            val context = cntxt
            val bitmap = btmp
            override fun doInBackground(vararg params: String?) {
                val contextWrapper = ContextWrapper(context.applicationContext)
                val directory: File = contextWrapper.getDir("profileImages", Context.MODE_PRIVATE)
                val path = File(directory, params[0].toString())
                var outputStream: FileOutputStream? = null

                try {
                    outputStream = FileOutputStream(path)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not save file", Toast.LENGTH_SHORT).show()
                } finally {
                    try {
                        outputStream?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        /**
         * Deletes an image from internal storage using a background thread
         *
         * @param path String Path to where the image is stored
         * @param name String Name of the image
         */
        private class DeleteImageAsyncTask(path: String, name: String): AsyncTask<Unit, Unit, Unit>() {
            val filePath = path
            val fileName = name
            override fun doInBackground(vararg params: Unit?) {
                try {
                    val image = File(filePath, fileName)
                    image.delete()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
