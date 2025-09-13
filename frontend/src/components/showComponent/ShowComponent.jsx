import "./Show.css"
import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom';
function ShowComponent({ url, sessionId }) {
    const [polls, setPolls] = useState([]);
    const [effect, setEffect] = useState(false);
    const [pollResults, setPollResults] = useState({});
    const navigate = useNavigate();
    // get all the polls
    const getPolls = async () => {
        try {
            const res = await fetch(`${url}/polls`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });

            const response = await res.json();
            if (res.ok) {
                setPolls([...response])
                fetchAllResults(response);
                setEffect(true);
            }
        }
        catch (error) {
            console.log("Error: Something went wrong getting all the polls");
        }
    };
    // create a vote object
    const AddVote = async (optionId, pollId, sessionId, caption) => {
        let voteObj = {
            userId: parseInt(sessionId),
            voteOptionId: parseInt(optionId),
            publishedAt: new Date().toISOString(),
        };

        try {

            const res = await fetch(`${url}/polls/${pollId}/votes`, {
                method: 'Post',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(voteObj)
            });

            if (res.ok) {
                alert("OK: Voted for option " + caption);
                setEffect(true);
            }
        }

        catch (error) {
            alert("Error: Failed to register your vote for" + caption);
        }

    }
    // fetch all the results from the list of polls
    const fetchAllResults = async (pollList) => {
        const resultsMap = {};

        await Promise.all(
            pollList.map(async (poll) => {
                try {
                    const res = await fetch(`${url}/polls/${poll.id}/result`, {
                        method: 'GET',
                        headers: { 'Content-Type': 'application/json' }
                    });

                    if (res.ok) {
                        const result = await res.json();
                        resultsMap[poll.id] = result;
                    }
                } catch (error) {
                    alert(`Error fetching result for poll ${poll.id}:`, error);
                }
            })
        );
        setPollResults(resultsMap);
    };
    // helper function to gather the specific vote counter for each vote
    const getVoteCount = (pollId, option) => {
        if (pollResults[pollId] != undefined) {
            for (let key in pollResults[pollId]) {
                if (key.includes(`id:${option.id}`) && key.includes(`caption:${option.caption}`)) {
                    return pollResults[pollId][key];
                }
            }

        }
        return 0;
    }
    //Update polls and their vote counter each time there as been changes to the data
    useEffect(() => {
        getPolls();
        const interval = setInterval(() => {
            getPolls();
        }, 3000);

        return () => clearInterval(interval);
    }, [effect]);

    //Check if the sessionId is invalid/not there anymore
    useEffect(() => {
        if (!sessionId) {

            navigate("/users");
        }
    }, [sessionId, navigate])

    return (
        <div id="showVotes">
            <h4 id="showPollsHeader">Polls </h4>
            <div className="polls">
                {polls.map((poll) => (
                    <div className="pollObj" key={poll.id}>
                        <h1>{poll.question}</h1>
                        <p>Published: {poll.published_At}</p>
                        <p>Valid Until: {poll.validUntil}</p>
                        <ul>
                            {poll.voteOptions.map((option, index) => (
                                <li key={index}>
                                    <button className="voteBtn" onClick={() => AddVote(option.id, poll.id, sessionId, option.caption)}>{option.caption}</button>
                                    <span className="voteCounter">
                                        Votes: {getVoteCount(poll.id, option)}
                                    </span>
                                </li>
                            ))}
                        </ul>
                    </div>
                ))}
            </div>
        </div>

    )

}

export default ShowComponent