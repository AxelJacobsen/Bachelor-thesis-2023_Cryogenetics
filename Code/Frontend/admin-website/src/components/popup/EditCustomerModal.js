import * as React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import { Button,  TextField } from '@mui/material';
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


export default function EditCustomerModal(props) {
  const [name, setName] = React.useState("");

  React.useEffect(() => {
    setName(props.selectedRow.client_name || "");
    
  }, [props.selectedRow]);

  const handleCloseModal = () => {
    props.setSelectedRow(null);
    props.onClose()
    setName("");
  }
  const handleConfirmModal = async () => {
    try {
      const data = [{
        primary: "client_id",
        client_id: props.selectedRow.client_id,
        client_name: name,
      }];      
      await fetchData("https://cryogenetics-logistics-solution.azurewebsites.net/api/client", 'PUT', data);
      handleCloseModal()
    } catch (error) {
      alert(`Error: ${error.message}`);
    }
  }

  return (
    <Modal open={Boolean(props.selectedRow)} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h6" component="h2" >
          Edit Customers
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