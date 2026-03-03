package com.dev.mxdelgado;

import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;

/**
 * Punto de entrada del programa.
 *
 * Este archivo se encarga de:
 * 
 *Inicializar el sistema de recomendación con un catálogo de películas
 *Gestionar el menú principal y submenús
 *Administrar perfiles de usuario tipo Netflix (crear/seleccionar/cambiar)
 *Delegar la lógica de recomendación y consultas a {@link RecommendationSystem}
 *Guardar el historial del usuario activo mediante {@link User}
 *los perfiles se guardan en memoria; al cerrar el programa se pierden.
 */

public class Main {

    /** Servicio en memoria que administra perfiles de usuario (crear, listar, buscar). */
    private static final UserService userService = new UserService();

    /**
     * Perfil actualmente seleccionado.
     * Todas las acciones del menú (marcar vistas, puntuar, recomendaciones)
     * afectan únicamente a este usuario.
     */
    private static User activeUser = null;

    /**
     * Método principal (main). Inicializa logger, carga películas y muestra el menú principal.
     *
     * @param args argumentos de consola (no utilizados)
     */
    public static void main(String[] args) {
        var logger = LoggerFactory.getLogger(Main.class.getName());

        logger.info("Iniciando el programa");
        // Sistema principal que contiene el catálogo y lógica de streams/paralelismo.
        var recomendation = new RecommendationSystem();
        // Carga inicial del catálogo de películas .
        recomendation.loadMovies(getMovies());
        logger.info("Cargando {} peliculas en el sistema", getMovies().size());

        // Scanner se cierra automáticamente con try-with-resources.
        try (var scanner = new Scanner(System.in)) {

            // Obligamos a tener un perfil activo antes de entrar al menú principal.
            activeUser = ensureActiveUser(scanner);

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
                        5. Cambiar perfil
                        0. Salir
                        """);

                System.out.println("Perfil activo: " + activeUser.getUsername());
                
                var option = getUserOption(scanner, "Ingrese la opción: ", 0, 5);

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

                    case 5:
                    // Cambiar perfil: reiniciamos activeUser y volvemos a pedir selección/creación.
                        activeUser = null;
                        activeUser = ensureActiveUser(scanner);
                        break;
                    
                    // Realmente no debería llegar aquí por getUserOption, pero se deja por seguridad.
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

    /**
     * Muestra recomendaciones para el usuario activo, basadas en un género seleccionado.
     *
     *La recomendación se filtra por:
     *Género seleccionado
     *Rating > 4.0 y votos >= 100 (reglas del reto)
     *No recomendar películas ya vistas por el usuario activo
     * @param scanner scanner para leer entrada del usuario
     * @param recomendation sistema de recomendación con el catálogo cargado
     */

    private static void showRecommendation(Scanner scanner, RecommendationSystem recomendation) {
        System.out.println("\n" + """
                -------------------------------------------
                |          Recomendar Peliculas           |
                -------------------------------------------
                """);

            // Selección del género.
            var genre = selectGenre(scanner, recomendation);
            if (genre == null) return;

            System.out.printf("\nLas peliculas recomendadas del genero %s son: %n", genre);

            // Recomendaciones excluyendo películas ya vistas por el usuario activo.
            var recommendations = recomendation.getRecommendationsByGenre(genre, activeUser.getWatchedTitles());
            if (recommendations.isEmpty()) {
                System.out.println("No hay recomendaciones con los filtros actuales (rating > 4.0 y votos >= 100) o ya las viste.");
            } else {
                recommendations.forEach(System.out::println);
            }

            
            var logger = LoggerFactory.getLogger(Main.class.getName());
            logger.info("Se generaron {} recomendaciones para el genero {}", recommendations.size(), genre);
            
            waitForEnter(scanner);


    }

    /**
     *Muestra el total de votos acumulados por género.
     * @param scanner scanner para pausar con ENTER
     * @param recomendation sistema de recomendación
     */

    private static void showTotalVotesByGenre(Scanner scanner, RecommendationSystem recomendation) {
        System.out.println("\n" + """
                -------------------------------------------
                |      Total de Votos por Género          |
                -------------------------------------------
                """);

        // Se calcula en RecommendationSystem con parallelStream + groupingBy + summingInt.
        var totalVotesByGenre = recomendation.getTotalVotesByGenre();

        // Se ordena por nombre de género para una salida más legible.
        totalVotesByGenre.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> System.out.printf("%s: %,d votos%n", 
                entry.getKey(), entry.getValue()));
        
        var logger = LoggerFactory.getLogger(Main.class.getName());
        logger.info("Se calcularon votos para {} generos", totalVotesByGenre.size());
        
        waitForEnter(scanner);
    }

     /**
     *Submenú de gestión de usuario (perfil activo):
     *Marcar película como vista
     *Ver historial de películas vistas
     *Puntuar película
     * @param scanner scanner para interacción
     * @param recomendation sistema de recomendación
     */

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

    /**
     *Permite marcar una película como vista en el historial del usuario activo.
     *El usuario puede buscar la película por género o por nombre.
     *Se valida que la película no haya sido marcada como vista previamente.
     * @param scanner scanner para leer opciones del usuario
     * @param recomendation sistema de recomendación
     */

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
                // Búsqueda por género 
                var genre = selectGenre(scanner, recomendation);
                if (genre == null) return;
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

                    // Guarda la película en el historial del usuario activo.
                    activeUser.watch(selectedMovie.getTitle());
                    System.out.printf("\n[EXITO] Película '%s' marcada como vista exitosamente!\n", selectedMovie.getTitle());
                    
                } catch (DuplicateOperationException e) {
                    System.out.println("\n[INFO] " + e.getMessage());
                }
                break;
                
            case 2:
                // Búsqueda por nombre (incluye validación de entrada y manejo de no encontrado).
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
                    
                    activeUser.watch(selectedMovieByName.getTitle());
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

    /**
     * Muestra el historial de películas vistas del usuario activo.
     * @param scanner scanner para pausar con ENTER
     */
    private static void showWatchedMovies(Scanner scanner) {
        System.out.println("\n" + """
                -------------------------------------------
                |        HISTORIAL DE PELÍCULAS VISTAS     |
                -------------------------------------------
                """);

        // Historial depende del perfil activo.
        var watched = activeUser.getWatchedTitles();
        
        if (watched.isEmpty()) {
            System.out.println("[INFO] Aún no has marcado ninguna película como vista.");
            System.out.println("       Ve a la opción 4.1 para marcar películas como vistas.");
        } else {
            System.out.printf("[INFO] Has visto %d película(s):\n\n", watched.size());

            // Se convierte a lista para imprimir con índices (1,2,3...)
            var watchedList = new ArrayList<>(watched);
            for (int i = 0; i < watchedList.size(); i++) {
                System.out.println((i + 1) + ". " + watchedList.get(i));
            }
        }
        
        waitForEnter(scanner);
    }


    /**
     *Permite puntuar una película y actualizar:
     *Rating promedio de la película (updateRating)
     *Rating personal del usuario activo (activeUser.rate)
     * @param scanner scanner para entrada
     * @param recomendation sistema de recomendación
     */

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
                if (genre == null) return;
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
                        selectedMovie.updateRating(rating); // Actualiza el promedio y votos de la película.
                        activeUser.rate(selectedMovie.getTitle(), rating); // Guardar rating personal
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
                            activeUser.rate(selectedMovieByName.getTitle(), rating); // Guardar rating personal
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
        if (operation.equals("watch") && activeUser.hasWatched(movieTitle)) {
            throw new DuplicateOperationException("La película '" + movieTitle + "' ya está marcada como vista.");
        }
        
        if (operation.equals("rate") && activeUser.hasRated(movieTitle)) {
            throw new DuplicateOperationException("Ya has puntuado la película '" + movieTitle + "' anteriormente.");
        }
    }
    
    /**
     * Muestra películas del catálogo filtradas por un género que el usuario selecciona.
     * @param scanner scanner para entrada
     * @param recomendation sistema de recomendación
     */

    private static void showMoviesByGenre(Scanner scanner, RecommendationSystem recomendation) {
        System.out.println("\n" + """
                -------------------------------------------
                |      Listado de peliculas por genero    |
                -------------------------------------------
                """);

        var genre = selectGenre(scanner, recomendation);
        if (genre == null) return;

        var movies = recomendation.getMoviesByGenre(genre);
        System.out.printf("\nLas peliculas del genero %s son: \n\n", genre);
        movies.forEach(System.out::println);
        
        var logger = LoggerFactory.getLogger(Main.class.getName());
        logger.info("Se encontraron {} peliculas del genero {}", movies.size(), genre);

        waitForEnter(scanner);

    }

    /**
     * Permite seleccionar un género de la lista de géneros disponibles.
     * Incluye opción "0. Volver".
     * @param scanner scanner para entrada
     * @param recomendation sistema de recomendación
     * @return género seleccionado o null si el usuario elige volver
     */

    public static String selectGenre(Scanner scanner, RecommendationSystem recomendation) {
    var logger = LoggerFactory.getLogger(Main.class.getName());
    System.out.println("\nSeleccione el genero de su preferencia: \n");

    var genres = recomendation.getGenres();

    System.out.println("0. Volver");
    for (int i = 0; i < genres.size(); i++) {
        System.out.println((i + 1) + ". " + genres.get(i));
    }

    var option = getUserOption(scanner, "\nIngrese la opción: ", 0, genres.size());
    if (option == 0) {
        logger.info("Usuario volvió sin seleccionar género");
        return null;
    }

    var selectedGenre = genres.get(option - 1);
    logger.info("Genero seleccionado: {}", selectedGenre);
    return selectedGenre;
}

    /**
     * Pausa la ejecución hasta que el usuario presione ENTER.
     * @param scanner scanner para leer el ENTER
     */

    private static void waitForEnter(Scanner scanner) {
        System.out.println("\nPresione ENTER para continuar");
        scanner.nextLine();
    }

    /**
     * Lee una opción del usuario y valida que esté dentro de un rango [min, max].
     * Si no es válida, vuelve a pedir la opción.
     * @param scanner scanner para leer entrada
     * @param message mensaje de solicitud al usuario
     * @param min mínimo permitido
     * @param max máximo permitido
     * @return opción válida en el rango indicado
     */

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

     /**
     * Asegura que exista un usuario activo antes de usar el sistema.
     * Permite crear un perfil o elegir uno existente.
     * @param scanner scanner para interacción
     * @return el usuario activo seleccionado/creado
     */

    private static User ensureActiveUser(Scanner scanner) {

    while (activeUser == null) {
        System.out.println("\n" + """
                -----------------
                |    PERFILES   |
                -----------------
                """);

        System.out.println("1. Crear perfil");
        System.out.println("2. Elegir perfil existente");
        System.out.println("0. Volver");

        int opt = getUserOption(scanner, "Ingrese la opción: ", 0, 2);

        switch (opt) {
            case 0 -> {
                // Si aún no hay perfil activo, no puede “volver” al menú principal
                // (porque el sistema necesita un perfil). Entonces solo repite el menú.
                System.out.println("[INFO] Debes seleccionar o crear un perfil para continuar.");
            }

            case 1 -> {
                System.out.print("Nombre del nuevo perfil: ");
                String name = scanner.nextLine().trim();

                if (name.isEmpty()) {
                    System.out.println("[ERROR] El nombre no puede estar vacío.");
                    break;
                }

                activeUser = userService.getOrCreate(name);
                System.out.println("[OK] Perfil activo: " + activeUser.getUsername());
            }

            case 2 -> {
                var users = userService.listUsers();

                if (users.isEmpty()) {
                    System.out.println("[INFO] No hay perfiles creados aún. Crea uno primero.");
                    break;
                }

                System.out.println("\nPerfiles disponibles:");
                for (int i = 0; i < users.size(); i++) {
                    System.out.println((i + 1) + ". " + users.get(i).getUsername());
                }
                System.out.println("0. Volver");

                int idx = getUserOption(scanner, "Elige un perfil: ", 0, users.size());
                if (idx == 0) {
                    break;
                }

                activeUser = users.get(idx - 1);
                System.out.println("[OK] Perfil activo: " + activeUser.getUsername());
            }
        }
    }

    return activeUser;
}

    //catalogo peliculas
    private static Collection<Movie> getMovies() {
        return Set.of(
                new Movie("Extraction", "Acción", 4.1, 120),
                new Movie("Atomic Blonde", "Acción", 4.3, 180),
                new Movie("The Old Guard", "Acción", 3.9, 95),
                new Movie("Nobody", "Acción", 4.2, 140),
                new Movie("Rambo: Last Blood", "Acción", 3.8, 160),
                new Movie("Angel Has Fallen", "Acción", 4.7, 110),
                new Movie("6 Underground", "Acción", 3.7, 150),
                new Movie("Bloodshot", "Acción", 3.6, 130),
                new Movie("Peppermint", "Acción", 4.3, 200),
                new Movie("Hard Kill", "Acción", 3.5, 90),
                new Movie("John Wick", "Acción", 4.5, 320),
                new Movie("Mad Max: Fury Road", "Acción", 4.6, 410),
                new Movie("Mission Impossible: Fallout", "Acción", 4.4, 350),
                new Movie("The Equalizer", "Acción", 4.2, 210),
                new Movie("Gladiator", "Acción", 4.7, 500),
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
                new Movie("Superbad", "Comedia", 4.4, 260),
                new Movie("The Hangover", "Comedia", 4.3, 340),
                new Movie("Step Brothers", "Comedia", 4.1, 200),
                new Movie("Mean Girls", "Comedia", 4.2, 230),
                new Movie("Anchorman", "Comedia", 4.0, 180),
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
                new Movie("The Shawshank Redemption", "Drama", 4.9, 900),
                new Movie("Forrest Gump", "Drama", 4.8, 820),
                new Movie("Fight Club", "Drama", 4.7, 750),
                new Movie("Whiplash", "Drama", 4.6, 500),
                new Movie("The Social Network", "Drama", 4.4, 320),
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
                new Movie("Interstellar", "Ciencia Ficción", 4.8, 880),
                new Movie("Inception", "Ciencia Ficción", 4.7, 840),
                new Movie("Blade Runner 2049", "Ciencia Ficción", 4.5, 410),
                new Movie("The Matrix", "Ciencia Ficción", 4.9, 920),
                new Movie("Arrival", "Ciencia Ficción", 4.4, 350),
                new Movie("The Rental", "Terror", 4.0, 160),
                new Movie("Host", "Terror", 4.2, 110),
                new Movie("Relic", "Terror", 3.9, 95),
                new Movie("The Dark and the Wicked", "Terror", 4.1, 150),
                new Movie("Run", "Terror", 4.3, 190),
                new Movie("The Beach House", "Terror", 3.7, 100),
                new Movie("Come Play", "Terror", 4.0, 170),
                new Movie("No Escape", "Terror", 3.8, 140),
                new Movie("You Should Have Left", "Terror", 3.9, 200),
                new Movie("The Conjuring", "Terror", 4.3, 330),
                new Movie("Hereditary", "Terror", 4.2, 260),
                new Movie("A Quiet Place", "Terror", 4.4, 370),
                new Movie("It", "Terror", 4.1, 300),
                new Movie("Amulet", "Terror", 3.6, 120));
                
    }
}
