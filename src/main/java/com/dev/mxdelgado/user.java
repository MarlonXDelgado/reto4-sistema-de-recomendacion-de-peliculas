package com.dev.mxdelgado;

import java.util.*;
import java.util.stream.Collectors;

public class User {

     private final String username;

    private final Set<String> watchedTitles = new HashSet<>();
    private final Map<String, Double> ratingsByTitle = new HashMap<>();

    public User(String username) {
        this.username = Objects.requireNonNull(username).trim();
    }

    public String getUsername() {
        return username;
    }

    // --- Vistas ---
    public void markWatched(String title) {
        if (title == null) return;
        watchedTitles.add(title.toLowerCase(Locale.ROOT).trim());
    }

    public void markWatched(Movie movie) {
        if (movie != null) markWatched(movie.getTitle());
    }

    public boolean hasWatched(String title) {
        if (title == null) return false;
        return watchedTitles.contains(title.toLowerCase(Locale.ROOT).trim());
    }

    public Set<String> getWatchedTitles() {
        return Collections.unmodifiableSet(watchedTitles);
    }

    // --- Calificaciones ---
    public void rate(String title, double rating) {
        if (title == null) return;
        var key = title.toLowerCase(Locale.ROOT).trim();
        ratingsByTitle.put(key, rating);
        watchedTitles.add(key); // Calificar implica que ya la vio
    }

    public void rate(Movie movie, double rating) {
        if (movie != null) rate(movie.getTitle(), rating);
    }

    public Map<String, Double> getRatings() {
        return Collections.unmodifiableMap(ratingsByTitle);
    }

    public Double getRatingFor(String title) {
        if (title == null) return null;
        return ratingsByTitle.get(title.toLowerCase(Locale.ROOT).trim());
    }

    // Helpers de impresión
    public String watchedPretty() {
        if (watchedTitles.isEmpty()) return "(sin vistas)";
        return watchedTitles.stream().sorted().collect(Collectors.joining(", "));
    }

    public String ratingsPretty() {
        if (ratingsByTitle.isEmpty()) return "(sin calificaciones)";
        return ratingsByTitle.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> "%s: %.1f".formatted(e.getKey(), e.getValue()))
                .collect(Collectors.joining(", "));
    }


    
}
