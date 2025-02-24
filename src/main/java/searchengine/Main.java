package searchengine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The main entry point for the search engine application. 
 * This class initializes the {@link SearchEngine} with the database file path 
 * specified in the configuration file and starts the {@link WebServer}.
 * <p>
 * The configuration file, named {@code config.txt}, must be located in the working directory 
 * and contain the path to the database file. The program will terminate if any error occurs 
 * during initialization or server startup.
 * </p>
 */
public class Main {
    /**
     * The main method initializes the application by reading the configuration file,
     * setting up the {@link SearchEngine} and {@link WebServer}, and starting the server.
     *
     * @param args command-line arguments (not used).
     * @throws IOException if an I/O error occurs while reading the configuration file,
     *                     initializing the {@link SearchEngine}, or starting the {@link WebServer}.
     */
    public static void main(final String... args) throws IOException {
        var filename = Files.readString(Paths.get("config.txt")).strip();
        SearchEngine searchEngine = new SearchEngine(filename);
        WebServer webServer = new WebServer(WebServer.PORT, searchEngine);
        webServer.startServer();
    }
}
