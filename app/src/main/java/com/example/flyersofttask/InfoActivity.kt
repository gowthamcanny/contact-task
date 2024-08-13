package com.example.flyersofttask

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.flyersofttask.data.AddressDatabase
import com.example.flyersofttask.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val viewModel =
            AddressListViewModel(AddressDatabase.getDatabase(applicationContext).addressDao())

        viewModel.size().observe(this) {
            binding.sizeInfo.text = getString(R.string.database_size, it)
        }

        binding.fullscreenSwitch.isChecked = !notUseFullScreenDialog
        binding.fullscreenSwitch.setOnCheckedChangeListener { _, b ->
            notUseFullScreenDialog = !b
        }

        binding.deleteAll.setOnClickListener {
            viewModel.deleteAll()
        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}