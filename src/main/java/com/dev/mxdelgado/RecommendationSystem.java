package com.dev.mxdelgado;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class RecommendationSystem {
    private List<Movie> movies;

    public void loadMovies(Collection<Movie> movies) {
        this.movies = new ArrayList<>(movies);
    }

    public List<String> getGenres() {
        return movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .sorted()
            .toList();
    }

    public List<Movie> getMoviesByGenre(String genre) {
        return movies.stream()
            .filter(m -> m.getGenre().equals(genre))
            .toList();
    }

    public List<Movie> getRecommendationsByGenre(String genre) {
        return movies.stream()
        .filter(m -> m.getGenre().equals(genre))
        .filter(RecommendationSystem::clasificate)
        .sorted(Comparator.comparingDouble(Movie::getRating).reversed()
                .thenComparing(Movie::getTitle))
        .toList();
    }

    private static boolean clasificate(Movie m) {
        return m.getRating() > 4.0 && m.getVotes() >= 100;
    }

    
    // Recomendaciones recibiendo un Set de títulos vistos
    public List<Movie> getRecommendationsByGenre(String genre, Set<String> watchedTitles) {
        Objects.requireNonNull(genre, "genre");
        Set<String> watched = (watchedTitles == null) ? Set.of()
            : watchedTitles.stream()
                .filter(Objects::nonNull)
                .map(s -> s.toLowerCase(Locale.ROOT).trim())
                .collect(java.util.stream.Collectors.toSet());

    return movies.stream()
            .filter(m -> m.getGenre().equalsIgnoreCase(genre))
            .filter(RecommendationSystem::clasificate)                 // rating > 4.0 & votos >= 100
            .filter(m -> !watched.contains(m.getTitle().toLowerCase(Locale.ROOT))) // excluir vistas
            .sorted(Comparator
                    .comparingDouble(Movie::getRating).reversed()
                    .thenComparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    // Recomendaciones directamente para un User
    public List<Movie> getRecommendationsForUser(String genre, User user) {
        Set<String> watched = (user == null) ? Set.of() : user.getWatchedTitles();
    return getRecommendationsByGenre(genre, watched);
}

}
