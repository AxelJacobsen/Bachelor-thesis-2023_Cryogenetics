import React , { useState } from 'react'
import {BrowserRouter as Router, Routes, Route, Navigate, Link} from 'react-router-dom'
import Transactions from './components/Transactions'
import LogIn from './components/LogIn'
import Containers from './components/Containers'
import ErrorPage from './components/ErrorPage'
import Customers from './components/Customers'
import Locations from './components/Locations'
import QrCodes from './components/QrCodes'
import './App.css'
import Typography from '@mui/material/Typography';
import Users from './components/Users'
import Models from './components/Models'
import Statuses from './components/Statuses'
import Acts from './components/Acts'
import Button from '@mui/material/Button'
import NavBar from './components/NavBar'



function Copyright(props) {
  return (
    <Typography variant="body2" color="text.secondary" align="left" top="90" {...props}>
      {'Copyright © '}
      <Link color="inherit" href="https://www.cryogenetics.com/">
        Cryogenetics
      </Link>{' '}
      {new Date().getFullYear()/*NOT REAL COPYRIGHT*/}
      {'.'}
    </Typography>
  );
}

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false); // Replace with your authentication logic

  function handleLogIn() {
    setIsLoggedIn(true);
  }

  function handleLogOut() {
    setIsLoggedIn(false);
  }

  return (
    <Router>
    
    <nav>
    {isLoggedIn && //Hides navbar if not logged in
      <><NavBar /><div className='navButtons'>
            <Button onClick={handleLogOut} size='large' variant='contained' color='inherit'> Log Out </Button>
          </div></>
    }
    </nav>
      <Routes>
        <Route path="/" element={isLoggedIn ? <Transactions isLoggedIn={isLoggedIn}/> : <Navigate to="/login" />} />

        <Route path="/login" element={<LogIn onLogIn={handleLogIn} setIsLoggedIn={setIsLoggedIn}/>} />

        <Route path="/containers" element={isLoggedIn ? <Containers /> : <Navigate to="/login" />} />
        <Route path="/customers" element={isLoggedIn ? <Customers /> : <Navigate to="/login" />} />
        <Route path="/users" element={isLoggedIn ? <Users /> : <Navigate to="/login" />} />
        <Route path="/locations" element={isLoggedIn ? <Locations /> : <Navigate to="/login" />} />
        <Route path="/qrcodes" element={isLoggedIn ? <QrCodes /> : <Navigate to="/login" />} />
        <Route path="/models" element={ <Models />} />        
        <Route path="/acts" element={ <Acts />} />
        <Route path="/statuses" element={ <Statuses />} />

        <Route path='*' element={<ErrorPage/>} /> {/* All deviations in URL lead to ErrorPage */}
      </Routes>
      <Copyright sx={{ mt: 8, mb: 4 }} />
    </Router>
  )
}

export default App
