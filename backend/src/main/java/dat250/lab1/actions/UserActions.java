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


    public User createUser(User user) {
        for (User u : users.values()) {
            if (Objects.equals(u.getEmail(), user.getEmail()) || Objects.equals(u.getUsername(), user.getUsername())) {
                return null;
            }
        }
        if (user.getId() == null) {
            user.setId(userIdCounter.incrementAndGet());
            users.put(user.getId(), user);
        }
        return user;
    }

    public User getUserById(Integer id) {
        return users.get(id);
    }

}
