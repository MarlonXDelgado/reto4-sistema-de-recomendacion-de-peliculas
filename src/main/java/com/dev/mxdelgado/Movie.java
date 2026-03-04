package com.dev.mxdelgado;

/**
 *Representa una película dentro del sistema de recomendación.
 *
 *Esta clase modela los datos básicos de una película:
 *Título
 *Género
 *Rating promedio
 *Número total de votos
 *Además, permite actualizar el rating promedio cuando un usuario
 *agrega una nueva puntuación.
 */

public class Movie {
    /** Título de la película. */
    private String title;

    /** Género de la película (Acción, Drama, Terror, etc.). */
    private String genre;

    /** Rating promedio actual de la película. */
    private Double rating;

     /** Cantidad total de votos recibidos. */
    private Integer votes;

    /**
     * Constructor de la película.
     * @param title  título de la película
     * @param genre  género al que pertenece
     * @param rating rating promedio inicial
     * @param votes  número de votos inicial
     */
    public Movie(String title, String genre, Double rating, Integer votes) {
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.votes = votes;
    }

    /** @return título de la película */
    public String getTitle() {
        return title;
    }

    /** @return género de la película */
    public String getGenre() {
        return genre;
    }

    /** @return rating promedio actual */
    public Double getRating() {
        return rating;
    }

    /** @return número total de votos */
    public Integer getVotes() {
        return votes;
    }

     /**
     * Representación en texto de la película.
     * Se usa cuando se imprime en consola (por ejemplo en los menús).
     * @return cadena formateada con título, rating y votos.
     */
    public String toString() {
        return String.format("%s \t\t\t- ratring: %.1f, votos: %d", title, rating, votes);
    }

     /**
     *Actualiza el rating promedio cuando un usuario agrega una nueva puntuación.
     *La fórmula utilizada es:
     *nuevoPromedio = (ratingActual * votosActuales + nuevaPuntuacion) / (votosActuales + 1)
     *El método es {@code synchronized} para evitar problemas si múltiples
     *hilos intentan modificar el rating al mismo tiempo.
     * @param newRating nueva puntuación ingresada por el usuario
     */
    
    public synchronized void updateRating(double newRating) {
         // Se calcula el total acumulado de puntuaciones.
        double totalRating = this.rating * this.votes + newRating;

        // Se incrementa el número de votos.
        this.votes++;

        // Se calcula el nuevo promedio.
        this.rating = totalRating / this.votes;
    }
    
}
