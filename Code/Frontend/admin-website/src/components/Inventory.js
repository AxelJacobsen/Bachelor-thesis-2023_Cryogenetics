import React from 'react'
import { useNavigate } from 'react-router-dom'
import './Inventory.css';

function Inventory() {
    let navigate = useNavigate();
  return (
    <div className='inventory'>
        Inventory page <button onClick={() => {navigate("/login")}}> Change to LogIn page </button>
    </div>
  )
}

export default Inventory