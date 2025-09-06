package dat250.lab1;

import dat250.lab1.model.Poll;
import dat250.lab1.model.User;
import dat250.lab1.model.Vote;
import dat250.lab1.model.VoteOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class pollAppTests {
    private RestClient client;

    @Autowired
    void clientBuilder(RestClient.Builder restBuild) {
        this.client = restBuild.baseUrl("http://localhost:8080").build();
    }

    @Test
    public void testScenarios() {
        //create a new user
        User user1 = new User("user1", "user1@email.com");
        ResponseEntity<String> user1Res = client.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user1).retrieve().toEntity(String.class);
        assertEquals(HttpStatus.OK, user1Res.getStatusCode());
        assertEquals(user1Res.getBody(), "Created user:" + user1.getUsername());
        //list all users
        HashSet<User> listUserRes = client.get().uri("/users").retrieve().
                toEntity(new ParameterizedTypeReference<HashSet<User>>() {
                }).getBody();
        assertNotNull(listUserRes);
        assertEquals(1, listUserRes.size());
        assertTrue(listUserRes.contains(user1));
        //create another user
        User user2 = new User("user2", "user2@email.com");

        ResponseEntity<String> user2Res = client.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user2).retrieve().toEntity(String.class);
        assertEquals(HttpStatus.OK, user2Res.getStatusCode());
        assertEquals(user2Res.getBody(), "Created user:" + user2.getUsername());

        //list all users again
        HashSet<User> listUserRes2 = client.get().uri("/users").retrieve().
                toEntity(new ParameterizedTypeReference<HashSet<User>>() {
                }).getBody();
        assertNotNull(listUserRes2);
        assertEquals(2, listUserRes2.size());
        assertTrue(listUserRes2.contains(user1));
        assertTrue(listUserRes2.contains(user2));

        //user1 creates a new poll
        Instant publishedAt = Instant.now();
        Instant validUntil = publishedAt.plus(21, ChronoUnit.DAYS);
        ArrayList<VoteOption> voteOptions = new ArrayList<>();
        voteOptions.add(new VoteOption("Sushi", 1));
        voteOptions.add(new VoteOption("Pizza", 2));
        voteOptions.add(new VoteOption("Kebab", 3));
        voteOptions.add(new VoteOption("Taco", 4));
        Poll poll = new Poll(
                "What do you want to eat?",
                validUntil,
                publishedAt,
                voteOptions
        );

        ResponseEntity<String> pollRes = client.post().uri("/polls/{creatorId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(poll).retrieve().toEntity(String.class);
        assertNotNull(pollRes);
        assertEquals(HttpStatus.OK, pollRes.getStatusCode());
        assertEquals("Created poll: " + poll.getQuestion(), pollRes.getBody());

        //list polls
        HashSet<Poll> listPolls = client.get().uri("/polls").retrieve().
                toEntity(new ParameterizedTypeReference<HashSet<Poll>>() {
                }).getBody();
        assertNotNull(listPolls);
        assertFalse(listPolls.isEmpty());
        assertEquals(1, listPolls.size());
        assertTrue(listPolls.contains(poll));

        //user2 votes for poll
        Instant voteTime = Instant.now();
        int voteOption = 2;
        int pollId = 1;
        int userId = 2;
        Vote vote = new Vote(userId, voteOption,voteTime);
        HashSet<Vote> votes = new HashSet<>();
        votes.add(vote);
        ResponseEntity<String> voteRes = client.post().uri("/polls/{pollId}/votes", pollId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(vote).retrieve().toEntity(String.class);
        assertNotNull(voteRes);
        assertEquals(HttpStatus.OK,voteRes.getStatusCode());
        assertEquals("Successfully voted for poll: " + pollId, voteRes.getBody());

        //check votecounter has gone up for the poll
        ResponseEntity<String> getVoteCounterRes = client.get().uri("votes/polls/{pollId}",pollId)
                .retrieve().toEntity(String.class);
        assertNotNull(getVoteCounterRes);
        assertEquals("Votes for pollId " + pollId + "\n" + votes.toString(), getVoteCounterRes.getBody());
        //save the current vote for user 2 to compare it later.

        ResponseEntity<String> voteResBeforeChange = client.get().
                uri("votes/recent/{userId}",userId)
                .retrieve().toEntity(String.class);
        assertNotNull(voteResBeforeChange);
        assertEquals(HttpStatus.OK,voteResBeforeChange.getStatusCode());


        //user 2 changes vote to voteOption 4
        int newVoteOption = 4;
        Vote changedVote = new Vote(userId,newVoteOption,publishedAt);
        changedVote.setVoteId(1);
        ResponseEntity<String> changeVoteRes = client.put()
                .uri("polls/{pollId}/changes/{userId}/newVoteOptions/{voteOption}",pollId,userId,newVoteOption)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve().toEntity(String.class);
        assertNotNull(changeVoteRes);
        assertEquals(HttpStatus.OK, changeVoteRes.getStatusCode());
        assertEquals("Successfully changed vote for userId " + userId +
                " to have voteOptionId " + newVoteOption + " at the poll " + pollId, changeVoteRes.getBody());
        //list recent votes for user 2
        ResponseEntity<String> recentVoteRes = client.get().
                uri("votes/recent/{userId}",userId)
                .retrieve().toEntity(String.class);
        assertNotNull(recentVoteRes);
        assertEquals(HttpStatus.OK,recentVoteRes.getStatusCode());
        //check recent votes for user 2
        assertEquals("Recent vote for user " + userId + ": " + changedVote.toString(),recentVoteRes.getBody());
        //check that the votes actually are different, and that we actually modified the voteoptions
        assertNotEquals(voteResBeforeChange.getBody(),recentVoteRes.getBody());

        //Delete one poll
        String deletePollRes = client.delete().
                uri("users/{creatorId}/polls/{pollId}",1,1).
                retrieve().body(String.class);
        assertEquals("Deleted poll with id:" + 1, deletePollRes);

        //list votes
        ArrayList<Vote> listVotes = client.get().uri("/votes")
                .retrieve().
                toEntity(new ParameterizedTypeReference<ArrayList<Vote>>() {}).
                getBody();
        assertNotNull(listVotes);
        assertTrue(listVotes.isEmpty());
    }
}

