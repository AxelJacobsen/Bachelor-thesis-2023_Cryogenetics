// Helper function that takes two objects, a and b, and a property, orderBy, 
// and returns -1 if b[orderBy] < a[orderBy], 1 if b[orderBy] > a[orderBy], 
// and 0 if they are equal
function descendingComparator(a, b, orderBy) {
  if (b[orderBy] < a[orderBy]) {
    return -1;
  }
  if (b[orderBy] > a[orderBy]) {
    return 1;
  }
  return 0;
}

// Function that takes two parameters, order and orderBy, and returns a comparator function
// that can be used to sort an array of objects by the value of the property specified in orderBy
function getComparator(order, orderBy) {
  // If order is 'desc', return a comparator function that sorts in descending order,
  // otherwise return a comparator function that sorts in ascending order
  return order === 'desc'
    ? (a, b) => descendingComparator(a, b, orderBy)
    : (a, b) => -descendingComparator(a, b, orderBy); // '-' reverses the sort order
}

// Function that takes an array and a comparator function and returns a new sorted array that is guaranteed to be stable
function stableSort(array, comparator) {
  // Map each element of the input array to a tuple of the form [element, index]
  const stabilizedThis = array.map((el, index) => [el, index]);
  // Sort the tuples first by the output of the comparator function, then by the index of the original element
  stabilizedThis.sort((a, b) => {
    const order = comparator(a[0], b[0]);
    if (order !== 0) {
      return order;
    }
    return a[1] - b[1];
  });
  // Map the stabilized array back to an array of the original elements
  return stabilizedThis.map((el) => el[0]);
}

// Export the stableSort and getComparator functions so they can be used elsewhere in the code
export {stableSort , getComparator}
