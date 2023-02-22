import React from 'react'
import {BrowserRouter as Router, Routes, Route, Link} from 'react-router-dom'
import Transactions from './components/Transactions'
import LogIn from './components/LogIn'
import Inventory from './components/Inventory'
import ErrorPage from './components/ErrorPage'

function App() {
  return (
    <Router>
    <nav>
      <Link to="/transactions"> Transactions </Link>
      <Link to="/login"> LogIn </Link>
      <Link to="/inventory"> Inventory </Link>
    </nav>
      
      <Routes>
        <Route path='/transactions' element={<Transactions/>}></Route>
        <Route path='/login' element={<LogIn/>}></Route>
        <Route path='/inventory' element={<Inventory/>}></Route>
        <Route path='*' element={<ErrorPage/>}></Route>
      </Routes>
      <div> Footer </div>
    </Router>
  )
}

export default App
