const PollComponent = (url) => {
  const handlePollSubmit = async (e) => {
    e.preventDefault();
    let pollObj = {
      username: e.target.question.value,
      email: e.target.email.value
    };
    
    await fetch(`${url}/polls`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(pollObj)
    });
    
  };
  return (

    <div id="createPolls">
      <h4>Create a Poll </h4>
      <form onSubmit={handlePollSubmit}>
        <label id="LPoll" htmlFor="question">Question</label>
        <input id="question" type="text" />
        <label id="LValidUntil" htmlFor="validUntil">Valid Until (date)</label>
        <input id="validUntil" type="datetime-local" />
        <label id="LPublishedAt" htmlFor="publishedAt">Published at (date)</label>
        <input id="publishedAt" type="datetime-local" />
        <input id="submitBtn" type="submit" value="Create Poll" />
      </form>
    </div>
  )
}
export default PollComponent