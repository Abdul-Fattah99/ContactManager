package com.example.contactmanager.viewmodels


import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.contactmanager.data.ContactRepository
import com.example.contactmanager.data.entities.Contact


class ContactListViewModel(application: Application) : AndroidViewModel(application) {

    private var selectedContact: MutableLiveData<Contact> = MutableLiveData<Contact>()
    private val repository: ContactRepository = ContactRepository(application)
    private val allContacts = repository.getAllContacts()

    fun addContact(contact: Contact) {
        repository.insert(contact)
    }

    fun update(contact: Contact) {
        repository.update(contact)
    }

    fun delete(contact: Contact) {
        repository.delete(contact)
    }

    fun deleteAllContacts() {
        repository.deleteAllContacts()
    }

    fun getAllContacts(): LiveData<List<Contact>> {
        return allContacts
    }

    fun setSelectedContact(contact: Contact) {
        this.selectedContact.value = contact
    }

    fun getSelectedContact(): LiveData<Contact> {
        return selectedContact
    }

    fun getFirstAvailableNumber(contact: Contact): String {
        return when {
            contact.phone_home != "" -> contact.phone_home.toString()
            contact.phone_cell != "" -> contact.phone_cell.toString()
            contact.phone_work != "" -> contact.phone_work.toString()
            else -> ""
        }
    }

//    fun getContactProfileImage(contact: Contact): Bitmap? {
//        var bitmap: Bitmap? = null
//        try {
//            bitmap = ImageDataHelper.readImage(ImageDataHelper.IMAGE_PATH, contact.profileImageName!!)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return bitmap
//    }

//    fun deleteContactProfileImage(contact: Contact) {
//        try {
//            ImageDataHelper.deleteImage(ImageDataHelper.IMAGE_PATH, contact.profileImageName!!)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    fun getBirthdays(): LiveData<List<Long>> {
        return repository.getBirthdays()
    }
}
