import React, { useState } from 'react';
import { Button, MenuItem,Select } from '@mui/material';
import fetchData from '../globals/fetchData';

export default function Report() {
    const [data, setData] = React.useState([]);
    const [filteredData, setFilteredData] = React.useState([]);
    const [selectedMonth, setSelectedMonth] = useState('');
    const [selectedYear, setSelectedYear] = useState('');
    const [endDateString, setEndDateString] = useState('');
    const [startDateString, setStartDateString] = useState('');

   // useEffect hook to update startDateString and endDateString whenever selectedMonth or selectedYear changes
   React.useEffect(() => {
    if (selectedMonth && selectedYear) {
      // Convert month value to 0-based index (January is 0)
      const monthIndex = selectedMonth - 1;
      const year = selectedYear;

      // Create start_date and end_date headers for API URL
      const startDate = new Date(year, monthIndex, 1);
      const endDate = new Date(year, monthIndex + 1, 0, 23, 59, 59);

      // THIS IS IN AMERICAN TIMES, DUE TO toISOString transporting us back in time by a day
      const startDateString = `${startDate.toISOString().slice(0, 10)} 00:00:00`;
      setStartDateString(startDateString);
      const endDateString = `${endDate.toISOString().slice(0, 10)} 23:59:59`;
      setEndDateString(endDateString);
    }
  }, [selectedMonth, selectedYear]);

  async function createReport() {
    if (!selectedMonth && !selectedYear) {
        console.error("SELECT A MONTH AND YEAR");
        return
    }

    const url = `/api/transaction?start_date=${startDateString}&end_date=${endDateString}`;
    try {
        const [response, filteredData] = await Promise.all([
            fetchData(url, 'GET'), 
            fetchData(url, 'GET').then(data => 
              data
                .filter(transaction => transaction.client_id !== null)
                .filter(transaction => transaction.act === "Sent out" || transaction.act === "Returned")
                .sort((a, b) => {
                  // Sort by container_sr_number
                  if (a.container_sr_number < b.container_sr_number) {
                    return -1;
                  } else if (a.container_sr_number > b.container_sr_number) {
                    return 1;
                  }
                  // If container_sr_number is the same, sort by date
                  if (a.date < b.date) {
                    return -1;
                  } else if (a.date > b.date) {
                    return 1;
                  }
                  return 0;
                })
                .map(transaction => {
                  return {
                    ...transaction,
                    date: new Date(transaction.date).toLocaleDateString('en-GB')
                  };
                })
            )
          ]);
    
        setData(response);
        setFilteredData(filteredData);
      
        // Create CSV header row
        const csvHeader = 'Client Name,Container Number,Start Date,End Date,Location\n';
    
        //make an array with clientNames
        const clientNames = []
        for(let i=0; i < filteredData.length; i++){
            if(!clientNames.includes(filteredData[i].client_name)){
                clientNames.push(filteredData[i].client_name)
            }
        }
        
        const fullReport = clientNames.map((clientName) => {
            // Call createSectionPerClient function for each clientName
            const filteredDataForClient = filteredData.filter(transaction => transaction.client_name === clientName);
            return createSectionPerClient(filteredDataForClient);
        });

        //Removes the commas between the array indexes
        let reportString = ""
        fullReport.map((table)=>(
            reportString+=table
        ))

            //add the header to the final report
        const finalReport= csvHeader + reportString

        // Download CSV file using CSVLink component
        const csvData = 'data:text/csv;charset=utf-8,' + encodeURIComponent(finalReport);
        const downloadLink = document.createElement('a');
        downloadLink.href = csvData;
        downloadLink.download = selectedMonth+'report.csv'; // Set the filename for the downloaded CSV file
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
    } catch (error) {
        console.error(error);
    }
  }
  
  function createSectionPerClient(client) {
    // Sort transactions by date in ascending order
    const sortedTransactions = client.sort((a, b) => new Date(a.date) - new Date(b.date));
  
    // Create empty row with client name
    const clientRow = `${client[0].client_name},,,,`;

    // Create a Set to keep track of unique container_sr_numbers
    const containerSrNumbersSet = new Set();
  
    // Create CSV data rows
    const csvData = sortedTransactions.map((transaction, index) => {
      const capacityTempId = transaction.liter_capacity + '-' + transaction.temp_id;
      const date = transaction.date;
      const endDate = SetEndDate(transaction.transaction_id,transaction.container_sr_number, transaction.date); // Replace with actual value from SetEndDate() function
      const location = transaction.container_status_name === 'At client' ? 'At client' : transaction.location_name;
  
      // Check if transaction_act is "Sent out" and it's the first transaction
    if (index === 0 && transaction.act === 'Sent out' && !containerSrNumbersSet.has(transaction.container_sr_number)) {
        // Add the container_sr_number to the Set to keep track of unique values
        containerSrNumbersSet.add(transaction.container_sr_number);

        const day = startDateString.slice(8, 10);
        const month = startDateString.slice(5, 7);
        const year = startDateString.slice(0, 4);
        let startDate = `${day}/${month}/${year}`;
        console.log(startDateString)
        console.log(startDate)
        // Create an additional row with "Sent out" and location as transaction_act and location_name respectively
        const stayHomeRow = `,${capacityTempId},${startDate},${date},${transaction.location_name}`;
        // Create the regular row with endDate as transaction.date
        const regularRow = `,${capacityTempId},${date},${endDate},${location}`;
        return [stayHomeRow, regularRow].join('\n');
      } else {
        // Create regular row with endDate as transaction.date
        return [`,${capacityTempId},${date},${endDate},${location}`];
      }
    }).join('\n');
    console.log(csvData)
  
    return clientRow + '\n' + csvData + '\n';
  }


  function SetEndDate(transaction_id, container_sr_number) {
    let endDate = ''; // Initialize end date as empty string
  
    // Loop through filteredData array
    for (let i = 0; i < filteredData.length; i++) {
      const transaction = filteredData[i];
  
      // Compare transaction_id, container_sr_number, and date with each transaction
      if (
        transaction.transaction_id > transaction_id &&
        transaction.container_sr_number === container_sr_number &&
        (transaction.act === 'Sent out' || transaction.act === 'Returned' || transaction.act === 'Discarded')
      ) {
        endDate = transaction.date; // Set end date to next transaction's date
        break; // Exit loop once next transaction is found
      }
    }
  
    // If endDate is still empty, set it to the last day of the month based on user Input
    if (endDate === '') {
        const day = endDateString.slice(8, 10);
        const month = endDateString.slice(5, 7);
        const year = endDateString.slice(0, 4);
        endDate = `${day}/${month}/${year}`;
      }
  
    return endDate;
  }  

  return (
    <>
      <Select
        value={selectedMonth}
        onChange={(event) => setSelectedMonth(event.target.value)}
        label="Month"
        sx={{ m: 1 }}
      >
        <MenuItem value={1}>January</MenuItem>
        <MenuItem value={2}>February</MenuItem>
        <MenuItem value={3}>March</MenuItem>
        <MenuItem value={4}>April</MenuItem>
        <MenuItem value={5}>May</MenuItem>
        <MenuItem value={6}>June</MenuItem>
        <MenuItem value={7}>July</MenuItem>
        <MenuItem value={8}>August</MenuItem>
        <MenuItem value={9}>September</MenuItem>
        <MenuItem value={10}>October</MenuItem>
        <MenuItem value={11}>November</MenuItem>
        <MenuItem value={12}>December</MenuItem>
      </Select>
      <Select
        value={selectedYear}
        onChange={(event) => setSelectedYear(event.target.value)}
        label="Year"
        sx={{ m: 1 }}
      >
        <MenuItem value={2023}>2023</MenuItem>
        {/* Add more years as needed */}
      </Select>
      <Button onClick={createReport} variant="contained" sx={{ m: 6 }}>
        Download
      </Button>
    </>
  );
};
