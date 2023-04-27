import {useState } from 'react';
import { Box, Button, Modal, TextField, Typography } from '@mui/material';
import fetchData from '../../globals/fetchData'
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

export default function AddActModal({ open, setOpen, onClose }) {
  const [actName, setActName] = useState('');
  const [actDescription, setActDescription] = useState('');

  const handleCloseModal = () => {
    onClose();
    setOpen(false);
    setActName('');
    setActDescription('');
  }

  const handleConfirmModal = async () => {
    try {
      const data = [{
        act_name: actName,
        description: actDescription,
        is_active: 1
      }];      
      await fetchData("/api/act", 'POST', data);
      handleCloseModal()
    } catch (error) {
      alert(`Error: ${error.message}`);
    }
  }

  return (
    <Modal open={open} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h6" component="h2">
          Add Act
        </Typography>
        <TextField
          id='act_name'
          label="Act Name"
          variant="outlined"
          value={actName}
          onChange={(event) => setActName(event.target.value)}
        />
        <TextField
          id="act_description"
          label="Act Description"
          multiline
          rows={8}
          variant="outlined"
          value={actDescription}
          onChange={(event) => setActDescription(event.target.value)}
        />
        <Button variant="contained" sx={{ m: 2 }} color="error" onClick={handleCloseModal}>
          Cancel
        </Button>
        <Button variant="contained" sx={{ m: 2 }} color="success" onClick={handleConfirmModal} disabled={!actName || !actDescription}>
          Confirm
        </Button>
      </Box>
    </Modal>
  );
}
