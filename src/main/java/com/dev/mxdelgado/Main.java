package com.dev.mxdelgado;

import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;

public class Main {
    private static Set<String> peliculasVistas = new HashSet<>();
    private static Map<String, Double> ratingsPersonales = new HashMap<>();
    public static void main(String[] args) {
        var logger = LoggerFactory.getLogger(Main.class.getName());

        logger.info("Iniciando el programa");
        var recomendation = new RecommendationSystem();
        recomendation.loadMovies(getMovies());
        logger.info("Cargando {} peliculas en el sistema", getMovies().size());

        try (var scanner = new Scanner(System.in)) {
            var exit = false;
            while (!exit) {
                System.out.println("\n" + """
                        =====================================
                        |    SISTEMA DE RECOMENDACION       |
                        =====================================
                        1. Ver todas las peliculas por genero
                        2. Calcular el total de votos por genero
                        3. Recomendar peliculas
                        4. Gestionar mi perfil de usuario
                        0. Salir
                        """);
                
                var option = getUserOption(scanner, "Ingrese la opción: ", 0, 4);

                switch (option) {
                    case 0:
                        exit = true;
                        break;
                    case 1:
                        logger.info("\nMostrando peliculas por genero");
                        showMoviesByGenre(scanner, recomendation);
                        break;
                    case 2:
                        logger.info("\nCalculando total de votos por genero");
                        showTotalVotesByGenre(scanner, recomendation);
                        break;
                    case 3:
                        logger.info("\nIniciando la recomendacion de peliculas");
                        showRecommendation(scanner, recomendation);
                        break;
                    case 4:
                        logger.info("\nIniciando gestion de usuario");
                        showUserManagement(scanner, recomendation);
                        break;

                    default:
                        System.err.println("\nOpción no válida");
                        waitForEnter(scanner);
                        logger.warn("Opción no válida: {}", option);
                        break;
                }

            }
            System.out.println("\nGracias por usar el sistema de recomendación de peliculas, hasta pronto!");
            logger.debug("Saliendo del programa");
        }
    }

    private static void showRecommendation(Scanner scanner, RecommendationSystem recomendation) {
        System.out.println("\n" + """
                -------------------------------------------
                |          Recomendar Peliculas           |
                -------------------------------------------
                """);

            var genre = selectGenre(scanner, recomendation);

            System.out.printf("\nLas peliculas recomendadas del genero %s son: %n", genre);

            var recommendations = recomendation.getRecommendationsByGenre(genre, peliculasVistas);
            
            var logger = LoggerFactory.getLogger(Main.class.getName());
            logger.info("Se generaron {} recomendaciones para el genero {}", recommendations.size(), genre);
            
            waitForEnter(scanner);


    }

    private static void showTotalVotesByGenre(Scanner scanner, RecommendationSystem recomendation) {
        System.out.println("\n" + """
                -------------------------------------------
                |      Total de Votos por Género          |
                -------------------------------------------
                """);

        var totalVotesByGenre = recomendation.getTotalVotesByGenre();
        
        totalVotesByGenre.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> System.out.printf("%s: %,d votos%n", 
                entry.getKey(), entry.getValue()));
        
        var logger = LoggerFactory.getLogger(Main.class.getName());
        logger.info("Se calcularon votos para {} generos", totalVotesByGenre.size());
        
        waitForEnter(scanner);
    }

    private static void showUserManagement(Scanner scanner, RecommendationSystem recomendation) {
        var exit = false;
        while (!exit) {
            System.out.println("\n" + """
                    -------------------------------------------
                    |        GESTIÓN DE USUARIO               |
                    -------------------------------------------
                    1. Ver película (marcar como vista)
                    2. Ver mi historial de películas
                    3. Puntuar película
                    4. Volver al menú principal
                    """);
            
            var option = getUserOption(scanner, "Ingrese la opción: ", 1, 4);
            
            switch (option) {
                case 1:
                    var logger = LoggerFactory.getLogger(Main.class.getName());
                    logger.info("\nMarcando película como vista");
                    markMovieAsWatched(scanner, recomendation);
                    break;
                case 2:
                    logger = LoggerFactory.getLogger(Main.class.getName());
                    logger.info("\nMostrando historial de películas vistas");
                    showWatchedMovies(scanner);
                    break;
                case 3:
                    logger = LoggerFactory.getLogger(Main.class.getName());
                    logger.info("\nIniciando puntuación de película");
                    rateMovie(scanner, recomendation);
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.err.println("\nOpción no válida");
                    waitForEnter(scanner);
                    break;
            }
        }
    }

    private static void markMovieAsWatched(Scanner scanner, RecommendationSystem recomendation) {
        System.out.println("\n" + """
                -------------------------------------------
                |        MARCAR PELÍCULA COMO VISTA       |
                -------------------------------------------
                ¿Cómo quieres buscar la película?
                1. Buscar por género
                2. Buscar por nombre
                3. Volver
                """);
        
        var option = getUserOption(scanner, "Ingrese la opción: ", 1, 3);
        
        switch (option) {
            case 1:
                // Búsqueda por género (reutilizar código existente)
                var genre = selectGenre(scanner, recomendation);
                var moviesByGenre = recomendation.getMoviesByGenre(genre);
                
                System.out.printf("\nPelículas del género %s:\n\n", genre);
                for (int i = 0; i < moviesByGenre.size(); i++) {
                    System.out.println((i + 1) + ". " + moviesByGenre.get(i));
                }
                
                var movieIndex = getUserOption(scanner, "\nSeleccione la película a marcar como vista (0 para volver): ", 0, moviesByGenre.size());
                if (movieIndex == 0) {
                    return; // Volver al submenú de gestión de usuario
                }
                var selectedMovie = moviesByGenre.get(movieIndex - 1);
                
                // Validar que no esté ya marcada como vista
                try {
                    validateMovieOperation(selectedMovie.getTitle(), "watch");
                    
                    peliculasVistas.add(selectedMovie.getTitle());
                    System.out.printf("\n[EXITO] Película '%s' marcada como vista exitosamente!\n", selectedMovie.getTitle());
                    
                } catch (DuplicateOperationException e) {
                    System.out.println("\n[INFO] " + e.getMessage());
                }
                break;
                
            case 2:
                // Búsqueda por nombre
                try {
                    String searchTerm = validateSearchInput(scanner, "\nIngrese el nombre de la película (o parte del nombre): ");
                    
                    var foundMovies = recomendation.searchMoviesByName(searchTerm);
                    
                    if (foundMovies.isEmpty()) {
                        throw new MovieNotFoundException("No se encontraron películas con el término: '" + searchTerm + "'");
                    }
                    
                    System.out.printf("\nPelículas encontradas (%d resultados):\n\n", foundMovies.size());
                    for (int i = 0; i < foundMovies.size(); i++) {
                        System.out.println((i + 1) + ". " + foundMovies.get(i));
                    }
                    
                    var movieIndexByName = getUserOption(scanner, "\nSeleccione la película a marcar como vista (0 para volver): ", 0, foundMovies.size());
                    if (movieIndexByName == 0) {
                        return; // Volver al submenú de gestión de usuario
                    }
                    var selectedMovieByName = foundMovies.get(movieIndexByName - 1);
                    
                    // Validar que no esté ya marcada como vista
                    validateMovieOperation(selectedMovieByName.getTitle(), "watch");
                    
                    peliculasVistas.add(selectedMovieByName.getTitle());
                    System.out.printf("\n[EXITO] Película '%s' marcada como vista exitosamente!\n", selectedMovieByName.getTitle());
                    
                } catch (InvalidSearchException e) {
                    System.out.println("\n[ERROR] " + e.getMessage());
                } catch (MovieNotFoundException e) {
                    System.out.println("\n[ERROR] " + e.getMessage());
                } catch (DuplicateOperationException e) {
                    System.out.println("\n[INFO] " + e.getMessage());
                }
                break;
                
            case 3:
                return;
        }
        
        waitForEnter(scanner);
    }

    private static void showWatchedMovies(Scanner scanner) {
        System.out.println("\n" + """
                -------------------------------------------
                |        HISTORIAL DE PELÍCULAS VISTAS     |
                -------------------------------------------
                """);
        
        if (peliculasVistas.isEmpty()) {
            System.out.println("[INFO] Aún no has marcado ninguna película como vista.");
            System.out.println("       Ve a la opción 4.1 para marcar películas como vistas.");
        } else {
            System.out.printf("[INFO] Has visto %d película(s):\n\n", peliculasVistas.size());
            
            var watchedList = new ArrayList<>(peliculasVistas);
            for (int i = 0; i < watchedList.size(); i++) {
                System.out.println((i + 1) + ". " + watchedList.get(i));
            }
        }
        
        waitForEnter(scanner);
    }

    private static void rateMovie(Scanner scanner, RecommendationSystem recomendation) {
        System.out.println("\n" + """
                -------------------------------------------
                |           PUNTUAR PELÍCULA              |
                -------------------------------------------
                ¿Cómo quieres buscar la película?
                1. Buscar por género
                2. Buscar por nombre
                3. Volver
                """);
        
        var option = getUserOption(scanner, "Ingrese la opción: ", 1, 3);
        
        switch (option) {
            case 1:
                // Búsqueda por género
                var genre = selectGenre(scanner, recomendation);
                var moviesByGenre = recomendation.getMoviesByGenre(genre);
                
                System.out.printf("\nPelículas del género %s:\n\n", genre);
                for (int i = 0; i < moviesByGenre.size(); i++) {
                    System.out.println((i + 1) + ". " + moviesByGenre.get(i));
                }
                
                var movieIndex = getUserOption(scanner, "\nSeleccione la película a puntuar (0 para volver): ", 0, moviesByGenre.size());
                if (movieIndex == 0) {
                    return; // Volver al submenú de gestión de usuario
                }
                var selectedMovie = moviesByGenre.get(movieIndex - 1);
                
                try {
                    validateMovieOperation(selectedMovie.getTitle(), "rate");
                    
                    System.out.print("\nIngrese su puntuación (1.0 - 5.0): ");
                    var rating = Double.parseDouble(scanner.nextLine());
                    if (rating >= 1.0 && rating <= 5.0) {
                        selectedMovie.updateRating(rating); // Actualizar datos reales de la película
                        ratingsPersonales.put(selectedMovie.getTitle(), rating); // Guardar rating personal
                        System.out.printf("\n[EXITO] Has puntuado '%s' con %.1f estrellas!\n", selectedMovie.getTitle(), rating);
                        System.out.printf("       Nuevo rating promedio: %.1f, Total votos: %d\n", selectedMovie.getRating(), selectedMovie.getVotes());
                    } else {
                        System.out.println("\n[ERROR] La puntuación debe estar entre 1.0 y 5.0.");
                    }
                } catch (DuplicateOperationException e) {
                    System.out.println("\n[INFO] " + e.getMessage());
                } catch (NumberFormatException e) {
                    System.out.println("\n[ERROR] Por favor ingrese un número válido.");
                }
                break;
                
            case 2:
                // Búsqueda por nombre
                try {
                    String searchTerm = validateSearchInput(scanner, "\nIngrese el nombre de la película (o parte del nombre): ");
                    
                    var foundMovies = recomendation.searchMoviesByName(searchTerm);
                    
                    if (foundMovies.isEmpty()) {
                        throw new MovieNotFoundException("No se encontraron películas con el término: '" + searchTerm + "'");
                    }
                    
                    System.out.printf("\nPelículas encontradas (%d resultados):\n\n", foundMovies.size());
                    for (int i = 0; i < foundMovies.size(); i++) {
                        System.out.println((i + 1) + ". " + foundMovies.get(i));
                    }
                    
                    var movieIndexByName = getUserOption(scanner, "\nSeleccione la película a puntuar (0 para volver): ", 0, foundMovies.size());
                    if (movieIndexByName == 0) {
                        return; // Volver al submenú de gestión de usuario
                    }
                    var selectedMovieByName = foundMovies.get(movieIndexByName - 1);
                    
                    try {
                        validateMovieOperation(selectedMovieByName.getTitle(), "rate");
                        
                        System.out.print("\nIngrese su puntuación (1.0 - 5.0): ");
                        var rating = Double.parseDouble(scanner.nextLine());
                        if (rating >= 1.0 && rating <= 5.0) {
                            selectedMovieByName.updateRating(rating); // Actualizar datos reales de la película
                            ratingsPersonales.put(selectedMovieByName.getTitle(), rating); // Guardar rating personal
                                                    System.out.printf("\n[EXITO] Has puntuado '%s' con %.1f estrellas!\n", selectedMovieByName.getTitle(), rating);
                        System.out.printf("       Nuevo rating promedio: %.1f, Total votos: %d\n", selectedMovieByName.getRating(), selectedMovieByName.getVotes());
                        } else {
                            System.out.println("\n[ERROR] La puntuación debe estar entre 1.0 y 5.0.");
                        }
                    } catch (DuplicateOperationException e) {
                        System.out.println("\n[INFO] " + e.getMessage());
                    } catch (NumberFormatException e) {
                        System.out.println("\n[ERROR] Por favor ingrese un número válido.");
                    }
                    
                } catch (InvalidSearchException e) {
                    System.out.println("\n[ERROR] " + e.getMessage());
                } catch (MovieNotFoundException e) {
                    System.out.println("\n[ERROR] " + e.getMessage());
                }
                break;
                
            case 3:
                return;
        }
        
        waitForEnter(scanner);
    }

    /**
     * Valida la entrada de búsqueda del usuario.
     * @param scanner El scanner para leer la entrada.
     * @param prompt El mensaje que se muestra al usuario.
     * @return La entrada validada.
     * @throws InvalidSearchException Si la entrada es inválida.
     */
    private static String validateSearchInput(Scanner scanner, String prompt) throws InvalidSearchException {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            throw new InvalidSearchException("La búsqueda no puede estar vacía.");
        }
        
        if (input.length() < 2) {
            throw new InvalidSearchException("La búsqueda debe tener al menos 2 caracteres.");
        }
        
        return input;
    }

    /**
     * Valida que una operación de película no sea duplicada.
     * @param movieTitle El título de la película.
     * @param operation El tipo de operación ("watch" o "rate").
     * @throws DuplicateOperationException Si la operación ya fue realizada.
     */
    private static void validateMovieOperation(String movieTitle, String operation) throws DuplicateOperationException {
        if (operation.equals("watch") && peliculasVistas.contains(movieTitle)) {
            throw new DuplicateOperationException("La película '" + movieTitle + "' ya está marcada como vista.");
        }
        
        if (operation.equals("rate") && ratingsPersonales.containsKey(movieTitle)) {
            throw new DuplicateOperationException("Ya has puntuado la película '" + movieTitle + "' anteriormente.");
        }
    }

    private static void showMoviesByGenre(Scanner scanner, RecommendationSystem recomendation) {
        System.out.println("\n" + """
                -------------------------------------------
                |      Listado de peliculas por genero    |
                -------------------------------------------
                """);

        var genre = selectGenre(scanner, recomendation);

        var movies = recomendation.getMoviesByGenre(genre);
        System.out.printf("\nLas peliculas del genero %s son: \n\n", genre);
        movies.forEach(System.out::println);
        
        var logger = LoggerFactory.getLogger(Main.class.getName());
        logger.info("Se encontraron {} peliculas del genero {}", movies.size(), genre);

        waitForEnter(scanner);

    }

    public static String selectGenre(Scanner scanner, RecommendationSystem recomendation) {
        var logger = LoggerFactory.getLogger(Main.class.getName());
        System.out.println("\nSeleccione el genero de su preferencia: \n");
        var genres = recomendation.getGenres();
        for (int i = 0; i < genres.size(); i++) {
            System.out.println((i + 1) + ". " + genres.get(i));
        }

        var option = getUserOption(scanner, "\nIngrese la opción: ", 0, genres.size());
        var selectedGenre = genres.get(--option);
        logger.info("Genero seleccionado: {}", selectedGenre);
        return selectedGenre;

    }

    private static void waitForEnter(Scanner scanner) {
        System.out.println("\nPresione ENTER para continuar");
        scanner.nextLine();
    }

    private static int getUserOption(Scanner scanner, String message, int min, int max) {

                while (true) {
                    try {
                        System.out.println(message);
                       var option = Integer.valueOf(scanner.nextLine());

                        if (option < min || option > max) {
                            throw new InvalidOptionException();
                        }

                        return option;

                    } catch (NumberFormatException | InvalidOptionException e) {
                        System.err.println("Opcion no valida. Intente nuevamente");
                        var logger = LoggerFactory.getLogger(Main.class.getName());
                        logger.warn("Usuario ingreso opcion invalida: {}", e.getMessage());
                    }

                }
    }

    private static Collection<Movie> getMovies() {
        return Set.of(
                new Movie("Extraction", "Acción", 4.1, 120),
                new Movie("Atomic Blonde", "Acción", 4.0, 180),
                new Movie("The Old Guard", "Acción", 3.9, 95),
                new Movie("Nobody", "Acción", 4.2, 140),
                new Movie("Rambo: Last Blood", "Acción", 3.8, 160),
                new Movie("Angel Has Fallen", "Acción", 4.0, 110),
                new Movie("6 Underground", "Acción", 3.7, 150),
                new Movie("Bloodshot", "Acción", 3.6, 130),
                new Movie("Peppermint", "Acción", 4.3, 200),
                new Movie("Hard Kill", "Acción", 3.5, 90),
                new Movie("Vacation Friends", "Comedia", 4.0, 180),
                new Movie("Game Over, Man!", "Comedia", 3.6, 95),
                new Movie("The Wrong Missy", "Comedia", 3.9, 120),
                new Movie("Murder Mystery", "Comedia", 4.2, 140),
                new Movie("Coffee & Kareem", "Comedia", 3.8, 160),
                new Movie("The Lovebirds", "Comedia", 4.1, 150),
                new Movie("Home Sweet Home Alone", "Comedia", 3.7, 100),
                new Movie("Superintelligence", "Comedia", 3.6, 110),
                new Movie("Jexi", "Comedia", 3.8, 200),
                new Movie("Bad Trip", "Comedia", 4.3, 140),
                new Movie("Pieces of a Woman", "Drama", 4.2, 170),
                new Movie("Hillbilly Elegy", "Drama", 4.1, 200),
                new Movie("The Last Letter from Your Lover", "Drama", 4.0, 140),
                new Movie("The Light Between Oceans", "Drama", 3.9, 150),
                new Movie("Penguin Bloom", "Drama", 4.1, 110),
                new Movie("The Dig", "Drama", 4.3, 190),
                new Movie("The Secret: Dare to Dream", "Drama", 3.8, 120),
                new Movie("Fatherhood", "Drama", 4.2, 200),
                new Movie("Finding You", "Drama", 3.7, 100),
                new Movie("Our Friend", "Drama", 4.0, 90),
                new Movie("Outside the Wire", "Ciencia Ficción", 4.1, 180),
                new Movie("Stowaway", "Ciencia Ficción", 4.0, 120),
                new Movie("Infinite", "Ciencia Ficción", 3.9, 150),
                new Movie("Code 8", "Ciencia Ficción", 4.2, 200),
                new Movie("Synchronic", "Ciencia Ficción", 3.8, 140),
                new Movie("Voyagers", "Ciencia Ficción", 3.7, 160),
                new Movie("Cosmic Sin", "Ciencia Ficción", 3.5, 110),
                new Movie("Archive", "Ciencia Ficción", 4.0, 100),
                new Movie("I Am Mother", "Ciencia Ficción", 4.3, 190),
                new Movie("The Midnight Sky", "Ciencia Ficción", 3.7, 130),
                new Movie("The Rental", "Terror", 4.0, 160),
                new Movie("Host", "Terror", 4.2, 110),
                new Movie("Relic", "Terror", 3.9, 95),
                new Movie("The Dark and the Wicked", "Terror", 4.1, 150),
                new Movie("Run", "Terror", 4.3, 190),
                new Movie("The Beach House", "Terror", 3.7, 100),
                new Movie("Come Play", "Terror", 4.0, 170),
                new Movie("No Escape", "Terror", 3.8, 140),
                new Movie("You Should Have Left", "Terror", 3.9, 200),
                new Movie("Amulet", "Terror", 3.6, 120));
    }
}
