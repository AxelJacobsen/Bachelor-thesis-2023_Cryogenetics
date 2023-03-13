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
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import FilterListIcon from '@mui/icons-material/FilterList';
import { visuallyHidden } from '@mui/utils';
import Button from '@mui/material/Button';
import { useNavigate } from 'react-router-dom';
import './TableLayout.css';
import fetchData from '../globals/fetchData';


// createData function takes in the row data and returns an object with the properties of the row
function createData(id,Date,Act,Operator,Location_Name,Customer_Name,Nr,SerialNr,Status,Comment) {
  return {
    id,
    Date,
    Act,
    Operator,
    Location_Name,
    Customer_Name,
    Nr,
    SerialNr,
    Status,
    Comment,
  };
}

const rows = [
  createData(7,'31-01-23 11:25', 'Main needed', "Jan", "Hamar", "United Fishermen",'047-7','5555-221',"In use", "fix lid please"),
  createData(1,'31-01-23 09:25', 'Sold', "Ola Nordmann", "Hamar", "United Fishermen",'047-1','5555-231',"At Client", "Will be shipped ASAP"),
  createData(2,'31-01-23 10:25', 'Refilled', "Ola Nordmann", "Hamar", "United Fishermen",'047-2','5555-331',"In use", ""),
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
    id: 'Date', numeric: false, disablePadding: true, label: 'Date',
  },
  {
    id: 'Act', numeric: false, disablePadding: true, label: 'Act',
  },
  {
    id: 'Operator', numeric: false, disablePadding: true, label: 'Operator',
  },
  {
    id: 'Location_Name', numeric: false, disablePadding: true, label: 'Location_Name',
  },
  {
    id: 'Customer_Name', numeric: false, disablePadding: true, label: 'Customer_Name',
  },
  {
    id: 'Nr', numeric: false, disablePadding: true, label: '#Nr',
  },
  {
    id: 'SerialNr', numeric: false, disablePadding: true, label: 'SerialNr',
  },
  {
    id: 'Status', numeric: false, disablePadding: true, label: 'Status',
  },
  {
    id: 'Comment', numeric: false, disablePadding: true, label: 'Comment',
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
          Transactions
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

export default function Transactions() {
  
    
  const [order, setOrder] = React.useState('asc');
  const [orderBy, setOrderBy] = React.useState('Operator');
  const [page, setPage] = React.useState(0);
  const [dense, setDense] = React.useState(false);
  const [rowsPerPage, setRowsPerPage] = React.useState(10);

  const [searchTerm, setSearchTerm] = React.useState('');

  //DEFINE WHAT THE COLLUMNS ARE FILTERED IN SEARCH
  const filterRows = (row) => {
    return (
      row.Status.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.Location_Name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.Act.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.Operator.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.Customer_Name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.Comment.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.SerialNr.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.Nr.toLowerCase().includes(searchTerm.toLowerCase())
    );
  };
  const filteredRows = rows.filter(filterRows);

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
                          {row.Date}
                        </TableCell>
                        <TableCell align='center'>{row.Act}</TableCell>
                        <TableCell align="center">{row.Operator}</TableCell>
                        <TableCell align="center">{row.Location_Name}</TableCell>
                        <TableCell align="center">{row.Customer_Name}</TableCell>
                        <TableCell align="center">{row.Nr}</TableCell>
                        <TableCell align="center">{row.SerialNr}</TableCell>
                        <TableCell align="center">{row.Status}</TableCell>
                        <TableCell align="center">{row.Comment}</TableCell>
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
