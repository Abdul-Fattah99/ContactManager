package com.adewale.contactmanager



import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import java.lang.ClassCastException

class ProfilePickerDialogFragment: AppCompatDialogFragment() {

    companion object {
        private const val REQUEST_CAMERA = 1
        private const val REQUEST_GALLERY = 2
    }

    private lateinit var listener: ProfilePickerDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.fragment_dialog_image_select, null)

        builder.setView(view)
            .setTitle(getString(R.string.dialog_image_title))
            .setNegativeButton(getString(R.string.dialog_cancel), negativeOnClick)

        val cameraButton = view.findViewById<Button>(R.id.dialog_camera)
        val galleryButton = view.findViewById<Button>(R.id.dialog_gallery)
        cameraButton.setOnClickListener(cameraOptionOnClick)
        galleryButton.setOnClickListener(galleryOptionOnClick)

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ProfilePickerDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "${context.toString()} must implement ProfilePickerDialogListener")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                val extras: Bundle? = data?.extras
                val bitmap: Bitmap = extras?.get("data") as Bitmap
                listener.changeProfileBitmap(bitmap)
            } else {
                Toast.makeText(context, getString(R.string.toast_camera_cancelled), Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == REQUEST_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                val image: Uri? = data?.data
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, image)
                listener.changeProfileBitmap(bitmap)
            } else {
                Toast.makeText(context, getString(R.string.toast_gallery_cancelled), Toast.LENGTH_LONG).show()
            }
        }
        dialog!!.dismiss()
    }

    private val negativeOnClick = DialogInterface.OnClickListener { _, _ ->
        //Do nothing, just dismiss
    }

    private val cameraOptionOnClick = View.OnClickListener {
        if (activity!!.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA)
                }
            }
        } else {
            Toast.makeText(context, getString(R.string.toast_no_camera), Toast.LENGTH_LONG).show()
        }
    }

    private val galleryOptionOnClick = View.OnClickListener {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes: Array<String> = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    interface ProfilePickerDialogListener {
        fun changeProfileBitmap(bitmap: Bitmap)
    }

}
