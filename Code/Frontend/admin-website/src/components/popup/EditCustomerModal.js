import * as React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import TextField from '@mui/material/TextField';
import { Button } from '@mui/material';

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


export default function EditCustomerModal(props) {


  function handleCloseModal() {
    props.setSelectedRow(null);
  }

  return (
    <Modal open={Boolean(props.selectedRow)} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      
      <Box sx={style}>
          <Typography id="modal-modal-title" variant="h6" component="h2" >
            Edit Customers
          </Typography>
          <TextField id="nrText" label={props.selectedRow.nr} variant="outlined" sx={{ m: 2 }}/>

          

        <Button variant="contained" sx={{ m: 2 }} color="error" onClick={handleCloseModal}>Cancel</Button>

        <Button variant="contained" sx={{ m: 2 }} color="success" onClick={handleCloseModal}>Confirm</Button>
        </Box>
      
    </Modal>
  );
}