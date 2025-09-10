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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PollAppTests {
    private RestClient client;

    @Autowired
    void clientBuilder(RestClient.Builder restBuild) {
        this.client = restBuild.baseUrl("http://localhost:8080").build();
    }

    @Test
    public void testScenarios() {
        //create a new user
        User user1 = new User("user1", "user1@email.com");
        ResponseEntity<User> user1Res = client.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user1).retrieve().toEntity(User.class);
        assertNotNull(user1Res);
        assertEquals(HttpStatus.OK, user1Res.getStatusCode());
        assertEquals(user1.getUsername(), user1Res.getBody().getUsername());
        assertEquals(user1.getEmail(), user1Res.getBody().getEmail());

        //list all users
        HashSet<User> listUserRes = client.get().uri("/users").retrieve().
                toEntity(new ParameterizedTypeReference<HashSet<User>>() {
                }).getBody();
        assertNotNull(listUserRes);
        assertEquals(1, listUserRes.size());
        assertTrue(listUserRes.contains(user1));

        //create another user
        User user2 = new User("user2", "user2@email.com");

        ResponseEntity<User> user2Res = client.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user2).retrieve().toEntity(User.class);
        assertNotNull(user2Res);
        assertEquals(HttpStatus.OK, user2Res.getStatusCode());
        assertEquals(user2.getUsername(), user2Res.getBody().getUsername());
        assertEquals(user2.getEmail(), user2Res.getBody().getEmail());

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
        ResponseEntity<Poll> pollRes = client.post().uri("/polls/{creatorId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(poll).retrieve().toEntity(Poll.class);
        assertNotNull(pollRes);
        assertEquals(HttpStatus.OK, pollRes.getStatusCode());
        assertEquals(poll, pollRes.getBody());
        assertEquals(poll.getQuestion(), pollRes.getBody().getQuestion());
        assertEquals(poll.getCreator(), pollRes.getBody().getCreator());
        assertEquals(validUntil, pollRes.getBody().getValidUntil());
        assertEquals(publishedAt, pollRes.getBody().getPublishedAt());

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
        Vote vote = new Vote(userId, voteOption, voteTime);
        ResponseEntity<Vote> voteRes = client.post().uri("/polls/{pollId}/votes", pollId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(vote).retrieve().toEntity(Vote.class);
        assertNotNull(voteRes);
        assertEquals(HttpStatus.OK, voteRes.getStatusCode());
        assertEquals(vote.getVoteOptionId(), voteRes.getBody().getVoteOptionId());
        assertEquals(userId, voteRes.getBody().getUserId());
        assertEquals(voteTime, voteRes.getBody().getPublishedAt());


        //check votecounter has gone up for the poll
        ResponseEntity<HashSet<Vote>> getVoteCounterRes = client.get().uri("votes/polls/{pollId}", pollId)
                .retrieve().toEntity(new ParameterizedTypeReference<HashSet<Vote>>() {
                });
        assertNotNull(getVoteCounterRes);
        assertEquals(HttpStatus.OK, voteRes.getStatusCode());
        assertEquals(1, getVoteCounterRes.getBody().size());
        for (Vote v : getVoteCounterRes.getBody()) {
            assertEquals(vote.getVoteOptionId(), v.getVoteOptionId());
            assertEquals(userId, v.getUserId());
            assertEquals(voteTime, v.getPublishedAt());
        }
        //save the current vote for user 2 to compare it later.
        ResponseEntity<Vote> voteResBeforeChange = client.get().
                uri("votes/recent/{userId}", userId)
                .retrieve().toEntity(Vote.class);
        assertNotNull(voteResBeforeChange);
        assertEquals(HttpStatus.OK, voteResBeforeChange.getStatusCode());

        //user 2 changes vote to voteOption 4
        int newVoteOption = 4;
        ResponseEntity<Vote> changeVoteRes = client.put()
                .uri("polls/{pollId}/changes/{userId}/newVoteOptions/{voteOption}", pollId, userId, newVoteOption)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve().toEntity(Vote.class);
        assertNotNull(changeVoteRes);
        assertEquals(HttpStatus.OK, changeVoteRes.getStatusCode());
        assertEquals(newVoteOption, changeVoteRes.getBody().getVoteOptionId());
        assertNotEquals(vote, changeVoteRes.getBody());

        //list recent votes for user 2
        ResponseEntity<Vote> recentVoteRes = client.get().
                uri("votes/recent/{userId}", userId)
                .retrieve().toEntity(Vote.class);
        assertNotNull(recentVoteRes);
        assertEquals(HttpStatus.OK, recentVoteRes.getStatusCode());
        assertEquals(newVoteOption, recentVoteRes.getBody().getVoteOptionId());
        assertEquals(userId, recentVoteRes.getBody().getUserId());
        assertEquals(voteTime, recentVoteRes.getBody().getPublishedAt());

        //Delete one poll
        Poll deletePollRes = client.delete().
                uri("users/{creatorId}/polls/{pollId}", 1, pollId).
                retrieve().body(Poll.class);
        assertEquals(poll.getQuestion(), deletePollRes.getQuestion());
        assertEquals(poll.getCreator(), deletePollRes.getCreator());
        assertEquals(publishedAt, deletePollRes.getPublishedAt());
        assertEquals(validUntil, deletePollRes.getValidUntil());

        //check that the list of polls are empty
        HashSet<Poll> emptyListPolls = client.get().uri("/polls").retrieve().
                toEntity(new ParameterizedTypeReference<HashSet<Poll>>() {
                }).getBody();
        assertNotNull(emptyListPolls);
        assertTrue(emptyListPolls.isEmpty());

        //check that the list of voteOptions is empty
        HashSet<ArrayList<VoteOption>> emptyVoList = client.get().uri("/voteOptions").retrieve().
                toEntity(new ParameterizedTypeReference<HashSet<ArrayList<VoteOption>>>() {
                }).getBody();
        assertNotNull(emptyVoList);
        assertTrue(emptyVoList.isEmpty());

        //check that the list of votes is empty
        ArrayList<Vote> emptyVoteList = client.get().uri("/votes").retrieve().
                toEntity(new ParameterizedTypeReference<ArrayList<Vote>>() {
                }).getBody();
        assertNotNull(emptyVoteList);
        assertTrue(emptyVoteList.isEmpty());
        //check that the list of results for that poll would give a bad request
        // have to wrap it as a try catch, if not the test would give error
        try {
            client.get().uri("votes/polls/{pollId}", pollId).retrieve().toBodilessEntity();
        } catch (HttpClientErrorException error) {
            assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        }


    }

}
