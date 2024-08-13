package com.example.flyersofttask

import android.content.ContentUris
import android.content.ContentValues
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ContactDetailActivity : AppCompatActivity() {

    private lateinit var contactRepository: ContactRepository
    private lateinit var contactDetailName: TextView
    private lateinit var contactDetailPhone: TextView

    private lateinit var selectedContact: ContactEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        contactRepository = ContactRepository(AppDatabase.getDatabase(this).contactDao())

        contactDetailName = findViewById(R.id.contactDetailName)
        contactDetailPhone = findViewById(R.id.contactDetailPhone)

        val contactId = intent.getStringExtra("CONTACT_ID")
        if (contactId != null) {
            loadContactDetails(contactId)
        }

        // Register content observer to monitor changes
        contentResolver.registerContentObserver(
            ContactsContract.Contacts.CONTENT_URI,
            true,
            contactObserver
        )

        // Setup buttons' onClickListeners
        setupButtons()
    }

    private fun loadContactDetails(contactId: String) {
        lifecycleScope.launch {
            val contact = contactRepository.getContactById(contactId)
            if (contact != null) {
                contactDetailName.text = contact.name
                contactDetailPhone.text = contact.phoneNumber
            }
        }
    }

    private val contactObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            // Handle additions, deletions, updates here
            syncContactsWithDatabase()
        }
    }

    private fun syncContactsWithDatabase() {
        // Logic to sync contacts with the database
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnEditContact).setOnClickListener {
            // Edit contact logic
            editContact()
        }
        findViewById<Button>(R.id.btnDeleteContact).setOnClickListener {
            // Delete contact logic
            deleteContact()
        }
        findViewById<Button>(R.id.btnUpdateContact).setOnClickListener {
            // Update contact logic
            updateContactInPhoneBook()
        }
    }

    private fun editContact() {
        selectedContact.let {
            val updatedContact = it.copy(name = "Edited Name")
            lifecycleScope.launch {
                contactRepository.update(updatedContact)
                Toast.makeText(this@ContactDetailActivity, "Contact Edited", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteContact() {
        selectedContact.let {
            lifecycleScope.launch {
                contactRepository.delete(it)
                Toast.makeText(this@ContactDetailActivity, "Contact Deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateContactInPhoneBook() {
        selectedContact.let { contact ->
            lifecycleScope.launch {
                // Update contact in your local database
                val updatedContact = contact.copy(name = "Updated Name")
                contactRepository.update(updatedContact)

                // Update the contact in the phone book
                val contactUri = ContentUris.withAppendedId(
                    ContactsContract.RawContacts.CONTENT_URI,
                    contact.id.toLong()
                )

                val contentValues = ContentValues().apply {
                    put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, "Updated Name")
                }

                contentResolver.update(
                    ContactsContract.Data.CONTENT_URI,
                    contentValues,
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(contact.id)
                )

                Toast.makeText(this@ContactDetailActivity, "Contact Updated", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun onContactSelected(contact: ContactEntity) {
        selectedContact = contact
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(contactObserver)
    }
}
