import * as React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import { Button, TextField, MenuItem } from '@mui/material';
import fetchData from '../../globals/fetchData';

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


export default function AddCustomerModal({ open, setOpen, onClose }) {
  const [rows, setRows] = React.useState([]);
  const [name, setName] = React.useState("");

  async function fetchRowData() {
    try {
      const response = await fetchData('/api/client', 'GET');
      setRows(response);
    } catch (error) {
      console.error(error);
    }
  }
  const highestClientId = rows.reduce((acc, curr) => {
    return acc > curr.client_id ? acc : curr.client_id;
  }, 0);

  //Call the API when Open is true
  React.useEffect(() => { 
    if (open){
      fetchRowData();
    }
  }, [open]); 

  const handleCloseModal = () => {
    setOpen(false);
    onClose();
    setName("");
  }
  const handleConfirmModal = async () => {
    try {
      const data = [{
        client_id: highestClientId+1,
        client_name: name,
      }];      
      await fetchData("/api/client", 'POST', data);
      handleCloseModal()
    } catch (error) {
      alert(`Error: ${error.message}`);
    }
  }

  return (
    <Modal open={open} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h6" component="h2" >
          Add Customers
        </Typography>

        <TextField
          fullWidth
          label="Client name"
          id="client-name"
          sx={{mt: 3}}
          value={name}
          onChange={(event) => setName(event.target.value)}
        />
          

        <Button variant="contained" sx={{ m: 2 }} color="error" onClick={handleCloseModal}>Cancel</Button>

        <Button variant="contained" sx={{ m: 2 }} color="success" onClick={handleConfirmModal} disabled={!name}>Confirm</Button>
        </Box>
      
    </Modal>
  );
}