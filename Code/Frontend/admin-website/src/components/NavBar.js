import React from 'react'
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@mui/material';
import '../App.css'

export default function NavBar() {


    const navigate = useNavigate();

    const [activeButton, setActiveButton] = useState(null);

    const handleClick = (buttonId) => {
        setActiveButton(buttonId);
    };

    return (

    <div className='navButtons'>

        <Button onClick={() => {navigate('/'); handleClick(1)}}
                variant={activeButton === 1 ? 'contained' : 'text'} 
                color={activeButton === 1 ? 'primary': 'inherit'}>
                Transactions 
        </Button>
        <Button onClick={() => {navigate('/customers'); handleClick(2)}}
                variant={activeButton === 2 ? 'contained' : 'text'} 
                color={activeButton === 2 ? 'primary' : 'inherit'}>
                Customers 
        </Button>
        <Button onClick={() => {navigate('/containers'); handleClick(3)}}
                variant={activeButton === 3 ? 'contained' : 'text'} 
                color={activeButton === 3 ? 'primary' : 'inherit'}>
                Containers 
        </Button>
        <Button onClick={() => {navigate('/users'); handleClick(4)}}
                variant={activeButton === 4 ? 'contained' : 'text'} 
                color={activeButton === 4 ? 'primary' : 'inherit'}>
                Users
        </Button>
        <Button onClick={() => {navigate('/locations'); handleClick(5)}}
                variant={activeButton === 5 ? 'contained' : 'text'}
                color={activeButton === 5 ? 'primary' : 'inherit'}> 
                Locations 
        </Button>
        <Button onClick={() => {navigate('/report'); handleClick(6)}}
                variant={activeButton === 6 ? 'contained' : 'text'}
                color={activeButton === 6 ? 'primary' : 'inherit'}> 
                Generate Report
        </Button>
        <Button onClick={() => {navigate('/devices'); handleClick(7)}}
                variant={activeButton === 7 ? 'contained' : 'text'}
                color={activeButton === 7 ? 'primary' : 'inherit'}> 
                Devices
        </Button>
    </div>
    )
}
