package com.dev.mxdelgado;

public class Movie {
    private String title;
    private String genre;
    private Double rating;
    private Integer votes;

    public Movie(String title, String genre, Double rating, Integer votes) {
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.votes = votes;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public Double getRating() {
        return rating;
    }

    public Integer getVotes() {
        return votes;
    }

    
    
}
