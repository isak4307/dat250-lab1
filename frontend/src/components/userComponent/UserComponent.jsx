import "./User.css"
import { useState } from 'react'
import { useNavigate } from 'react-router-dom';
function UserComponent({ url, setSessionId }) {
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const handleUserSubmit = async (e) => {
    e.preventDefault();
    const data = new FormData(e.target);
    const actionType = e.nativeEvent.submitter.value;
    let userObj = {
      username: data.get('username'),
      email: data.get('email')
    };
    //divide the logic between creating the user and signing inn as the user
    if (actionType == "Create User") {
      CreateUser(userObj, url, setSessionId, setMessage);
    }
    else {
      let value = SignInn(userObj, url, setSessionId, setMessage);
      value.then(v => {
        if (v == true) {
          setTimeout(() => {
            navigate("/");
          }, 3000);
        }
      })
    }
  };
  return (
    <div id="createUsers">
      <h4 id="createUserHeader">Create/Sign inn </h4>
      <form id="createUserForm" onSubmit={handleUserSubmit}>
        <label id="LUsername" htmlFor="username">Username</label>
        <input id="username" name="username" type="text" placeholder="Username" required />
        <label id="LEmail" htmlFor="email" >Email</label>
        <input id="email" name="email" type="email" placeholder="Email" required />
        <input id="createUserBtn" className="submitBtn" type="submit" value="Create User" />
        <input id="logInUserBtn" className="submitBtn" type="submit" value="log inn" />
      </form>
      <p>{message}</p>
    </div>
  )
}
const SignInn = async (userObj, url, setSessionId, setMessage) => {
  try {
    const res = await fetch(`${url}/users/signInn`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userObj)
    });
    if (res.headers.get('Content-Type') == 'application/json') {
      //We got a HTTP OK response back
      const response = await res.json()
      const jsonResp = JSON.stringify(response)
      const user = JSON.parse(jsonResp)
      setSessionId(user.id)
      setMessage(`Successfully signed inn as  ` + user.username)
      return true;
    }
    else {
      //HTTP bad request
      setMessage(`Error:Something went wrong signing inn the user,\n Try again`)
      setTimeout(() => { setMessage("") }, 3000);
      return false;
    }
  }
  catch (error) {
    setMessage(`Error: ${error.body}`)
    setTimeout(() => { setMessage("") }, 3000);
    return false;
  }
};
const CreateUser = async (userObj, url, setSessionId, setMessage) => {
  try {
    const res = await fetch(`${url}/users`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userObj)
    });
    if (res.headers.get('Content-Type') == 'application/json') {
      const response = await res.json()
      const jsonResp = JSON.stringify(response)
      const user = JSON.parse(jsonResp)
      setSessionId(user.id)
      setMessage(`OK: Created user ` + user.username)
      setTimeout(() => { setMessage("") }, 3000);
    }
    else {
      setMessage(`Error:Something went wrong creating the user\n Try again`)
      setTimeout(() => { setMessage("") }, 3000);
    }

  }
  catch (error) {
    setMessage(`Error: ${error.message}`)
    setTimeout(() => { setMessage("") }, 3000);
  }
};
export default UserComponent
