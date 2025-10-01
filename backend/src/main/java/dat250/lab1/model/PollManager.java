package dat250.lab1.model;

import dat250.lab1.actions.PollActions;
import dat250.lab1.actions.UserActions;
import dat250.lab1.actions.VoteActions;
import dat250.lab1.actions.VoteOptionActions;
import dat250.lab1.messager.Consumer;
import dat250.lab1.messager.MessagerSetup;
import dat250.lab1.messager.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import redis.clients.jedis.UnifiedJedis;

import java.io.Serializable;
import java.util.*;

@CrossOrigin
@Component
public class PollManager implements Serializable {

    private final UserActions userActions;
    private final PollActions pollActions;
    private final VoteOptionActions voteOptionActions;
    private final VoteActions voteActions;
    //Integer = Poll id
    private final HashMap<Integer, Poll> pollManager = new HashMap<>();
    // integer = poll ID
    private final HashMap<Integer, HashSet<Vote>> voteManager = new HashMap<>();
    private final UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");


    public PollManager(@Autowired UserActions userActions,
                       @Autowired PollActions pollActions,
                       @Autowired VoteOptionActions voteOptionActions,
                       @Autowired VoteActions voteActions) {
        this.userActions = userActions;
        this.pollActions = pollActions;
        this.voteOptionActions = voteOptionActions;
        this.voteActions = voteActions;
        /*this.messagerSetup = messagerSetup;
        this.consumer = consumer;*/
    }

    public HashSet<User> getUsers() {
        return new HashSet<>(this.userActions.getUsers());
    }

    public HashSet<Poll> getPolls() {
        return new HashSet<>(this.pollManager.values());
    }

    public Poll getPollById(Integer id) {
        return this.pollManager.get(id);
    }

    public User createUser(User user) {
        return this.userActions.createUser(user);
    }


    public Poll createPoll(Poll poll, User user) {
        //Create the Poll objects and its fields
        this.pollActions.setPollId(poll, user);
        this.pollManager.put(poll.getId(), poll);
        this.voteManager.put(poll.getId(), new HashSet<>());
        //registerTopic(poll);
        return poll;
    }

    /** Register a respective Topic to the poll
     * TODO
     */
  /*  private void registerTopic(Poll poll){
        String queueName ="poll-" + poll.getId() + "-queue";
        this.messagerSetup.setupMessagerPoll(poll.getId());
        consumer.receiveMessage(queueName);
    }
*/

    public User getUserById(Integer userId) {
        return this.userActions.getUserById(userId);
    }

    public HashSet<ArrayList<VoteOption>> getAllVoteOptions() {
        HashSet<ArrayList<VoteOption>> voSet = new HashSet<>();
        for (Poll p : this.pollManager.values()) {
            ArrayList<VoteOption> vopL = new ArrayList<>(p.getOptions());
            voSet.add(vopL);
        }
        return voSet;
    }

    public void createVoteOptions(Poll poll) {
        for (VoteOption vo : poll.getOptions()) {
            this.voteOptionActions.setVoteOptionId(vo, poll);
        }
        //sort the voteOption
        poll.sortVoteOptions();
    }

    public VoteOption getVoteOptionById(Integer pollId, Integer voteOptionId) {
        List<VoteOption> voL = this.pollManager.get(pollId).getOptions();
        for (VoteOption vo : voL) {
            if (Objects.equals(vo.getId(), voteOptionId)) {
                return vo;
            }
        }
        return null;
    }

    public Vote createVote(Integer pollId, Vote vote) {
        //If the user who voted is the creator
        if (Objects.equals(vote.getUserId(), getPollById(pollId).getCreator().getId())) {
            return null;
        }
        //If the poll exists, then create the vote
        if (this.pollManager.containsKey(pollId)) {
            this.voteActions.setVoteId(vote);
            HashSet<Vote> voteSet = this.voteManager.get(pollId);
            voteSet.add(vote);
            //update the vote counter in redis
            String key = "poll:" + pollId;
            if (checkRedis(key)) {
                this.jedis.hincrBy(key, String.valueOf(vote.getVoteOptionId()), 1);

            }
            return vote;
        }
        return null;
        //TODO create vote messager
    }

    public HashSet<Vote> getVotesByPollId(Integer pollId) {
        return this.voteManager.get(pollId);
    }

    public Vote changeVote(Integer pollId, Integer userId, Integer newVoteOptionId) {

        HashSet<Vote> votes = getVotesByPollId(pollId);
        for (Vote v : votes) {
            if (Objects.equals(v.getUserId(), userId)) {
                if (voteOptionExists(pollId, newVoteOptionId)) {
                    Integer oldVoteOptionId = v.getVoteOptionId();
                    v.setVoteOptionId(newVoteOptionId);
                    String key = "poll:" + pollId;
                    //Only update the cache if the result exist in the cache
                    if (checkRedis(key)) {
                        //Remove the vote from the voteOption
                        this.jedis.hincrBy(key, String.valueOf(oldVoteOptionId), -1);
                        //increase the vote counter for the new voteOption
                        this.jedis.hincrBy(key, String.valueOf(newVoteOptionId), 1);
                    }
                    return v;
                } else {
                    return null;
                }
            }
        }
        //TODO send message to
        return null;
    }

    private boolean voteOptionExists(Integer pollId, Integer newVoteOptionId) {
        Poll poll = getPollById(pollId);
        for (VoteOption vo : poll.getOptions()) {
            if (Objects.equals(vo.getId(), newVoteOptionId)) {
                return true;
            }
        }
        return false;
    }


    public boolean userAlreadyVoted(Integer pollId, Integer userId) {
        HashSet<Vote> votes = getVotesByPollId(pollId);
        for (Vote v : votes) {
            if (Objects.equals(v.getUserId(), userId)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Vote> getAllVotes() {
        ArrayList<Vote> voteList = new ArrayList<>();
        for (HashSet<Vote> voteSet : this.voteManager.values()) {
            voteList.addAll(voteSet);
        }
        return voteList;
    }

    public ArrayList<Vote> getVotesByUser(Integer userId) {
        ArrayList<Vote> userVotes = new ArrayList<>();
        for (HashSet<Vote> voteSet : this.voteManager.values()) {
            for (Vote vo : voteSet) {
                if (Objects.equals(vo.getUserId(), userId)) {
                    userVotes.add(vo);
                }
            }
        }
        return userVotes;
    }

    public Vote getRecentVote(ArrayList<Vote> votes) {
        if (votes.isEmpty()) {
            return null;
        } else {
            return votes.stream().max(Comparator.comparing(Vote::getPublishedAt)).orElse(null);
        }
    }

    public Poll deletePollById(Integer pollId) {
        //delete all votes from voteManager
        this.voteManager.remove(pollId);
        // delete the poll itself
        Poll deletedPoll = this.pollManager.get(pollId);
        this.pollManager.remove(pollId);
        //delete the poll in redis if it does exist
        String key = "poll:" + pollId;
        if (checkRedis(key)) {
            this.jedis.del(key);
        }
        //TODO delete queue
        return deletedPoll;
    }

    /**
     * Check if the cache contains the poll object with its results
     *
     * @param key the key to indicate which poll it is about
     * @return True if it exists in the cache
     */
    private Boolean checkRedis(String key) {
        Map<String, String> jsonCounter = this.jedis.hgetAll(key);
        return (jsonCounter != null && !jsonCounter.isEmpty());
    }

    public HashMap<VoteOption, Integer> voteCounter(Integer pollId) {
        HashSet<Vote> votes = voteManager.get(pollId);
        //Check if there are no votes in the poll.
        if (votes == null) {
            return new HashMap<VoteOption, Integer>();
        }

        String key = "poll:" + pollId;
        //Check if the cache contains the votes for the poll
        HashMap<Integer, Integer> counter = new HashMap<>();
        if (checkRedis(key)) {
            Map<String, String> jsonCounter = this.jedis.hgetAll(key);
            for (Map.Entry<String, String> entry : jsonCounter.entrySet()) {
                counter.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
            }
        } else {
            //go through all the votes for the specific poll, and count them according to voteOption
            Map<String, String> redisMap = new HashMap<>();
            for (Vote v : votes) {
                Integer optionId = v.getVoteOptionId();
                if (counter.containsKey(optionId)) {
                    counter.put(optionId, counter.get(optionId) + 1);

                } else {
                    counter.put(optionId, 1);
                }
                redisMap.put(String.valueOf(optionId), String.valueOf(counter.get(optionId)));
            }
            //Store it in the cache currently 2 min ttl
            int ttlMin = 2;
            int ttlSec = 60 * ttlMin;
            jedis.hset(key, redisMap);
            jedis.expire(key, ttlSec);
        }
        return mapToVoteOption(counter, pollId);
    }

    /**
     * Maps voteOptionId to the voteOption object, and its amount of votes
     *
     * @param counter Map consisting of voteOptionId and its total votes
     * @param pollId id of the poll
     * @return a Map which has VoteOption and its total votes
     */
    private HashMap<VoteOption, Integer> mapToVoteOption(HashMap<Integer, Integer> counter, Integer pollId) {
        if (counter == null || counter.isEmpty()) {
            return new HashMap<VoteOption, Integer>();
        }
        HashMap<VoteOption, Integer> result = new HashMap<>();
        List<VoteOption> voteOptionList = this.pollManager.get(pollId).getOptions();
        for (VoteOption vo : voteOptionList) {
            Integer counts = counter.get(vo.getId());
            //If the VoteOption doesn't have any votes, we can't have it be displayed null
            if (counts == null) {
                counts = 0;
            }
            result.put(vo, counts);
        }
        return result;

    }
}
