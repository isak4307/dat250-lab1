import './App.css'
import { useState } from 'react'

import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import UserComponent from './components/userComponent/UserComponent'
import PollComponent from './components/pollComponent/PollComponent'
import ShowComponent from './components/showComponent/ShowComponent'
function App() {
  const URL = "http://localhost:8080";
  const [sessionId, setSessionId] = useState("");
  return (
    <Router>
      <>
        <Routes>
          <Route path="/users" element={<UserComponent url={URL} setSessionId={setSessionId} />} />
          <Route path="/" element={<PollComponent url={URL} sessionId={sessionId} />} />
        </Routes>
        <ShowComponent url={URL} sessionId={sessionId} />
      </>
    </Router>
  );
}
export default App