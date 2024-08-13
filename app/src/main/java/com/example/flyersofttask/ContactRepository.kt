package com.example.flyersofttask

class ContactRepository(private val contactDao: ContactDao) {

    suspend fun insertContacts(contacts: List<ContactEntity>) {
        contactDao.insertContacts(contacts)
    }

    suspend fun getAllContacts(): List<ContactEntity> {
        return contactDao.getAllContacts()
    }


    suspend fun insert(contact: ContactEntity) {
        contactDao.insert(contact)
    }

    suspend fun update(contact: ContactEntity) {
        contactDao.update(contact)
    }

    suspend fun delete(contact: ContactEntity) {
        contactDao.delete(contact)
    }

    suspend fun getContactById(contactId: String): ContactEntity? {
        return contactDao.getContactById(contactId)
    }
}
