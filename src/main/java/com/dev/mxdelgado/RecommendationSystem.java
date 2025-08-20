package com.dev.mxdelgado;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

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

}
