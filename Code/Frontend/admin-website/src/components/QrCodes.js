import React, { useRef } from 'react';
import QRCode from 'qrcode-generator';

export default function QrCodes() {
  const canvasRef = useRef(null);

  // Generate QR code when component mounts
  React.useEffect(() => {
    const data = "047-0001";
    const qr = QRCode(0, 'H');
    qr.addData(data);
    qr.make();

    // Create the QR code image data URL using the createDataURL method,
    // and then create a new Image object and assign the onload property to handle the image load event.
    // When the image is loaded, we can draw it on the canvas and add the QR content under the code using the fillText method.
    const imageDataUrl = qr.createDataURL(8, 0);
    const img = new Image();
    img.onload = function() {
      const ctx = canvasRef.current.getContext('2d');
      ctx.fillStyle = 'white'; // Set background color to white
      ctx.fillRect(0, 0, canvasRef.current.width, canvasRef.current.height);
      const padding = 50; // Add some padding around the QR code
      const qrSize = canvasRef.current.width - 2 * padding;
      const qrX = padding + (qrSize - this.width) / 2;
      const qrY = (qrSize - this.height) / 2;

      
      // Draw the QR code image on the canvas
      ctx.drawImage(this, qrX, qrY, this.width, this.height);
      
      // Add the QR content under the code using the fillText method
      ctx.fillStyle = 'black';
      ctx.font = '100px Inter';
      ctx.textAlign = 'center'; // Center text horizontally
      ctx.fillText(data, qrX + qrSize / 4, qrY + qrSize);
      const textY = qrY + qrSize + 20;
      if (textY < canvasRef.current.height) { console.log("outside") }
    };
    img.src = imageDataUrl;
  }, []);

  // Handle download button click
  const handleDownloadClick = () => {
    const data = "test";
    const downloadLink = document.createElement('a');
    downloadLink.setAttribute('download', data+'.png');
    downloadLink.setAttribute('href', canvasRef.current.toDataURL('image/png').replace('image/png', 'image/octet-stream'));
    downloadLink.click();
  };

  return (
    <div>
      <canvas ref={canvasRef} width={512} height={700} />
      <button onClick={handleDownloadClick}>Download QR code</button>
    </div>
  );
}