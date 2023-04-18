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


export default function AddModelModal({ open, setOpen, onClose }) {
  const [refill, setRefill] = React.useState(0)
  const [liter, setLiter] = React.useState(0)
  const [name, setName] = React.useState("")


  const handleCloseModal = () => {
    setName("")
    setLiter(0)
    setRefill(0)
    setOpen(false);
    onClose()
  }
  const handleSubmit = async () => {
    try {
      const data = [{
        container_model_name: name,
        "liter_capacity": liter,
        "refill_interval": refill
      }]; 
      console.log(data);
      await fetchData("/api/container_model", 'POST', data);
      handleCloseModal();
    } catch (error) {
      alert(`Error: ${error.message}`);
    }
  };
  

  return (
    <Modal open={open} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h6" component="h2" >
          Add Model
        </Typography>
        <TextField
          fullWidth
          label="Model name"
          id="model-name"
          sx={{mt: 3}}
          value={name}
          onChange={(event) => setName(event.target.value)}
        />

        <TextField
          fullWidth
          required
          type={"number"}
          label="Liter capacity"
          id="liter-capacity"
          sx={{mt: 3}}
          value={liter}
          inputProps={{
            onKeyDown: (event) => {
              const key = event.key;
              // Allow only numeric characters (0-9)
              if (isNaN(key) || key === " ") {
                event.preventDefault();
              }
            }
          }}
          onChange={(event) => setLiter(event.target.value)}
        />

        <TextField
          fullWidth
          required
          type={"number"}
          label="Refill interval (days)"
          id="refill-interval"
          sx={{mt: 3}}
          value={refill}
          inputProps={{
            onKeyDown: (event) => {
              const key = event.key;
              // Allow only numeric characters (0-9)
              if (isNaN(key) || key === " ") {
                event.preventDefault();
              }
            }
          }}
          onChange={(event) => setRefill(event.target.value)}
        />
          

        <Button variant="contained" sx={{ m: 2 }} color="error" onClick={handleCloseModal}>Cancel</Button>

        <Button variant="contained" sx={{ m: 2 }} color="success" onClick={handleSubmit} disabled={!name || !refill || !liter}>Confirm</Button>
        </Box>
      
    </Modal>
  );
}