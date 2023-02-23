import React from 'react'

function Transactions({ isLoggedIn }) {

  console.log("isLoggedIn: "+ isLoggedIn)
  return (
    <div>
      <h1>Welcome to the Transactions page!</h1>
      <p>{isLoggedIn ? "You're logged in" : "You're not logged in"}</p>
      
      {/* Main content of your app goes here */}
    </div>
  )
}

export default Transactions