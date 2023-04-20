import * as React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';
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

const tempData = [
  {
    value: 'USD',
    label: '$',
  },
  {
    value: 'EUR',
    label: '€',
  },
  {
    value: 'BTC',
    label: '฿',
  },
  {
    value: 'JPY',
    label: '¥',
  },
];

export default function EditContainerModal(props) {

  function handleCloseModal() {
    props.setSelectedRow(null);
  }

  return (
    <Modal open={Boolean(props.selectedRow)} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      
      <Box sx={style}>
          <Typography id="modal-modal-title" variant="h6" component="h2" >
            Edit Container
          </Typography>
          <TextField id="nrText" label={props.selectedRow.nr} variant="outlined" sx={{ m: 2 }}/>

          <TextField
          id="selectModel"
          select
          label="Select"
          defaultValue={props.selectedRow.model}
          helperText="Please select your model"
          sx={{ m: 2 }}
        >
          {tempData.map((option) => (
            <MenuItem key={option.value} value={option.value}>
              {option.label}
            </MenuItem>
          ))}
        </TextField>

        <TextField
          id="selectLocation"
          select
          label="Select"
          defaultValue={props.selectedRow.location}
          helperText="Please select your location"
          sx={{ m: 2 }}
        >
          {tempData.map((option) => (
            <MenuItem key={option.value} value={option.value}>
              {option.label}
            </MenuItem>
          ))}
        </TextField>

        <TextField
          id="selectCustomer"
          select
          label="Select"
          defaultValue={props.selectedRow.customer}
          helperText="Please select your customer"
          sx={{ m: 2 }}
        >
          {tempData.map((option) => (
            <MenuItem key={option.value} value={option.value}>
              {option.label}
            </MenuItem>
          ))}
        </TextField>

        {/**TODO: FIX FORMAT */}
        <TextField
        id="selectLastFilled"
        label="Last Filled"
        type="date"
        defaultValue={props.selectedRow.last_filled} 
        sx={{ width: 220, m: 2 }}
        InputLabelProps={{
          shrink: true,
        }}
      />

      {/**TODO: FIX FORMAT */}
      <TextField
        id="selectInvoice"
        label="Invoice"
        type="date"
        defaultValue={props.selectedRow.Invoice} 
        sx={{ width: 220, m: 2 }}
        InputLabelProps={{
          shrink: true,
        }}
      />

          <TextField id="address" label={props.selectedRow.address} variant="outlined" sx={{ m: 2 }}/>

          <TextField
          id="selectStatus"
          select
          label="Select"
          defaultValue={props.selectedRow.status}
          helperText="Please select your status"
          sx={{ m: 2 }}
        >
          {tempData.map((option) => (
            <MenuItem key={option.value} value={option.value}>
              {option.label}
            </MenuItem>
          ))}
        </TextField>

        <Button variant="contained" sx={{ m: 2 }} color="error" onClick={handleCloseModal}>Cancel</Button>

        <Button variant="contained" sx={{ m: 2 }} color="success" onClick={handleCloseModal}>Confirm</Button>
        </Box>
      
    </Modal>
  );
}