package com.dev.mxdelgado;

import java.util.ArrayList;
import java.util.Collection;
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

}
