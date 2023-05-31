import * as React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import TextField from '@mui/material/TextField';
import { Button } from '@mui/material';
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


export default function EditModelModal(props) {
  const [refill, setRefill] = React.useState(0)
  const [liter, setLiter] = React.useState(0)
  React.useEffect(() => {
    setRefill(props.selectedRow.refill_interval || "");
    setLiter(props.selectedRow.liter_capacity || "")
  }, [props.selectedRow]);


  function handleCloseModal() {
    props.setSelectedRow(null);
    setLiter(0)
    setRefill(0)
    props.onClose()
  }
  const handleConfirmModal = async () => {
    try {
      const data = [{
        primary: "container_model_name",
        container_model_name: props.selectedRow.container_model_name,
        refill_interval: refill,
        liter_capacity: liter
      }];      
      await fetchData("https://cryogenetics-logistics-solution.azurewebsites.net/api/container_model", 'PUT', data);
      handleCloseModal()
    } catch (error) {
      alert(`Error: ${error.message}`);
    }
  }

  return (
    <Modal open={Boolean(props.selectedRow)} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h6" component="h2" >
          Edit Model
        </Typography>
        
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

        <Button variant="contained" sx={{ m: 2 }} color="success" onClick={handleConfirmModal} disabled={!refill || !liter }>Confirm</Button>
        </Box>
      
    </Modal>
  );
}