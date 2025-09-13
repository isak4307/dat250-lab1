import "./Poll.css"

import { useState } from 'react';
function PollComponent({ url, sessionId }) {
  const [voteOptionSet, setVoteOptionSets] = useState([{ caption: '', presentationOrder: 0 }]);
  const [question, setQuestion] = useState("");
  const [validUntil, setValidUntil] = useState(undefined);
  const [publishedAt, setPublishedAt] = useState(undefined);
  // Add new fields for a new caption and presentation order.
  const addNewVoteOption = () => {
    // Initalize an empty caption and a presentationOrder with value 0
    setVoteOptionSets([...voteOptionSet, { caption: '', presentationOrder: 0 }]);
  };

  const handleChange = (index, event) => {
    const caption = event.target.name;
    const presentationOrder = event.target.value;
    const updatedVoteOption = [...voteOptionSet];
    updatedVoteOption[index][caption] = caption === 'presentationOrder' ? parseInt(presentationOrder) : presentationOrder;
    setVoteOptionSets(updatedVoteOption);
  };


  const handlePollSubmit = async () => {
    const voteOptionList = voteOptionSet.map(({ caption, presentationOrder }) => ({
      caption,
      presentationOrder
    }));
    let pollObj = {
      question: question,
      validUntil: new Date(validUntil).toISOString(),
      publishedAt: new Date(publishedAt).toISOString(),
      voteOptions: voteOptionList
    };
    try {
      const res = await fetch(`${url}/polls/` + sessionId, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(pollObj)
      });

      if (res.ok) {
        alert("OK: created poll " + question);
      }
    }
    catch (e) {
      alert("Error: Unable to create poll " + question);
    }
  };
  return (
    <div id="createPolls">
      <h4 id="createPollHeader">Create a Poll </h4>
      <label id="LPoll" htmlFor="question">Question</label>
      <input value={question} type="text" onChange={e => setQuestion(e.target.value)} required placeholder="Question" />

      <label id="LPublishedAt" htmlFor="publishedAt">Published at</label>
      <input value={publishedAt} type="datetime-local" onChange={e => setPublishedAt(e.target.value)} required placeholder="Published at" />

      <label id="LValidUntil" htmlFor="validUntil">Valid Until</label>
      <input value={validUntil} type="datetime-local" onChange={e => setValidUntil(e.target.value)} required placeholder="Valid until" />
      <ul>
        {voteOptionSet.map((voteOption, index) => (
          <li key={index}>
            <label>
              Caption:
            </label>
            <input
              type="text"
              name="caption"
              value={voteOption.caption}
              onChange={(e) => handleChange(index, e)}
            />
            <label>
              PresentationOrder:
            </label>
            <input
              type="number"
              name="presentationOrder"
              value={voteOption.presentationOrder}
              onChange={(e) => handleChange(index, e)}
              min={1}
            />
          </li>
        ))}
      </ul>
      <button type="button" onClick={addNewVoteOption}>Add new voteOption</button>
      <input id="submitBtn" type="button" onClick={handlePollSubmit} value="Create Poll" />
    </div>
  )
}


export default PollComponent