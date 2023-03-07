import * as React from 'react';
import PropTypes from 'prop-types';
import Box from '@mui/material/Box';
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

// createData function takes in the row data and returns an object with the properties of the row
function createData(id,date,act,operator,location,client,nr,serialnr,status,comment) {
  return {
    id,
    date,
    act,
    operator,
    location,
    client,
    nr,
    serialnr,
    status,
    comment,
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
  console.log(orderBy)
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
    id: 'date', numeric: false, disablePadding: true, label: 'Date',
  },
  {
    id: 'act', numeric: false, disablePadding: true, label: 'Act',
  },
  {
    id: 'operator', numeric: false, disablePadding: true, label: 'Operator',
  },
  {
    id: 'location', numeric: false, disablePadding: true, label: 'Location',
  },
  {
    id: 'client', numeric: false, disablePadding: true, label: 'Client',
  },
  {
    id: 'nr', numeric: false, disablePadding: true, label: '#Nr',
  },
  {
    id: 'serialnr', numeric: false, disablePadding: true, label: 'SerialNR',
  },
  {
    id: 'status', numeric: false, disablePadding: true, label: 'Status',
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
          Transactions
        </Typography>

        <Tooltip title="Filter list">
          <IconButton>
            <FilterListIcon />
          </IconButton>
        </Tooltip>
      
    </Toolbar>
  );
}

export default function Transactions({ isLoggedIn }) {
  
    
  const [order, setOrder] = React.useState('asc');
  const [orderBy, setOrderBy] = React.useState('operator');
  const [page, setPage] = React.useState(0);
  const [dense, setDense] = React.useState(false);
  const [rowsPerPage, setRowsPerPage] = React.useState(10);

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
                        key={row.id}
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
                        <TableCell align="center">{row.operator}</TableCell>
                        <TableCell align="center">{row.location}</TableCell>
                        <TableCell align="center">{row.client}</TableCell>
                        <TableCell align="center">{row.nr}</TableCell>
                        <TableCell align="center">{row.serialnr}</TableCell>
                        <TableCell align="center">{row.status}</TableCell>
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
