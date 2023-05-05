package cryogenetics.logistics.cameraQR

import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import cryogenetics.logistics.R
import cryogenetics.logistics.databinding.FragmentCameraBinding
import cryogenetics.logistics.ui.tank.OnItemClickListener

/**
 *  Camera preview with QR recognition.
 */
class CameraFragment(
    private val mOnProductClickListener: OnItemClickListener,
) : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var previewView: PreviewView // ViewBinder to preview camera in
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var mCameraProvider: ProcessCameraProvider? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get components
        previewView = view.findViewById(R.id.preview_view)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext())

        binding.ibCancelCam.setOnClickListener {
            mOnProductClickListener.onStopCam()
            onPaus()
        }
        // Start camera
        startCamera()
    }

    /**
     *  Start the camera; Requesting access and creating listeners.
     */
    private fun startCamera() {
        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                mCameraProvider = cameraProvider
                bindCameraPreview()
            } catch (ex: Exception) {
                Log.e("CameraProviderFuture", "Couldn't bind camera preview")
                error("Error binding camera preview")
            }
        }, ContextCompat.getMainExecutor(this.requireContext()))
    }

    /**
     *  Binds the camera preview and sets up QR scanner.
     */
    private fun bindCameraPreview() {
        previewView.implementationMode =
            PreviewView.ImplementationMode.PERFORMANCE // COMPATIBLE => TextureView, PERFORMANCE => SurfaceView

        // Bind preview
        val preview: Preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        // Select camera (front/back)
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK) // LENS_FACING_BACK => back camera, LENS_FACING_FRONT => front camera
            .build()

        // Set up image analyzer
        val imageAnalasys: ImageAnalysis =
            ImageAnalysis.Builder().setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

        imageAnalasys.setAnalyzer(ContextCompat.getMainExecutor(this.requireContext()), QRScanner({
            mOnProductClickListener.onFoundQR(it)
        }, {
            // QR Not found
        }))

        // Bind analyzer and preview to selected camera
        val camera: androidx.camera.core.Camera = mCameraProvider!!.bindToLifecycle(
            this as LifecycleOwner, cameraSelector, imageAnalasys, preview
        )
    }

    fun onPaus() {
        super.onPause()
        savedStateRegistry
        mCameraProvider!!.unbindAll() // Stops camera
    }

    fun onRes(): Boolean {
        super.onResume()
        return if (mCameraProvider != null) {
            bindCameraPreview()
            true
        } else false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mCameraProvider!!.unbindAll() // Stops camera
    }
}