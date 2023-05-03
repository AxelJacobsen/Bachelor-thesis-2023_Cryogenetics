package cryogenetics.logistics.cameraQR

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import cryogenetics.logistics.ui.tankfill.TankFillFragment





class CamAccess : DialogFragment() {
    companion object {
        private val cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val CAMERA_REQUEST_CODE = 100

        /**
         * Requests camera permission from the user.
         * @param activity - The activity where the dialog will show.
         */
        fun requestCameraPermission(activity: Activity) {
             ActivityCompat.requestPermissions(activity, cameraPermissions,
                CAMERA_REQUEST_CODE
            )
        }

        /**
         * Checks the app's camera permissions.
         * @param context - The context of the app.
         */
        fun checkCameraPermission(context: Context): Boolean {
            return (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        }
    }
}