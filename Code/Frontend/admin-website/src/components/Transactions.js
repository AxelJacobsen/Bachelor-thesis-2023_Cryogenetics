import React from 'react'

export default function Transactions({ isLoggedIn }) {

  console.log("isLoggedIn: "+ isLoggedIn)
  return (
    <div>
      <h1>Welcome to the Transactions page!</h1>
      <p>{isLoggedIn ? "You're logged in" : "You're not logged in"}</p>
      
      {/* Main content of your app goes here */}
    </div>
  )
}

