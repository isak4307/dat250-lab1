package dat250.lab1.actions;

import dat250.lab1.model.User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UserActions {
    private final AtomicInteger userIdCounter = new AtomicInteger(0);
    private Map<Integer, User> users = new HashMap<>();

    public UserActions() {
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    /**
     * Creates user if the user doesn't exist.
     * This means, the username or email isn't the same as some other user
     *
     * @param user
     * @return true if it was able to register the user. False if it was unable
     */
    public boolean createUser(User user) {
        for (User u : users.values()) {
            if (Objects.equals(u.getEmail(), user.getEmail()) || Objects.equals(u.getUsername(), user.getUsername())) {
                return false;
            }
        }
        if (user.getUserId() == 0) {
            user.setUserId(userIdCounter.incrementAndGet());
            users.put(user.getUserId(), user);
        }
        return true;
    }

    /**
     * @param id
     * @return
     */
    public User getUserById(int id) {
        return users.get(id);
    }

}
