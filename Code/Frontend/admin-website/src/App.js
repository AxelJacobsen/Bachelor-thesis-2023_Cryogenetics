import React , { useState } from 'react'
import {BrowserRouter as Router, Routes, Route, Navigate, Link} from 'react-router-dom'
import Transactions from './components/Transactions'
import LogIn from './components/LogIn'
import Inventory from './components/Inventory'
import ErrorPage from './components/ErrorPage'
import './App.css'


function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false); // Replace with your authentication logic

  function handleLogIn() {
    setIsLoggedIn(true);
  }

  return (
    <Router>
    <nav>
      <Link to="/"> Transactions </Link>
      <Link to="/login"> LogIn </Link>
      <Link to="/inventory"> Inventory </Link>
    </nav>
      
      <Routes>
        <Route path="/" element={isLoggedIn ? <Transactions isLoggedIn={isLoggedIn}/> : <Navigate to="/login" />} />

        <Route path="/login" element={<LogIn onSignIn={handleLogIn} setIsLoggedIn={setIsLoggedIn}/>} />
        <Route path="/inventory" element={isLoggedIn ? <Inventory /> : <Navigate to="/login" />} />

        <Route path='*' element={<ErrorPage/>}></Route> {/* All deviations in URL lead to ErrorPage */}
      </Routes>
      <div> Copyright for Cryogenetics </div>
    </Router>
  )
}

export default App
