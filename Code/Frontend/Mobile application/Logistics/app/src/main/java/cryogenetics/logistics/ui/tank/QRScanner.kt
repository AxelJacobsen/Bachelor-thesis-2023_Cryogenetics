package cryogenetics.logistics.ui.tank

import android.graphics.ImageFormat.*
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

/**
 *  Scans for QR codes in images.
 */
class QRScanner(
    var QRFoundListener: (String) -> Unit,  // When a QR code is found...
    var QRNotFoundListener: () -> Unit      // When a QR code is found...
) : ImageAnalysis.Analyzer {

    /**
     *  Analyzes an image/frame.
     */
    override fun analyze(image: ImageProxy) {
        if (image.format == YUV_420_888 || image.format == YUV_422_888 || image.format == YUV_444_888) {
            val byteBuffer: ByteBuffer = image.planes[0].buffer
            val imageData = ByteArray(byteBuffer.capacity())
            byteBuffer.get(imageData)

            val source = PlanarYUVLuminanceSource(
                imageData,
                image.width, image.height,
                0, 0,
                image.width, image.height,
                false
            )

            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                val result: Result = QRCodeMultiReader().decode(binaryBitmap)
                QRFoundListener(result.text)
            } catch (e: Exception) {
                QRNotFoundListener()
            }
        }

        image.close()
    }
}