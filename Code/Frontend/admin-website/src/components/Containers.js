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
import FilterListIcon from '@mui/icons-material/FilterList';
import { visuallyHidden } from '@mui/utils';
import EditContainerModal from './popup/EditContainerModal';
import AddContainerModal from './popup/AddContainerModal';
import './TableLayout.css';

function createData(serialnr, nr, model, location, customer, address, last_filled, invoice, status) {
  return {
    serialnr, nr, model, location, customer, address, last_filled, invoice, status,
  };
}



const rows = [
  createData('1234-999','047-1', 'ET11', "Hamar", "United Fishermen",'Hamar','29-01-23',"22-01-24", "In use"),
  createData('12334-219','047-2', 'ET11', "Tronny", "United Fi3shermen",'aaaa','29-01-23',"22-01-24", "Broken"),
  createData('122134-93299','047-3', 'ET11', "Haaamar", "United Fish2ermen",'eeee','29-01-23',"22-01-24", "Quarantined"),

]; 

function descendingComparator(a, b, orderBy) {
  if (b[orderBy] < a[orderBy]) {
    return -1;
  }
  if (b[orderBy] > a[orderBy]) {
    return 1;
  }
  return 0;
}

function getComparator(order, orderBy) {
  return order === 'desc'
    ? (a, b) => descendingComparator(a, b, orderBy)
    : (a, b) => -descendingComparator(a, b, orderBy);
}

// Since 2020 all major browsers ensure sort stability with Array.prototype.sort().
// stableSort() brings sort stability to non-modern browsers (notably IE11). If you
// only support modern browsers you can replace stableSort(exampleArray, exampleComparator)
// with exampleArray.slice().sort(exampleComparator)
function stableSort(array, comparator) {
  const stabilizedThis = array.map((el, index) => [el, index]);
  stabilizedThis.sort((a, b) => {
    const order = comparator(a[0], b[0]);
    if (order !== 0) {
      return order;
    }
    return a[1] - b[1];
  });
  return stabilizedThis.map((el) => el[0]);
}

const headCells = [
  {
    id: 'nr', numeric: false, disablePadding: true, label: 'NR',
  },
  {
    id: 'serialnr', numeric: false, disablePadding: true, label: 'SerialNR',
  },
  {
    id: 'model', numeric: false, disablePadding: true, label: 'Model',
  },
  {
    id: 'location', numeric: false, disablePadding: true, label: 'Location',
  },
  {
    id: 'customer', numeric: false, disablePadding: true, label: 'Customer',
  },
  {
    id: 'address', numeric: false, disablePadding: true, label: 'Address',
  },
  {
    id: 'last_filled', numeric: false, disablePadding: true, label: 'Last Filled',
  },
  {
    id: 'invoice', numeric: false, disablePadding: true, label: 'Invoice',
  },
  {
    id: 'status', numeric: false, disablePadding: true, label: 'Status',
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

function EnhancedTableToolbar() {
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
      
    </Toolbar>
  );
}

export default function Containers() {
  const [order, setOrder] = React.useState('asc');
  const [orderBy, setOrderBy] = React.useState('calories');
  const [page, setPage] = React.useState(0);
  const [dense, setDense] = React.useState(false);
  const [rowsPerPage, setRowsPerPage] = React.useState(10);

  const [selectedRow, setSelectedRow] = useState(null);
  const [openModal, setOpenModal] = useState(false);

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

  const handleModelClick = () =>{
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
        <EnhancedTableToolbar />
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
                rowCount={rows.length}
              />
              <TableBody>
                {stableSort(rows, getComparator(order, orderBy))
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((row, index) => {
                    const labelId = `enhanced-table-checkbox-${index}`;

                    return (
                      <TableRow
                        hover
                        role="checkbox"
                        tabIndex={-1}
                        key={row.serialnr}
                      >
                        
                        <TableCell
                          component="th"
                          id={labelId}
                          scope="row"
                          padding="none"
                          align='center'
                        >
                          {row.nr}
                        </TableCell>
                        <TableCell align='center'>{row.serialnr}</TableCell>
                        <TableCell align="center">{row.model}</TableCell>
                        <TableCell align="center">{row.location}</TableCell>
                        <TableCell align="center">{row.customer}</TableCell>
                        <TableCell align="center">{row.address}</TableCell>
                        <TableCell align="center">{row.last_filled}</TableCell>
                        <TableCell align="center">{row.invoice}</TableCell>
                        <TableCell align="center">{row.status}</TableCell>
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
        <Button variant='contained' onClick={handleModelClick}> Model Overview </Button>
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
