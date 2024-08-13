package com.example.flyersofttask

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contactRepository: ContactRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contactRepository = ContactRepository(AppDatabase.getDatabase(this).contactDao())

        contactAdapter = ContactAdapter { contact ->
            val intent = Intent(this, ContactDetailActivity::class.java)
            intent.putExtra("CONTACT_ID", contact.id)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactAdapter

        checkPermissionAndLoadContacts()
        setupButtons()
    }

    private fun checkPermissionAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)
        } else {
            loadContacts()
        }
    }

    private fun loadContacts() {
        lifecycleScope.launch {
            val contacts = getContacts()
            contactRepository.insertContacts(contacts)
            contactAdapter.submitList(contactRepository.getAllContacts())
        }
    }

    @SuppressLint("Range")
    fun getContacts(): List<ContactEntity> {
        val contactList = mutableListOf<ContactEntity>()
        val contentResolver = contentResolver

        val cursor = contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    // Process each contact
                    val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    // Fetch phone numbers
                    val phoneCursor = contentResolver?.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )

                    phoneCursor?.use { pCursor ->
                        if (pCursor.moveToFirst()) {
                            val phoneNumber = pCursor.getString(
                                pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )

                            contactList.add(ContactEntity(id, name, phoneNumber, null))
                        }
                    }
                } while (it.moveToNext())
            }
        }
        return contactList
    }

    private fun setupButtons() {
        findViewById<FloatingActionButton>(R.id.btnAddContact).setOnClickListener {
            // Add contact logic
            addContact()
        }
    }

    private fun addContact() {
        val newContact = ContactEntity(name = "New Contact", phoneNumber = "1234567890", id = "", email = "")
        lifecycleScope.launch {
            contactRepository.insert(newContact)
            Toast.makeText(this@MainActivity, "Contact Added", Toast.LENGTH_SHORT).show()
        }
    }

}
