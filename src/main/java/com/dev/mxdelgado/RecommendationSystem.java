package com.dev.mxdelgado;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *Motor principal del sistema de recomendación.
 
 *Responsabilidades:
 *Cargar y mantener en memoria el catálogo de películas.
 *Listar géneros disponibles y filtrar películas por género.
 *Generar recomendaciones según reglas (rating, votos y películas ya vistas).
 *Calcular métricas agregadas (total de votos por género) usando streams/parallelStream.
 *Buscar películas por coincidencia parcial de nombre.
 */
public class RecommendationSystem {
    /**
     *Normaliza texto para comparaciones consistentes:
     *trim(): elimina espacios al inicio/final
     *toLowerCase(): ignora mayúsculas/minúsculas
     *Se usa para comparar géneros y títulos sin que fallen por diferencias de formato.
     * @param s texto original
     * @return texto normalizado, o cadena vacía si {@code s} es null
     */

    private static String norm(String s) {
    return s == null ? "" : s.trim().toLowerCase(java.util.Locale.ROOT);
}

    /** Lista de películas cargadas en memoria (catálogo). */
    private List<Movie> movies;

    /**
     *Carga (o reemplaza) el catálogo de películas en memoria.
     *Se copia a una nueva lista para evitar depender de la colección externa
     *(inmutabilidad defensiva).
     * @param movies colección de películas a cargar
     */
    public synchronized void loadMovies(Collection<Movie> movies) {
        this.movies = new ArrayList<>(movies);
    }

    /**
     *Obtiene la lista de géneros disponibles en el catálogo.
     *Procesos:
     *Mapea cada película a su género
     *Elimina duplicados (distinct)
     *Ordena alfabéticamente
     * @return lista de géneros únicos ordenados
     */

    public synchronized List<String> getGenres() {
        return movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .sorted()
            .toList();
    }

    /**
     *Filtra películas por género (comparación exacta del String recibido).
     *En este método la comparación es directa {@code equals}. En otros puntos
     *(como recomendaciones) se usa normalización para tolerar mayúsculas/espacios.
     * @param genre género a filtrar
     * @return películas que pertenecen a ese género
     */
    public synchronized List<Movie> getMoviesByGenre(String genre) {
        return movies.stream()
            .filter(m -> m.getGenre().equals(genre))
            .toList();
    }

    /**
     *Genera recomendaciones para un género dado, excluyendo películas ya vistas.
     *
     *Reglas de recomendación aplicadas:
     *Solo películas del género seleccionado (comparación normalizada).
     *Solo películas que cumplan: rating > 4.0 y votos >= 100.
     *No recomendar títulos presentes en el historial de vistas del usuario.
     *Ordenar por rating descendente, y si empatan, por título (sin distinguir mayúsculas).
     * @param genre género seleccionado por el usuario
     * @param watchedTitles conjunto de títulos ya vistos (del perfil activo); puede ser null
     * @return lista de películas recomendadas ordenadas
     */

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
         // 1) Filtrar por género (normalizado)
        .filter(m -> norm(m.getGenre()).equals(genreNorm))

        // 2) Filtrar por criterios de calidad  
        .filter(RecommendationSystem::clasificate)  
          
        // 3) Excluir películas ya vistas por el usuario         
        .filter(m -> !watchedNorm.contains(norm(m.getTitle())))

        // 4) Ordenamiento: rating (desc) y luego título (alfabético)
        .sorted(Comparator.comparingDouble(Movie::getRating).reversed()
                .thenComparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER))
        .toList();
}

    /**
     * Regla de clasificación (filtro de “calidad”) para recomendar películas.
     *La condición es:
     *rating > 4.0
     *votos >= 100
     * @param m película evaluada
     * @return true si cumple los criterios para ser recomendada
     */

    private static boolean clasificate(Movie m) {
        return m.getRating() > 4.0 && m.getVotes() >= 100;
    }

     /**
     * Calcula el total de votos acumulados por género.
     *Usa {@code parallelStream()} para demostrar paralelismo.
     *La agregación se realiza con:
     *{@code groupingBy(Movie::getGenre)}
     *{@code summingInt(Movie::getVotes)}
     * @return mapa: género → suma total de votos de todas sus películas
     */

    public synchronized Map<String, Integer> getTotalVotesByGenre() {
        return movies.parallelStream()
            .collect(Collectors.groupingBy(
                Movie::getGenre,
                Collectors.summingInt(Movie::getVotes)
            ));
    }

    /**
     * Busca películas por coincidencia parcial de título (case-insensitive).
     *Ejemplo: si el usuario busca "ring", podría encontrar "The Lord of the Rings".
     * @param searchTerm término de búsqueda ingresado por el usuario
     * @return lista de películas cuyo título contiene el término
     */

    public synchronized List<Movie> searchMoviesByName(String searchTerm) {
        return movies.stream()
            .filter(m -> m.getTitle().toLowerCase().contains(searchTerm.toLowerCase()))
            .toList();
    }

}
