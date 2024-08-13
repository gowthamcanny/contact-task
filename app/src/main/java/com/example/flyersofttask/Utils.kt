package com.example.flyersofttask

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import kotlin.collections.HashMap

class Utils {
    companion object {

        private tailrec fun Context.getActivity(): Activity? = this as? Activity
            ?: (this as? ContextWrapper)?.baseContext?.getActivity()

        private val showedPermissionInfo: HashMap<String, Boolean> = HashMap()

        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher. You can use either a val, as shown in this snippet,
        // or a lateinit var in your onAttach() or onCreate() method.
        lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

        fun needPermission(
            context: Context,
            permission: String,
            infoResId: Int? = null
        ) {
            val activity = context.getActivity() ?: return

            val readCallLogPermissionResult =
                context.checkSelfPermission(permission)
            if (readCallLogPermissionResult == PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
            } else if (infoResId != null && shouldShowRequestPermissionRationale(
                    activity,
                    permission
                )
            ) {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                if (showedPermissionInfo[permission] != true) {
                    Toast.makeText(
                        context,
                        infoResId,
                        Toast.LENGTH_LONG
                    ).show()
                    showedPermissionInfo[permission] = true
                }
            } else {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}