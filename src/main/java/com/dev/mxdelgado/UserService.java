package com.dev.mxdelgado;

import java.util.*;

public class UserService {
    private final Map<String, User> users = new LinkedHashMap<>();

    public User getOrCreate(String username) {
        String key = norm(username);
        return users.computeIfAbsent(key, k -> new User(username));
    }

    public User find(String username) {
        return users.get(norm(username));
    }

    public List<User> listUsers() {
        return new ArrayList<>(users.values());
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }

    private static String norm(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }
}