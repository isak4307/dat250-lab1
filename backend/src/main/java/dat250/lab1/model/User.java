package dat250.lab1.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable {
    private String username;
    private String email;
    private int id = 0;


    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User user)) return false;
        return Objects.equals(username, user.username) && Objects.equals(email, user.email);
    }
    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }
}
