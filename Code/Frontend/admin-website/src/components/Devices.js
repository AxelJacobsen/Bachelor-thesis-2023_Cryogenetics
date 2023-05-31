import React from 'react';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Checkbox from '@mui/material/Checkbox';
import { Button } from '@mui/material';
import fetchData from '../globals/fetchData';

export default function Devices() {
  const [rows, setRows] = React.useState([]);
  const [checked, setChecked] = React.useState([]);
  const [checkedKeys, setCheckedKeys] = React.useState([]);

  const handleToggle = (value, keyvalue) => () => {
      const currentIndex = checked.indexOf(value);
      const newChecked = [...checked];
      const newCheckedKeys = [...checkedKeys];
    
      if (currentIndex === -1) {
        newChecked.push(value);
        newCheckedKeys.push(keyvalue);
      } else {
        newChecked.splice(currentIndex, 1);
        newCheckedKeys.splice(newCheckedKeys.indexOf(keyvalue), 1);
      }
    
      setChecked(newChecked);
      setCheckedKeys(newCheckedKeys);
    };

    React.useEffect(() => {
    console.log(checkedKeys);
    }, [checkedKeys]);

  React.useEffect(() => {
    async function fetchRowData() {
      try {
        const response = await fetchData('https://cryogenetics-logistics-solution.azurewebsites.net/api/user/admin/verification', 'GET');
        if (response == null){
          console.log('No data available.'); // Log error message instead of returning JSX
        }
        else {
          setRows(response); // Update "rows" state with fetched data
        }
      } catch (error) {
        console.error(error);
      }
    }
    fetchRowData();
  }, []);

  const handleConfirm = async () => {
    try {
    const response = await fetchData('https://cryogenetics-logistics-solution.azurewebsites.net/api/user/admin/verification', 'POST', checkedKeys.map(key => ({ keyvalue: key })));
      console.log(response);
      // Call another GET after successful POST
      const newResponse = await fetchData('https://cryogenetics-logistics-solution.azurewebsites.net/api/user/admin/verification', 'GET');
      setRows(newResponse);
      setChecked([])
      setCheckedKeys([])
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div>
      <List sx={{ width: '100%', maxWidth: 360, bgcolor: 'background.paper' }}>
        {rows.map((row, value) => {
          const labelId = `checkbox-list-label-${value}`;

          return (
            <ListItem key={value} disablePadding>
              <ListItemButton role={undefined} onClick={handleToggle(value, row.keyvalue)} dense>
                <ListItemIcon>
                  <Checkbox
                    edge="start"
                    checked={checked.indexOf(value) !== -1}
                    tabIndex={-1}
                    disableRipple
                    inputProps={{ 'aria-labelledby': labelId }}
                  />
                </ListItemIcon>
                <ListItemText id={labelId} primary={row.keyvalue} />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
      <Button variant="contained" onClick={handleConfirm}>
        Confirm
      </Button>
    </div>
  );
};
