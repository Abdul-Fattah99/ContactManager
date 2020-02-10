package com.example.contactmanager.data


import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.example.contactmanager.data.dao.ContactDao
import com.example.contactmanager.data.entities.Contact


class ContactRepository(application: Application) {

    private var contactDao: ContactDao
    private var allContacts: LiveData<List<Contact>>

    init {
        val database: ContactDatabase =
            ContactDatabase.getInstance(application.applicationContext)!!
        contactDao = database.contactDao()
        allContacts = contactDao.getAllContacts()
    }

    fun insert(contact: Contact) {
        val insertAsyncTask = InsertAsyncTask(
            contactDao
        ).execute(contact)
    }

    fun update(contact: Contact) {
        val updateAsyncTask = UpdateAsyncTask(
            contactDao
        ).execute(contact)
    }

    fun delete(contact: Contact) {
        val deleteAsyncTask = DeleteAsyncTask(
            contactDao
        ).execute(contact)
    }

    fun deleteAllContacts() {
        val deleteAllAsyncTask = DeleteAllAsyncTask(
            contactDao
        ).execute()
    }

    fun getAllContacts(): LiveData<List<Contact>> {
        return allContacts
    }

    fun getBirthdays(): LiveData<List<Long>> {
        return contactDao.getBirthdays()
    }


    companion object {

        private class InsertAsyncTask(contactDao: ContactDao): AsyncTask<Contact, Unit, Unit>() {
            val dao = contactDao
            override fun doInBackground(vararg params: Contact?) {
                dao.insert(params[0]!!)
            }
        }

        private class UpdateAsyncTask(contactDao: ContactDao): AsyncTask<Contact, Unit, Unit>() {
            val dao = contactDao
            override fun doInBackground(vararg params: Contact?) {
                dao.update(params[0]!!)
            }
        }

        private class DeleteAsyncTask(contactDao: ContactDao): AsyncTask<Contact, Unit, Unit>() {
            val dao = contactDao
            override fun doInBackground(vararg params: Contact?) {
                dao.delete(params[0]!!)
            }
        }

        private class DeleteAllAsyncTask(contactDao: ContactDao): AsyncTask<Contact, Unit, Unit>() {
            val dao = contactDao
            override fun doInBackground(vararg params: Contact?) {
                dao.deleteAllContacts()
            }
        }
    }}

