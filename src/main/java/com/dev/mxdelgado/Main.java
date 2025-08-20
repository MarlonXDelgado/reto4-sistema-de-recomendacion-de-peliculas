package com.dev.mxdelgado;

import java.util.Collection;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.LoggerFactory;

public class Main {
    public static void main(String[] args) {
        var logger = LoggerFactory.getLogger(Main.class.getName());

        logger.info("Iniciando el programa");
        var recomendation = new RecommendationSystem();
        recomendation.loadMovies(getMovies());

        try (var scanner = new Scanner(System.in)) {
            var exit = false;
            while (!exit) {
                System.out.println("\n" + """
                        =====================================
                        |    SISTEMA DE RECOMENDACION       |
                        =====================================
                        1. Ver todas las peliculas por genero
                        0. Salir
                        """);

                int opcion = -1;
                while (true) {
                    var error = false;
                    try {
                        System.out.println("\nIngrese una opción: ");
                        opcion = Integer.valueOf(scanner.nextLine());

                        if (opcion < 0 || opcion > 1) {
                            throw new InvalidOptionException();
                        }

                    } catch (NumberFormatException | InvalidOptionException e) {
                        System.err.println("Opcion no valida. Intente nuevamente");
                        logger.warn("Opcion no valida: {}", scanner.nextLine());
                        error = true;
                    }

                    if (!error) {
                        break;
                    }
                }

                switch (opcion) {
                    case 0:
                        exit = true;
                        break;
                    case 1:
                        logger.info("\nMostrando peliculas por genero");
                        showMoviesByGenre(scanner, recomendation);
                        break;
                    default:
                        System.err.println("\nOpción no válida");
                        logger.warn("Opción no válida: {}", opcion);
                        break;
                }

            }
            System.out.println("\nGracias por usar el sistema de recomendación de peliculas, hasta pronto!");
            logger.debug("Saliendo del programa");
        }
    }

    private static void showMoviesByGenre(Scanner scanner, RecommendationSystem recomendation) {
        System.out.println("\n" + """
                -------------------------------------------
                |      Listado de peliculas por genero    |
                -------------------------------------------
                """);
        System.out.println("\nSeleccione el genero de su preferencia: ");
        var genres = recomendation.getGenres();
        for (int i = 0; i < genres.size(); i++) {
            System.out.println((i + 1) + ". " + genres.get(i));
        }

        System.out.println("\nSeleccione el genero: ");
        var opcion = Integer.valueOf(scanner.nextLine());

        var genre = genres.get(--opcion);

        var movies = recomendation.getMoviesByGenre(genre);
        System.out.printf("\nLas peliculas del genero %s son: %n\n", genre);
        movies.forEach(System.out::println);

        System.out.println("\nPresione ENTER para continuar");
        scanner.nextLine();

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
