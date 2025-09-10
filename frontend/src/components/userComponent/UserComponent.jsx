import "./User.css"
import React, { useState } from 'react'
function UserComponent({ url }) {
  const [message, setMessage] = useState('');
  const handleUserSubmit = async (e) => {
    e.preventDefault();
    let userObj = {
      username: e.target.username.value,
      email: e.target.email.value
    }; 
    try {
      const res = await fetch(`${url}/users`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userObj)
      });
      const response = await res.json()
      if (res.ok) {
        setMessage(`OK: Created user ${response}`)
      }
      else{
        setMessage(`Error:Something went wrong,\n Try again`)
      }
    }
    catch (error) {
      setMessage(`Error: ${error.message}`)
    }
  };
  return (
    <div id="createUsers">
      <h4 id="createUserHeader">Create a User </h4>
      <form id="createUser" onSubmit={handleUserSubmit}>
        <label id="LUsername" htmlFor="username">Username</label>
        <input id="username" type="text" placeholder="Username" minLength={1}/>
        <label id="LEmail" htmlFor="email" >Email</label>
        <input id="email" type="email" placeholder="Email" minLength={1} />
        <input id="submitBtn" type="submit" value="Create User" />
      </form>
      <p>{message}</p>
    </div>
  )
}
export default UserComponent
