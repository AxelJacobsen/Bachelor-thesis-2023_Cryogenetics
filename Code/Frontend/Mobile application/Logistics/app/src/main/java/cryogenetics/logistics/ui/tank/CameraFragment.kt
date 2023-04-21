import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import cryogenetics.logistics.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CameraFragment : Fragment() {

    private var cameraDevice: CameraDevice? = null
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private lateinit var previewRequest: CaptureRequest
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var imageReader: ImageReader
    private lateinit var viewFinder: TextureView
    private lateinit var captureButton: Button
    private var barcodeScannerOptions: BarcodeScannerOptions? = null
    private var barcodeScanner: BarcodeScanner? = null

    private val PERMISSIONS_REQUEST_CAMERA = 0
    private var previewSize: Size? = null

    private val cameraDeviceStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFinder = view.findViewById<TextureView>(R.id.view_finder)
        captureButton = view.findViewById(R.id.capture_button)

        barcodeScannerOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions!!)

        startCamera()
    }


    private fun startCamera() {
        val manager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // Find the first back-facing camera
            val cameraId = manager.cameraIdList.firstOrNull {
                val characteristics = manager.getCameraCharacteristics(it)
                characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            } ?: throw IllegalStateException("Could not find any back-facing camera")

            // Get the stream configuration map
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!

            // Select a preview size and configure the image reader
            previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture::class.java).maxByOrNull { it.width * it.height }!!
            imageReader = ImageReader.newInstance(previewSize!!.width, previewSize!!.height, ImageFormat.JPEG, 1).apply {
                setOnImageAvailableListener({ reader ->
                    val image = reader.acquireNextImage()
                    // Process the image here

                    //val ttt = image.planes
                    // https://stackoverflow.com/questions/62363100/detach-image-produced-from-imagereader

                    //println(image)
                    scanQR(image, image.width, image.height)
                    image.close()
                }, null)
            }

            // Open the camera and create the capture session
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST_CAMERA)
                return
            }
            manager.openCamera(cameraId, cameraDeviceStateCallback, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting camera: ${e.message}", e)
        }
    }

    private fun scanQR(image: Image, width: Int, height: Int) {
        Log.d(TAG, "scanQR scanQR")
        val detet = ImagePreprocessor()
        val bitmap : Bitmap? = detet.preprocessImage(image, width, height)

        val context: Context = requireContext()// your context

        val outputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        val imagePath = "my_image.jpg" // name of the file you want to save
        val file = File(context.cacheDir, imagePath)

        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(outputStream.toByteArray())
        fileOutputStream.flush()
        fileOutputStream.close()

        val uri = Uri.fromFile(file)

        try {
            val inputImage = InputImage.fromFilePath(requireContext(), uri!!)
            val barcodeResult = barcodeScanner!!.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    extraxctBarcodeQrCodeInfo(barcodes)
                }
                .addOnFailureListener{ e ->
                    Log.d(TAG, "scanQR err", e)
                    Toast.makeText(requireContext(), "Scan failed, ${e.message}", Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {

        }
    }

    private fun extraxctBarcodeQrCodeInfo(barcodes: List<Barcode>) {
        println("extraxctBarcodeQrCodeInfo")
        println(barcodes)
        for (barcode in barcodes) {
            val bound = barcode.boundingBox
            val corner = barcode.cornerPoints

            val rawValue = barcode.rawValue
            Log.d(TAG, "extraxctBarcodeQrCodeInfo raw: $rawValue")


            val valueType = barcode.valueType
            when (valueType) {
                Barcode.TYPE_WIFI -> {
                    val typeWifi = barcode.wifi

                }
            }

        }
    }

    private fun createCameraPreviewSession() {
        try {
            val surfaceTexture = viewFinder.surfaceTexture
            surfaceTexture?.setDefaultBufferSize(previewSize!!.width, previewSize!!.height)
            val previewSurface = Surface(surfaceTexture)
            val imageReaderSurface = imageReader.surface
            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(previewSurface)
            previewRequestBuilder.addTarget(imageReaderSurface)
            cameraDevice!!.createCaptureSession(
                listOf(previewSurface, imageReaderSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        cameraCaptureSession = session
                        previewRequestBuilder.set(
                            CaptureRequest.CONTROL_MODE,
                            CameraMetadata.CONTROL_MODE_AUTO
                        )
                        previewRequest = previewRequestBuilder.build()
                        cameraCaptureSession.setRepeatingRequest(previewRequest, null, null)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {

                    }
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraDevice?.close()
        cameraDevice = null
    }
}