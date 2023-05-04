import React, { useRef, useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import { Button } from '@mui/material';
import QRCode from 'qrcode-generator';


const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 700,
  bgcolor: 'background.paper',
  border: '2px solid #000',
  boxShadow: 24,
  p: 4,
};


export default function PrintModal( props ) {

  const handleCloseModal = () => {
    props.setSelectedRow(null);
  }
  const handleConfirmModal = async () => {
    handleDownloadClick();
  }

  useEffect(() => {
    // Call handleGenerate when props.selectedRow changes (modal opens)
    handleGenerate();
  }, [props.selectedRow]);
  const canvasRef = useRef(null);
  const [inputData,setInputData] = useState('');

 

  // Handle download button click
  const handleDownloadClick = () => {
    const data = inputData;
    const downloadLink = document.createElement('a');
    downloadLink.setAttribute('download','Conatiner '+ data+'.png');
    downloadLink.setAttribute('href', canvasRef.current.toDataURL('image/png').replace('image/png', 'image/octet-stream'));
    downloadLink.click();
  };

 
  // Handle download button click
  const handleGenerate = () => {
    const data = `${props.selectedRow.container_sr_number}`;
    const id =`${props.selectedRow.liter_capacity}-${props.selectedRow.id}`
    setInputData(data);
    return generateQRCode(data,id);
  };


  const generateQRCode = (data,id) => {
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
      ctx.fillText(id, qrX + qrSize / 4, qrY + qrSize + 100);
    };
    img.src = imageDataUrl;
  }

  return (
    <Modal open={Boolean(props.selectedRow)} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h6" component="h2" >
          Print QR code
        </Typography>

        <canvas ref={canvasRef} width={512} height={700} />

        <Button variant="contained" sx={{ m: 2 }} color="error" onClick={handleCloseModal}>Cancel</Button>

        <Button variant="contained" sx={{ m: 2 }} color="success" onClick={handleConfirmModal}>Confirm and Download</Button>
        </Box>
      
    </Modal>
  );
}