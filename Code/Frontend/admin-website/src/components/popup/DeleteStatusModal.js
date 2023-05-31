import { Box, Button, Modal, Typography } from '@mui/material';
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

export default function DeleteStatusModal(props) {

  const handleCloseModal = () => {
    props.onClose();
    props.setSelectedRow(null);
  }
  

  const handleConfirmModal = async () => {
    try {
      const url = "https://cryogenetics-logistics-solution.azurewebsites.net/api/container_status?container_status_name="+props.selectedRow.container_status_name
      const data = [{
        container_status_name: props.selectedRow.container_status_name
      }];      
      await fetchData(url, 'DELETE', data);
      handleCloseModal()
    } catch (error) {
      alert(`Error: ${error.message}`);
    }
  }

  return (
    <Modal open={Boolean(props.selectedRow)} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h6" component="h2">
          DELETE MODEL?
        </Typography>

        <Typography id="modal-modal-title" variant="h6" component="h2">
          ONLY DO THIS IF THE MODEL IS UNUSED!
        </Typography>
        
        <Button variant="contained" sx={{ m: 2 }} color="error" onClick={handleCloseModal}>
          Cancel
        </Button>
        <Button variant="contained" sx={{ m: 2 }} color="success" onClick={handleConfirmModal}>
          Confirm
        </Button>
      </Box>
    </Modal>
  );
}
