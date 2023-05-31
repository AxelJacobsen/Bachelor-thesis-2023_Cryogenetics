import * as React from 'react';
import PropTypes from 'prop-types';
import Box from '@mui/material/Box';
import { TextField } from '@mui/material';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import TableSortLabel from '@mui/material/TableSortLabel';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import { visuallyHidden } from '@mui/utils';
import Button from '@mui/material/Button';
import { useNavigate } from 'react-router-dom';
import './TableLayout.css';
import fetchData from '../globals/fetchData';
import {stableSort , getComparator} from '../globals/globalFunctions';

const headCells = [
  {
    id: 'date', numeric: false, disablePadding: true, label: 'Date',
  },
  {
    id: 'act', numeric: false, disablePadding: true, label: 'Act',
  },
  {
    id: 'employee_alias', numeric: false, disablePadding: true, label: 'Responsible Sign',
  },
  {
    id: 'location_name', numeric: false, disablePadding: true, label: 'Inventory Name',
  },
  {
    id: 'address', numeric: false, disablePadding: true, label: 'Address',
  },
  {
    id: 'client_name', numeric: false, disablePadding: true, label: 'Customer Name',
  },
  {
    id: 'container_status_name', numeric: false, disablePadding: true, label: 'Status',
  },
  {
    id: 'container_sr_number', numeric: false, disablePadding: true, label: 'SerialNR',
  },
  {
    id: 'comment', numeric: false, disablePadding: true, label: 'Comment',
  },
];

// EnhancedTableHead function takes in props and renders the table header with sorting functionality
function EnhancedTableHead(props) {
  const { order, orderBy, onRequestSort } =
    props;
  const createSortHandler = (property) => (event) => {
    onRequestSort(event, property);
  };

  return (
    <TableHead>
      <TableRow>
        {headCells.map((headCell) => (
          <TableCell
            key={headCell.id}
            align='center'
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}
          >
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              onClick={createSortHandler(headCell.id)}
            >
              {headCell.label}
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </Box>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))}
      </TableRow>
    </TableHead>
  );
}

EnhancedTableHead.propTypes = {
  onRequestSort: PropTypes.func.isRequired,
  order: PropTypes.oneOf(['asc', 'desc']).isRequired,
  orderBy: PropTypes.string.isRequired,
  rowCount: PropTypes.number.isRequired,
};

// EnhancedTableToolbar function takes in props and renders the toolbar with filtering and button functionality
function EnhancedTableToolbar({ searchTerm, setSearchTerm, startDate, setStartDate, endDate, setEndDate, onDatesChange }) {
  // Function to handle click on the "Apply" button
  const handleApplyClick = () => {
  onDatesChange(startDate, endDate);
  };

  return (
    <Toolbar
      sx={{
        pl: { sm: 2 },
        pr: { xs: 1, sm: 1 },
      }}
    >
        <Typography
          sx={{ flex: '1 1 100%' }}
          variant="h6"
          id="tableTitle"
          component="div"
        >
          Transactions
        </Typography>

        <TextField
          id="StartDate"
          type="date"
          sx={{ ml: 2, width: 200 }}
          required
          value={startDate}
          onChange={(event) => setStartDate(event.target.value)}
        />
        <TextField
          id="EndDate"
          type="date"
          sx={{ ml: 2, width: 200 }}
          required
          value={endDate}
          onChange={(event) => setEndDate(event.target.value)}
        />
        <Button variant="contained" onClick={handleApplyClick} sx={{ ml: 2 }}>
          Apply
        </Button>

        <TextField
        label="Search"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        sx={{ ml: 2, width: 200 }}
      />
      
    </Toolbar>
  );
}

export default function Transactions() {
  const [order, setOrder] = React.useState('asc');
  const [orderBy, setOrderBy] = React.useState('Date');
  const [page, setPage] = React.useState(0);
  const [dense, setDense] = React.useState(false);
  const [rowsPerPage, setRowsPerPage] = React.useState(10);
  const [searchTerm, setSearchTerm] = React.useState('');
  const [startDate,setStartDate] = React.useState("")
  const [endDate,setEndDate] = React.useState("")


  const [rows, setRows] = React.useState([]);

  React.useEffect(() => {
    async function fetchRowData() {
      try {
        const response = await fetchData('https://cryogenetics-logistics-solution.azurewebsites.net/api/transaction', 'GET');
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

  //DEFINE WHAT THE COLLUMNS ARE FILTERED IN SEARCH
  const filterRows = (row) => {
    return (
      row.employee_alias.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.client_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.location_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.act.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.address.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.comment.toLowerCase().includes(searchTerm.toLowerCase())
    );
  };
  const filteredRows = rows.filter(filterRows);

  // Function to be called when both start and end dates are changed
  const handleDatesChange = async (start, end) => {
    // Check if both start and end dates are selected
    if (start && end) {
      // Convert start and end dates to Date objects
      const startDateObj = new Date(start);
      const endDateObj = new Date(end);

      // Compare start and end dates
      if (startDateObj <= endDateObj) {
        try {
          const response = await fetchData(`https://cryogenetics-logistics-solution.azurewebsites.net/api/transaction?start_date=${start}&end_date=${end}`, 'GET');
          if (response == null){
            alert("No data available for that time period.");
          }
          else {
            setRows(response); // Update "rows" state with fetched data
          }
        } catch (error) {
          console.error(error);
        }
      } else {
        // Show an error message or perform any other action to handle invalid dates
        alert("End date must be after start date.");
      }
    } else {
      try {
        const response = await fetchData('https://cryogenetics-logistics-solution.azurewebsites.net/api/transaction', 'GET');
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
  };

  const handleRequestSort = (event, property) => {
    const isAsc = orderBy === property && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(property);
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleChangeDense = (event) => {
    setDense(event.target.checked);
  };

  const navigate = useNavigate();
  const handleActClick = () => {
    navigate('/acts');
  }

  // Avoid a layout jump when reaching the last page with empty rows.
  const emptyRows =
    page > 0 ? Math.max(0, (1 + page) * rowsPerPage - rows.length) : 0;

  return (
    <Box sx={{ width: '100%' }}>
    <div className = "grid-container">
      <div className = "grid-child table"><Paper sx={{ width: '100%', mb: 2 }}>
        <EnhancedTableToolbar searchTerm={searchTerm} setSearchTerm={setSearchTerm} startDate={startDate} setStartDate={setStartDate} endDate={endDate} setEndDate={setEndDate} onDatesChange={handleDatesChange} />
          <TableContainer>
            <Table
              sx={{ minWidth: 750 }}
              aria-labelledby="tableTitle"
              size={dense ? 'small' : 'medium'}
            >
              <EnhancedTableHead
                order={order}
                orderBy={orderBy}
                onRequestSort={handleRequestSort}
                rowCount={filteredRows.length}
              />
              <TableBody>
              
                {//sort the rows based on what "direction" is currently active, and what collumn is selected
                  stableSort(filteredRows, getComparator(order, orderBy))
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((row, index) => {
                    const labelId = `enhanced-table-checkbox-${index}`;

                    return (
                      <TableRow
                        hover
                        role="checkbox"
                        tabIndex={-1}
                        key={row.transaction_id}
                      >
                        
                        <TableCell
                          component="th"
                          id={labelId}
                          scope="row"
                          padding="none"
                          align='center'
                        >
                          {row.date}
                        </TableCell>
                        <TableCell align='center'>{row.act}</TableCell>
                        <TableCell align="center">{row.employee_alias}</TableCell>
                        <TableCell align="center">{row.location_name}</TableCell>
                        <TableCell align="center">{row.address}</TableCell>
                        <TableCell align="center">
                          {row.client_name === null ? "NULL" : row.client_name}
                        </TableCell>
                        <TableCell align="center">{row.container_status_name}</TableCell>
                        <TableCell align="center">{row.container_sr_number}</TableCell>
                        <TableCell align="center">{row.comment}</TableCell>
                      </TableRow>
                    );
                  })}
                {emptyRows > 0 && (
                  <TableRow
                    style={{
                      height: (dense ? 33 : 53) * emptyRows,
                    }}
                  >
                    <TableCell colSpan={6} />
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
          <TablePagination
            rowsPerPageOptions={[10, 25, 50]}
            component="div"
            count={rows.length}
            rowsPerPage={rowsPerPage}
            page={page}
            onPageChange={handleChangePage}
            onRowsPerPageChange={handleChangeRowsPerPage}
          />
        </Paper>
        </div>
        <div className = "grid-child-buttons">
          <Button variant='contained' onClick={handleActClick}> ACT Overview </Button>
        </div>
      </div>
      <FormControlLabel
        control={<Switch checked={dense} onChange={handleChangeDense} />}
        label="Dense padding"
        sx={{paddingLeft:"20px"}}
      />
      
    </Box>
    
  );
}
