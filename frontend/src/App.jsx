import './App.css'
import UserComponent from './components/userComponent/UserComponent'
import PollComponent from './components/pollComponent/PollComponent'
import VoteComponent from './components/voteComponent/VoteComponent'
function App() {
  const URL = "http://localhost:8080";
  return (
    <>
    <UserComponent url={URL} />
    <PollComponent url={URL} />
    <VoteComponent url={URL} />
    </>
  );
}
export default App

