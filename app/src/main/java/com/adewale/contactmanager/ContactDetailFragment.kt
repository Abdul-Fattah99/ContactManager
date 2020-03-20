package com.adewale.contactmanager

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.adewale.contactmanager.data.entities.Contact
import com.adewale.contactmanager.viewmodels.ContactListViewModel
import kotlinx.android.synthetic.main.fragment_contact_detail.*
import java.lang.Exception
import java.util.*

class ContactDetailFragment : Fragment() {

    private lateinit var viewModel: ContactListViewModel

    private lateinit var phone: String
    private var numberBuffer = ""

    companion object {
        fun newInstance(): ContactDetailFragment {
            return ContactDetailFragment()
        }
        private const val REQUEST_CALL = 1

        private const val BUTTON_CALL = 10
        private const val BUTTON_TEXT = 20
        private const val BUTTON_EMAIL = 30

        const val FRAGMENT_TAG = "ContactDetailFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_detail, container, false)
        viewModel = (activity as MainActivity).getViewModel()
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val contact = viewModel.getSelectedContact()

        initialDataSetup(contact.value!!)

        //contact_profile_image_view.setImageBitmap(viewModel.getContactProfileImage(contact.value!!))

        phone = contact.value?.phone_home.toString()
        call_home_phone_button.setOnClickListener(ButtonOnClickListener(phone, BUTTON_CALL))
        text_home_phone_button.setOnClickListener(ButtonOnClickListener(phone, BUTTON_TEXT))
        phone = contact.value?.phone_cell.toString()
        call_cell_phone_button.setOnClickListener(ButtonOnClickListener(phone, BUTTON_CALL))
        text_cell_phone_button.setOnClickListener(ButtonOnClickListener(phone, BUTTON_TEXT))
        phone = contact.value?.phone_work.toString()
        call_work_phone_button.setOnClickListener(ButtonOnClickListener(phone, BUTTON_CALL))
        text_work_phone_button.setOnClickListener(ButtonOnClickListener(phone, BUTTON_TEXT))
        email_button.setOnClickListener(
            ButtonOnClickListener(contact.value?.email.toString(), BUTTON_EMAIL))
        activity?.invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        activity!!.menuInflater.inflate(R.menu.contact_details_menu, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.contact_details_menu_edit -> {
            val fragment
                    = ContactAddEditFragment.newInstance(ContactAddEditFragment.EDIT_REQUEST)
            val transaction = activity!!
                .supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
                    R.anim.enter_from_top, R.anim.exit_to_bottom)
                replace(this@ContactDetailFragment.id, fragment, ContactAddEditFragment.FRAGMENT_TAG)
                addToBackStack(null)
            }
            transaction.commit()
            true
        }
        R.id.contact_details_menu_delete -> {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Delete Contact?")
            alertDialog.setMessage("Are you sure you want to delete this contact?\n" +
                    "This action is permanent.")
            alertDialog.setPositiveButton("YES"){_, _ ->
                viewModel.delete(viewModel.getSelectedContact().value!!)
                val fragment = ContactsListFragment.newInstance()
                val transaction = activity!!
                    .supportFragmentManager.beginTransaction().apply {
                    replace(this@ContactDetailFragment.id, fragment)
                }
                transaction.commit()
            }
            alertDialog.setNegativeButton("CANCEL"){dialog, id ->
                dialog.dismiss()
            }
            alertDialog.show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                call(numberBuffer)
            } else {
                Toast.makeText(context, "Call permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun call(number: String) {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
            numberBuffer = number
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL
            )
        } else {
            numberBuffer = ""
            if (number != "") {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${number}"))
                startActivity(intent)
            } else {
                Toast.makeText(context, "No phone number available", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun text(number: String) {
        try {
            if (number != "") {
                val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null))
                intent.putExtra("sms_body", "")
                startActivity(intent)
            } else {
                Toast.makeText(context, "No phone number available", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "No available messaging app", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Launches an Email Intent given an email address
     *
     * @param addr String
     */
    private fun email(addr: String) {
        try {
            if (addr != "") {
                val intent = Intent(Intent.ACTION_SEND)
                val array = arrayOf(addr)
                intent.putExtra(Intent.EXTRA_EMAIL, array)
                intent.type = "message/rfc822"
                startActivity(Intent.createChooser(intent, "Pick an Email Client"))
            } else { //This condition should never be visible
                Toast.makeText(context, "No email address available", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "No available email app", Toast.LENGTH_LONG).show()
        }
    }
    private fun initialDataSetup(contact: Contact) {
        if (contact.first_name != "" || contact.last_name != "") {
            name_text_view.visibility = View.VISIBLE
            if (contact.first_name != "" && contact.last_name != "") {
                name_text_view.text = "${contact.first_name} ${contact.last_name}"
            } else {
                if (contact.first_name == "") {
                    name_text_view.text = contact.last_name.toString()
                } else {
                    name_text_view.text = contact.first_name.toString()
                }
            }
        }

        if (contact.email != "") {
            email_relative_layout.visibility = View.VISIBLE
            email_text_text_view.text = contact.email.toString()
        }

        if (contact.phone_home != "" || contact.phone_cell != "" || contact.phone_work != "") {
            phone_relative_layout.visibility = View.VISIBLE
            if (contact.phone_home != "") {
                home_phone_linear_layout.visibility = View.VISIBLE
                home_phone_text_view.text = contact.phone_home.toString()
            }
            if (contact.phone_cell != "") {
                cell_phone_linear_layout.visibility = View.VISIBLE
                cell_phone_text_view.text = contact.phone_cell.toString()
            }
            if (contact.phone_work != "") {
                work_phone_linear_layout.visibility = View.VISIBLE
                work_phone_text_view.text = contact.phone_work.toString()
            }
        }

        if (contact.address != "") {
            address_relative_layout.visibility = View.VISIBLE
            address_text_text_view.text = contact.address.toString()
        }

        if (contact.birthday != null) {
            birthday_relative_layout.visibility  = View.VISIBLE
            val cal = Calendar.getInstance()
            cal.timeInMillis = contact.birthday!!.time
            birthday_text_text_view.text = "${cal.get(Calendar.DAY_OF_MONTH)}" +
                    "/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
        }
    }
    inner class ButtonOnClickListener(
        var value: String,
        var type: Int): View.OnClickListener {
        override fun onClick(v: View?) {
            when(type) {
                BUTTON_CALL -> call(value)
                BUTTON_TEXT -> text(value)
                BUTTON_EMAIL -> email(value)
            }
        }
    }
}