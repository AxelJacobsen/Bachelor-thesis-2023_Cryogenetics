package cryogenetics.logistics.ui.tankfill

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import cryogenetics.logistics.R
import cryogenetics.logistics.databinding.FragmentTankFillingBinding
import cryogenetics.logistics.ui.inventory.mini.MiniInventoryFragment
import cryogenetics.logistics.ui.tank.CameraFragment


class TankFillFragment : Fragment() {


    companion object {
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101

        private const val TAG = "MAIN_TAG"
    }

    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>
    private var imageUri: Uri? = null
    private var barcodeScannerOptions: BarcodeScannerOptions? = null
    private var barcodeScanner: BarcodeScanner? = null

    private var _binding : FragmentTankFillingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TankViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentTankFillingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        barcodeScannerOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions!!)


        binding.ibCamera?.setOnClickListener {
            if (checkCameraPermission()){
                //if (checkStoragePermission()){
                binding.fragmentContainer?.visibility = View.VISIBLE
                childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CameraFragment())
                    .commit()
                println("checkSTORagePermission success")
                /*} else {
                    println("checkSTORagePermission fail")
                    requestStoragePermission()
                }*/
            } else {
                println("checkCameraPermission fail")
                requestCameraPermission()
                //requestStoragePermission()
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.flMiniInventoryRecycler, MiniInventoryFragment())
            .commit()

        childFragmentManager.beginTransaction() // TODO: Change Fragment to refilled tanks
            .replace(R.id.flRefilledTanksRecycler, MiniInventoryFragment())
            .commit()
    }


    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val data = result.data

            imageUri = data?.data
            Log.d(TAG, ": galleryActivityResultLauncher imageUri: $imageUri")

            binding.ibCamera?.setImageURI(imageUri) // TODO: LINK TO IMAGEVIEW

        } else {
            Toast.makeText(requireContext(), "galleryActivityResultLauncher failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun pickImageCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample image title")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample image description")

        imageUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        requireActivity().startActivityFromFragment(TankFillFragment(), intent, CAMERA_REQUEST_CODE)
        startActivity(intent)
        //requireActivity().startActivityForResult(intent, CAMERA_REQUEST_CODE)
        cameraActivityResultLauncher.launch(intent)
    }


    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data

            Log.d(TAG, ": cameraActivityResultLauncher - imageUri: $imageUri")
            binding.ibCamera?.setImageURI(imageUri) // TODO: LINK IMAGEVIEW
        }
    }

    private fun checkStoragePermission(): Boolean {
        val storg = (ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        return storg
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), storagePermissions, STORAGE_REQUEST_CODE)
    }

    private fun checkCameraPermission(): Boolean {
        val camRes = (ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)

        return camRes
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), cameraPermissions, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && storageAccepted)
                        pickImageCamera()
                    else
                        Toast.makeText(requireContext(), "Camera and storage permissions are required", Toast.LENGTH_LONG).show()
                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted)
                        pickImageGallery()
                     else
                        Toast.makeText(requireContext(), "Camera and storage permissions are required", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TankViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
