import React from 'react'
import { useNavigate } from 'react-router-dom'

function Inventory() {
    let navigate = useNavigate();
  return (
    <div>
        Inventory page <button onClick={() => {navigate("/login")}}> Change to LogIn page </button>
    </div>
  )
}

export default Inventory