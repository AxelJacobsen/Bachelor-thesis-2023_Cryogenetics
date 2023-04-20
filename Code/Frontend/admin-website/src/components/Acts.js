import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import {
  Box,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TableSortLabel,
  Toolbar,
  Typography,
  Paper,
  IconButton,
  Tooltip,
  FormControlLabel,
  Switch,
  TextField,
} from '@mui/material';
import { visuallyHidden } from '@mui/utils';
import FilterListIcon from '@mui/icons-material/FilterList';
import AddActModal from './popup/AddActModal';
import './TableLayout.css';
import {stableSort, getComparator} from '../globals/globalFunctions';
import fetchData from '../globals/fetchData';


const headCells = [
  {
    id: 'act_name', numeric: false, disablePadding: true, label: 'act_name',
  },
  {
    id: 'description', numeric: false, disablePadding: true, label: 'description',
  },
  {
    id: 'is_active', numeric: true, disablePadding: true, label: 'is_active', 
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
          Act's
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

export default function Acts() {
  const [order, setOrder] = React.useState('asc');
  const [orderBy, setOrderBy] = React.useState('act_name');
  const [page, setPage] = React.useState(0);
  const [dense, setDense] = React.useState(false);
  const [rowsPerPage, setRowsPerPage] = React.useState(10);

  const [selectedRow, setSelectedRow] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const [searchTerm, setSearchTerm] = React.useState('');
  const [rows, setRows] = React.useState([]);

  async function fetchRowData() {
    try {
      const response = await fetchData('/api/act', 'GET');
      setRows(response);
      console.log(response)
    } catch (error) {
      console.error(error);
    }
  }

  React.useEffect(() => {
    fetchRowData();
  }, []);

  const handleModalClose = () => {
    fetchRowData();
  }

  //DEFINE WHAT THE COLLUMNS ARE FILTERED IN SEARCH
  const filterRows = (row) => {
    return (
      row.act_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      row.description.toLowerCase().includes(searchTerm.toLowerCase())
    );
  };
  const filteredRows = rows.filter(filterRows);

  const handleOpenModal = () => {
    setOpenModal(true);
  };

  function handleRowClick(rowData) {
    setSelectedRow(rowData);
  }

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
  const handleTransactionClick = () =>{
    navigate('/');
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
                        key={row.act_name}
                      >
                        
                        <TableCell
                          component="th"
                          id={labelId}
                          scope="row"
                          padding="none"
                          align='center'
                        >
                          {row.act_name}
                        </TableCell>
                        <TableCell align='center'>{row.description}</TableCell>
                        <TableCell align="center">{row.is_active ? "True" : "False"}</TableCell>
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
        <Button variant='contained' color='success' onClick={handleOpenModal}> Add Act </Button>
        <AddActModal open={openModal} setOpen={setOpenModal} onClose={handleModalClose} />
        <Button variant='contained' onClick={handleTransactionClick}> Back to Transaction screen </Button>
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
