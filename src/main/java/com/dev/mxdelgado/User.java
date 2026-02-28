package com.dev.mxdelgado;

import java.util.*;

public class User {
    private final String username;
    private final Set<String> watchedTitles = new HashSet<>();
    private final Map<String, Double> ratingsByTitle = new HashMap<>();

    public User(String username) {
        this.username = Objects.requireNonNull(username).trim();
        if (this.username.isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getWatchedTitles() {
        return Collections.unmodifiableSet(watchedTitles);
    }

    public Map<String, Double> getRatings() {
        return Collections.unmodifiableMap(ratingsByTitle);
    }

    public boolean hasWatched(String title) {
        return watchedTitles.contains(norm(title));
    }

    public void watch(String title) {
        watchedTitles.add(norm(title));
    }

    public boolean hasRated(String title) {
        return ratingsByTitle.containsKey(norm(title));
    }

    public void rate(String title, double rating) {
        ratingsByTitle.put(norm(title), rating);
        watchedTitles.add(norm(title)); // opcional: al puntuar, se considera vista
    }

    private static String norm(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }
}