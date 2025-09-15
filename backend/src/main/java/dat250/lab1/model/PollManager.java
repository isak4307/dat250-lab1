package dat250.lab1.model;

import dat250.lab1.actions.PollActions;
import dat250.lab1.actions.UserActions;
import dat250.lab1.actions.VoteActions;
import dat250.lab1.actions.VoteOptionActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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

    public PollManager(@Autowired UserActions userActions, @Autowired PollActions pollActions, @Autowired VoteOptionActions voteOptionActions, @Autowired VoteActions voteActions) {
        this.userActions = userActions;
        this.pollActions = pollActions;
        this.voteOptionActions = voteOptionActions;
        this.voteActions = voteActions;
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
        return poll;
    }


    public User getUserById(Integer userId) {
        return this.userActions.getUserById(userId);
    }

    public HashSet<ArrayList<VoteOption>> getAllVoteOptions() {
        HashSet<ArrayList<VoteOption>> voSet = new HashSet<>();
        for (Poll p : this.pollManager.values()) {
            ArrayList<VoteOption> vopL = p.getVoteOptions();
            voSet.add(vopL);
        }
        return voSet;
    }

    public void createVoteOptions(Poll poll) {
        for (VoteOption vo : poll.getVoteOptions()) {
            this.voteOptionActions.setVoteOptionId(vo, poll);
        }
        //sort the voteOption
        poll.sortVoteOptions();
    }

    public VoteOption getVoteOptionById(Integer pollId, Integer voteOptionId) {
        ArrayList<VoteOption> voL = this.pollManager.get(pollId).getVoteOptions();
        for (VoteOption vo : voL) {
            if (vo.getId() == voteOptionId) {
                return vo;
            }
        }
        return null;
    }

    public Vote createVote(Integer pollId, Vote vote) {
        this.voteActions.setVoteId(vote);
        if (vote.getUserId() == getPollById(pollId).getCreator().getId()) {
            return null;
        }
        //If the poll exists
        if (this.pollManager.containsKey(pollId)) {
            HashSet<Vote> voteSet = this.voteManager.get(pollId);
            voteSet.add(vote);
            return vote;
        }
        return null;
    }

    public HashSet<Vote> getVotesByPollId(Integer pollId) {
        return this.voteManager.get(pollId);
    }

    public Vote changeVote(Integer pollId, Integer userId, Integer newVoteOptionId) {

        HashSet<Vote> votes = getVotesByPollId(pollId);
        for (Vote v : votes) {
            if (v.getUserId() == userId) {
                if (voteOptionExists(pollId, newVoteOptionId)) {
                    v.setVoteOptionId(newVoteOptionId);
                    return v;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private boolean voteOptionExists(Integer pollId, Integer newVoteOptionId) {
        Poll poll = getPollById(pollId);
        for (VoteOption vo : poll.getVoteOptions()) {
            if (vo.getId() == newVoteOptionId) {
                return true;
            }
        }
        return false;
    }


    public boolean userAlreadyVoted(Integer pollId, Integer userId) {
        HashSet<Vote> votes = getVotesByPollId(pollId);
        for (Vote v : votes) {
            if (v.getUserId() == userId) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Vote> getAllVotes() {
        ArrayList<Vote> voteList = new ArrayList<>();
        for (HashSet<Vote> voteSet : this.voteManager.values()) {
            for (Vote vo : voteSet) {
                voteList.add(vo);
            }
        }
        return voteList;
    }

    public ArrayList<Vote> getVotesByUser(Integer userId) {
        ArrayList<Vote> userVotes = new ArrayList<>();
        for (HashSet<Vote> voteSet : this.voteManager.values()) {
            for (Vote vo : voteSet) {
                if (vo.getUserId() == userId) {
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
        return deletedPoll;
    }

    public HashMap<VoteOption, Integer> voteCounter(Integer pollId) {
        HashMap<VoteOption, Integer> counter = new HashMap<VoteOption, Integer>();
        HashSet<Vote> votes = voteManager.get(pollId);
        if (votes == null) {
            return counter;
        }
        for (Vote v : votes) {
            Integer optionId = v.getVoteOptionId();
            VoteOption vo = getVoteOptionById(pollId, optionId);
            if (counter.containsKey(vo)) {
                counter.put(vo, counter.get(vo) + 1);
            } else {
                counter.put(vo, 1);
            }
        }
        return counter;

    }
}
