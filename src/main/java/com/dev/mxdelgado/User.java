package com.dev.mxdelgado;

import java.util.*;

/**
 *Representa un perfil de usuario dentro del sistema de recomendación
 *Cada usuario mantiene:
 *Un nombre único de perfil
 *Un historial de películas vistas
 *Las puntuaciones personales que ha dado a películas
 *Este diseño permite que el sistema recomiende películas evitando
 *las que el usuario ya vio y también registrar su valoración personal.
 */
public class User {

    /** Nombre del perfil del usuario. Es inmutable una vez creado. */
    private final String username;

    /**
     *Conjunto de títulos de películas que el usuario ha visto.
     *Se usa {@link Set} para evitar duplicados automáticamente.
     */
    private final Set<String> watchedTitles = new HashSet<>();

    /**
     *Mapa que guarda las puntuaciones personales del usuario.
     *Clave: título de la película  
     *Valor: puntuación dada por el usuario.
     */
    private final Map<String, Double> ratingsByTitle = new HashMap<>();

    /**
     * Constructor del perfil de usuario.
     * @param username nombre del usuario
     * @throws IllegalArgumentException si el nombre está vacío
     */
    public User(String username) {
        this.username = Objects.requireNonNull(username).trim();
        if (this.username.isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
    }

    /**
     * Obtiene el nombre del usuario.
     * @return nombre del perfil
     */
    public String getUsername() {
        return username;
    }

    /**
     *Devuelve el conjunto de películas vistas por el usuario.
     *Se retorna una vista inmutable para evitar que código externo
     *modifique directamente la colección interna.
     *@return conjunto de títulos vistos
     */
    public Set<String> getWatchedTitles() {
        return Collections.unmodifiableSet(watchedTitles);
    }

    /**
     *Devuelve las puntuaciones realizadas por el usuario.
     *También se devuelve como mapa inmutable para proteger
     *la integridad de los datos internos.
     * @return mapa título → puntuación
     */
    public Map<String, Double> getRatings() {
        return Collections.unmodifiableMap(ratingsByTitle);
    }

     /**
     * Verifica si el usuario ya vio una película.
     * @param title título de la película
     * @return true si ya está marcada como vista
     */
    public boolean hasWatched(String title) {
        return watchedTitles.contains(norm(title));
    }

    /**
     *Marca una película como vista en el historial del usuario.
     *@param title título de la película
     */
    public void watch(String title) {
        watchedTitles.add(norm(title));
    }

     /**
     *Verifica si el usuario ya puntuó una película.
     *@param title título de la película
     *@return true si ya existe una puntuación registrada
     */
    public boolean hasRated(String title) {
        return ratingsByTitle.containsKey(norm(title));
    }

    /**
     *Guarda una puntuación personal del usuario para una película.
     *Al puntuar una película también se marca automáticamente
     *como vista (porque es lógico que el usuario la haya visto).
     *@param title título de la película
     *@param rating puntuación dada por el usuario
     */
    public void rate(String title, double rating) {
        ratingsByTitle.put(norm(title), rating);
        // Al puntuar se considera que el usuario ya vio la película
        watchedTitles.add(norm(title)); 
    }

     /**
     *Normaliza texto para comparaciones consistentes.
     *Convierte el texto a minúsculas y elimina espacios extras
     *para evitar errores en comparaciones de títulos.
     *@param s texto original
     *@return texto normalizado
     */
    private static String norm(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }
}