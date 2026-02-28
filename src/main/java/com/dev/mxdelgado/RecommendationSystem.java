package com.dev.mxdelgado;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecommendationSystem {

    private static String norm(String s) {
    return s == null ? "" : s.trim().toLowerCase(java.util.Locale.ROOT);
}

    private List<Movie> movies;

    public synchronized void loadMovies(Collection<Movie> movies) {
        this.movies = new ArrayList<>(movies);
    }

    public synchronized List<String> getGenres() {
        return movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .sorted()
            .toList();
    }

    public synchronized List<Movie> getMoviesByGenre(String genre) {
        return movies.stream()
            .filter(m -> m.getGenre().equals(genre))
            .toList();
    }

  public synchronized List<Movie> getRecommendationsByGenre(String genre, java.util.Set<String> watchedTitles) {

    // Normalizamos el genero para comparar sin errores por mayúsculas/espacios
    String genreNorm = norm(genre);

    // Normalizamos las vistas (por si vienen con mayúsculas o espacios)
    java.util.Set<String> watchedNorm = (watchedTitles == null)
            ? java.util.Set.of()
            : watchedTitles.stream()
                .map(RecommendationSystem::norm)
                .collect(java.util.stream.Collectors.toSet());

    return movies.stream()
        .filter(m -> norm(m.getGenre()).equals(genreNorm))     // género normalizado
        .filter(RecommendationSystem::clasificate)             // rating > 4.0 y votos >= 100
        .filter(m -> !watchedNorm.contains(norm(m.getTitle())))// evitar vistas
        .sorted(Comparator.comparingDouble(Movie::getRating).reversed()
                .thenComparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER))
        .toList();
}

    private static boolean clasificate(Movie m) {
        return m.getRating() > 4.0 && m.getVotes() >= 100;
    }

    public synchronized Map<String, Integer> getTotalVotesByGenre() {
        return movies.parallelStream()
            .collect(Collectors.groupingBy(
                Movie::getGenre,
                Collectors.summingInt(Movie::getVotes)
            ));
    }

    public synchronized List<Movie> searchMoviesByName(String searchTerm) {
        return movies.stream()
            .filter(m -> m.getTitle().toLowerCase().contains(searchTerm.toLowerCase()))
            .toList();
    }

}
