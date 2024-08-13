package com.example.flyersofttask

import android.database.ContentObserver
import android.os.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactObserver(private val repository: ContactRepository) : ContentObserver(Handler()) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        CoroutineScope(Dispatchers.IO).launch {
            // Code to synchronize with Room Database
        }
    }
}
