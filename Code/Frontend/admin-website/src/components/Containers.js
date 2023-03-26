import * as React from 'react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
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
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import { TextField } from '@mui/material';
import FilterListIcon from '@mui/icons-material/FilterList';
import { visuallyHidden } from '@mui/utils';
import EditContainerModal from './popup/EditContainerModal';
import AddContainerModal from './popup/AddContainerModal';
import './TableLayout.css';
import {stableSort , getComparator} from '../globals/globalFunctions';
import fetchData from '../globals/fetchData';

const headCells = [
  {
    id: 'id', numeric: false, disablePadding: true, label: 'id',
  },
  {
    id: 'serial_number', numeric: false, disablePadding: true, label: 'SerialNr',
  },
  {
    id: 'status', numeric: false, disablePadding: true, label: 'Status',
  },
  {
    id: 'model', numeric: false, disablePadding: true, label: 'Model_Name',
  },
  {
    id: 'location_name', numeric: false, disablePadding: true, label: 'Location_Name',
  },
  {
    id: 'customer_name', numeric: false, disablePadding: true, label: 'Customer Name',
  },
  {
    id: 'address', numeric: false, disablePadding: true, label: 'Address',
  },
  {
    id: 'invoice', numeric: false, disablePadding: true, label: 'Invoice',
  },
  {
    id: 'last_filled', numeric: false, disablePadding: true, label: 'Last Filled',
  },
  {
    id: 'comment', numeric: false, disablePadding: true, label: 'Comment',
  },
  {
    id: 'maintenance_needed', numeric: false, disablePadding: true, label: 'Maintenance Needed',
  },
  {
    id: 'production_date', numeric: false, disablePadding: true, label: 'Production Date',
  },
  
];

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

function EnhancedTableToolbar({ searchTerm, setSearchTerm }) {
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
          Containers
        </Typography>

        <Tooltip title="Filter list">
          <IconButton>
            <FilterListIcon />
          </IconButton>
        </Tooltip>
      
        <TextField
        label="Search"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        sx={{ ml: 2, width: 200 }}
      />
    </Toolbar>
  );
}

export default function Containers() {
  const [order, setOrder] = React.useState('asc');
  const [orderBy, setOrderBy] = React.useState('NR');
  const [page, setPage] = React.useState(0);
  const [dense, setDense] = React.useState(false);
  const [rowsPerPage, setRowsPerPage] = React.useState(10);

  const [selectedRow, setSelectedRow] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  const [rows, setRows] = React.useState([]);

  React.useEffect(() => {
    async function fetchRowData() {
      try {
        const response = await fetchData('/api/user/container', 'GET');
        setRows(response);
      } catch (error) {
        console.error(error);
      }
    }
    fetchRowData();
  }, []);
  console.log(rows)

    //DEFINE WHAT THE COLLUMNS ARE FILTERED IN SEARCH
    const filterRows = (row) => {
      return (
        row.model.toLowerCase().includes(searchTerm.toLowerCase()) 
      );
    };
    const filteredRows = rows.filter(filterRows);

  const navigate = useNavigate();

  function handleRowClick(rowData) {
    setSelectedRow(rowData);
  }
  const handleOpenModal = () => {
    setOpenModal(true);
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

  const handleModel_NameClick = () =>{
    navigate('/models');
  }

  const handleStatusClick = () =>{
    navigate('/statuses');
  }

  // Avoid a layout jump when reaching the last page with empty rows.
  const emptyRows =
    page > 0 ? Math.max(0, (1 + page) * rowsPerPage - rows.length) : 0;

  return (
    
    <Box sx={{ width: '100%' }}>
    <div className = "grid-container">
      <div className = "grid-child table"><Paper sx={{ width: '100%', mb: 2 }}>
        <EnhancedTableToolbar searchTerm={searchTerm} setSearchTerm={setSearchTerm}/>
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
                {stableSort(filteredRows, getComparator(order, orderBy))
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((row, index) => {
                    const labelId = `enhanced-table-checkbox-${index}`;

                    return (
                      <TableRow
                        hover
                        role="checkbox"
                        tabIndex={-1}
                        key={row.id}
                      >
                        
                        <TableCell
                          component="th"
                          id={labelId}
                          scope="row"
                          padding="none"
                          align='center'
                        >
                          {row.id}
                        </TableCell>
                        <TableCell align='center'>{row.serial_number}</TableCell>
                        <TableCell align="center">{row.model}</TableCell>
                        <TableCell align="center">{row.status}</TableCell>
                        <TableCell align="center">
                          {row.location_name === null ? "NULL" : row.location_name}
                        </TableCell>
                        <TableCell align="center">
                          {row.customer_name === null ? "NULL" : row.customer_name}
                        </TableCell>
                        <TableCell align="center">
                          {row.address === null ? "NULL" : row.address}
                        </TableCell>
                        <TableCell align="center">
                          {row.invoice === null ? "NULL" : row.invoice}
                        </TableCell>
                        <TableCell align="center">{row.last_filled}</TableCell>
                        
                        <TableCell align="center">
                          {row.comment === null ? "NULL" : row.comment}
                        </TableCell>
                        <TableCell align="center">
                          {row.maintenance_needed === 0 ? "No" : "Yes"}
                        </TableCell>
                        <TableCell align="center">{row.production_date}</TableCell>
                        
                        <TableCell onClick={() => handleRowClick(row)}> 
                        <Button variant="outlined"> Edit </Button>
                      </TableCell> 
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
            {selectedRow && ( //Checks if there is a selected Row, If this line isnt here, you will get an "Error child is empty" console message.
          <EditContainerModal
            selectedRow={selectedRow}
            setSelectedRow={setSelectedRow}
          />
        )} 
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
        <Button variant='contained' color='success' onClick={handleOpenModal}> Add container </Button>
        <AddContainerModal open={openModal} setOpen={setOpenModal} />
        <Button variant='contained' onClick={handleModel_NameClick}> Models Overview </Button>
        <Button variant='contained' onClick={handleStatusClick}> Status Overview </Button>
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
