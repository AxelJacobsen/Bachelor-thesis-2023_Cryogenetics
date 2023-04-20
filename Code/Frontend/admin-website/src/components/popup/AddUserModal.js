import * as React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import { Button, TextField,MenuItem } from '@mui/material';
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


export default function AddUserModal({ open, setOpen, onClose }) {
  const [rows, setRows] = React.useState([]);
  const [location, setLocation] = React.useState("");
  const [name, setName] = React.useState("");
  const [alias, setAlias] = React.useState("");
  const [code, setCode] = React.useState(0);
  const [duplicateCode, setDuplicateCode] = React.useState(false);

  async function fetchRowData() {
    try {
      const response = await fetchData('/api/create/employee', 'GET');
      setRows(response);
    } catch (error) {
      console.error(error);
    }
  }
  const locationOptions = rows && rows.location ? rows.location.map(location => ({ value: location.location_id, label: location.location_name })) : [];

  //Call the API when Open is true
  React.useEffect(() => { 
    if (open){
      fetchRowData();
    }
  }, [open]); 
  


  const handleCloseModal = () => {
    setOpen(false);
    onClose();
    setCode(0);
    setAlias("");
    setName("");
    setLocation("")
  }
  const handleConfirmModal = async () => {
    try {
      const data = [{
        employee_id:Math.max(...rows.employee.map(employee => employee.employee_id))+1,
        employee_name: name,
        employee_alias: alias,
        login_code: code,
        location_id: location
      }];      
      await fetchData("/api/employee", 'POST', data);
      handleCloseModal()
    } catch (error) {
      alert(`Error: ${error.message}`);
    }
  }
  const handleCodeChange = (event) => {
    const enteredCode = event.target.value;
    const key = event.key;
    // Allow only numeric characters (0-9)
    if ((key !== "Backspace" && key !== "Delete" && isNaN(key)) || key === " ") {
      event.preventDefault();
    }

    // Check if entered code exists in employeeData
    const codeExists = rows.employee.some(
      (employee) => employee.login_code === Number(enteredCode)
    );
    if (codeExists) {
      setDuplicateCode(true);
    } else {
      setDuplicateCode(false);
      setCode(enteredCode);
    }
  };

  return (
    <Modal open={open} onClose={handleCloseModal}  aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h6" component="h2" >
          Add User
        </Typography>

        <TextField
          id='user-name'
          label="User's Name"
          variant="outlined"
          value={name}
          onChange={(event) => setName(event.target.value)}
        />
        <TextField
          id='user-alias'
          label="User's alias"
          variant="outlined"
          value={alias}
          onChange={(event) => setAlias(event.target.value)}
        />

        <TextField
          select
          required
          label="Location"
          id="location"
          fullWidth
          sx={{mt: 3}}
          value={location}
          onChange={(event) => setLocation(event.target.value)}
        >
        <MenuItem value={""} disabled>Select a location</MenuItem>
        {locationOptions.map(option => (
          <MenuItem key={option.value} value={option.value}>{option.label}</MenuItem>
        ))}
        </TextField>

        <TextField
          required
          type="number"
          label="Login Code"
          id="login-code"
          sx={{ mt: 3 }}
          helperText={duplicateCode ? "Duplicate code detected!" : "Unique!"}
          value={code}
          inputProps={{
            maxLength: 3,
            onKeyDown: handleCodeChange,
          }}
          onChange={handleCodeChange}
        />
          
        <Button variant="contained" sx={{ m: 2 }} color="error" onClick={handleCloseModal}>Cancel</Button>

        <Button variant="contained" sx={{ m: 2 }} color="success" onClick={handleConfirmModal} disabled={!name || !alias || !code || !location || duplicateCode}>Confirm</Button>
        </Box>
      
    </Modal>
  );
}