package com.dev.mxdelgado;

import java.util.*;

public class UserService {

    private final Map<String, User> users = new HashMap<>();

    /** Crea o devuelve el usuario si ya existía. */
    public User getOrCreate(String username) {
        return users.computeIfAbsent(Objects.requireNonNull(username).trim(), User::new);
    }

    public Optional<User> find(String username) {
        return Optional.ofNullable(users.get(username.trim()));
    }

    public Collection<User> all() {
        return Collections.unmodifiableCollection(users.values());
    }
}
    

