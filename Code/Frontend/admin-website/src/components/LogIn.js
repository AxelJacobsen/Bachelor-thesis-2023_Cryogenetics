import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function LogIn({setIsLoggedIn}) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  function handleLogIn(event) {
    event.preventDefault();
    // Replace with your authentication logic
    const isLoggedIn = true;
    if (isLoggedIn) {
      setIsLoggedIn(true);
      navigate('/');
      console.log(isLoggedIn)
    }
  }

  return (
    <div>
      <h1>Log in</h1>
      <form onSubmit={handleLogIn}>
        <label>
          Email:
          <input type="text" value={email} onChange={event => setEmail(event.target.value)} />
        </label>
        <label>
          Password:
          <input type="password" value={password} onChange={event => setPassword(event.target.value)} />
        </label>
        <button type="submit">Log in</button>
      </form>
    </div>
  );
}

export default LogIn

