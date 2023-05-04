import * as React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import { Button,  TextField , FormControl, MenuItem} from '@mui/material';
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


export default function EditActModal(props) {
    const [actDescription, setActDescription] = React.useState('');
    const [active, setActive] = React.useState(0);

  React.useEffect(() => {
    setActDescription(props.selectedRow.description || "");
    setActive(props.selectedRow.is_active || "");
    
  }, [props.selectedRow]);

  const handleCloseModal = () => {
    props.setSelectedRow(null);
    props.onClose()
    setActDescription("");
  }
  const handleConfirmModal = async () => {
    try {
      const data = [{
        primary: "act_name",
        act_name: props.selectedRow.act_name,
        description: actDescription || null,
        is_active: active
      }];      
      await fetchData("/api/act", 'PUT', data);
      handleCloseModal()
    } catch (error) {
      alert(`Error: ${error.message}`);
    }
  }

  return (
    <Modal open={Boolean(props.selectedRow)} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h6" component="h2" >
          Edit Act
        </Typography>

        <TextField
          fullWidth
          multiline
          rows={2}
          label="Description"
          id="Description"
          sx={{mt: 3}}
          value={actDescription}
          onChange={(event) => setActDescription(event.target.value)}
        />
        <FormControl fullWidth>
            <TextField
                select
                label="Is this Act active?"
                id="active"
                fullWidth
                sx={{mt: 3}}
                value={active}
                onChange={(event) => setActive(event.target.value)}
            >
                <MenuItem value={1}>Yes</MenuItem>
                <MenuItem value={0}>No</MenuItem>
            </TextField>
        </FormControl>
          

        <Button variant="contained" sx={{ m: 2 }} color="error" onClick={handleCloseModal}>Cancel</Button>

        <Button variant="contained" sx={{ m: 2 }} color="success" onClick={handleConfirmModal} >Confirm</Button>
        </Box>
      
    </Modal>
  );
}