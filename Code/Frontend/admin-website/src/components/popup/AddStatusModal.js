import * as React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import { Button, TextField } from '@mui/material';
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


export default function AddStatusModal({ open, setOpen, onClose }) {

  const [name, setName] = React.useState("")


  const handleCloseModal = () => {
    setName("")
    setOpen(false);
    onClose()
  }
  const handleSubmit = async () => {
    try {
      const data = [{
        container_status_name: name
      }]; 
      console.log(data);
      await fetchData("/api/container_status", 'POST', data);
      handleCloseModal();
    } catch (error) {
      alert(`Error: ${error.message}`);
    }
  };


  return (
    <Modal open={open} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h6" component="h2" >
          Add Status
        </Typography>

        <TextField
          fullWidth
          label="Status name"
          id="status-name"
          sx={{mt: 3}}
          value={name}
          onChange={(event) => setName(event.target.value)}
        />
        

      <Button variant="contained" sx={{ m: 2 }} color="error" onClick={handleCloseModal}>Cancel</Button>

      <Button variant="contained" sx={{ m: 2 }} color="success" onClick={handleSubmit} disabled={!name}>Confirm</Button>
      </Box>
      
    </Modal>
  );
}