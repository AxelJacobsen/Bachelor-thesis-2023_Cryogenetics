import React from 'react'
import { useNavigate } from 'react-router-dom'
import './Containers.css';

export default function Containers() {
    let navigate = useNavigate();
  return (
    <div className='inventory'>
        Inventory page 
    </div>
  )
}