
import React, { useState } from 'react'
function VoteComponent({ url }) {
    const [message, setMessage] = useState('');
    const handleVoteSubmit = async (e) => {
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
                setMessage(`OK: Created user ${username}`)
            }
            else {
                setMessage(`Error:Something went wrong,\n Try again`)
            }
        }
        catch (error) {
            setMessage(`Error: ${error.message}`)
        }
    };
    /* const getAllPolls = {

    } */
    return (
        <div>

            <p>{message}</p>
        </div>
    )
}
export default VoteComponent