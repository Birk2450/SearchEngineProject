package searchengine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * The {@code WebServer} class represents a simple HTTP server designed to serve
 * a web-based
 * search engine application. It handles routing requests, responding to client
 * queries,
 * and performing search operations using the {@link SearchEngine} and
 * associated components.
 * <p>
 * The server operates on a specified port and uses a {@link SearchEngine}
 * instance to perform
 * its core search functionality. It provides endpoints for serving static
 * assets (HTML, CSS, JS)
 * and handling search requests.
 * </p>
 */
public class WebServer {
  /**
   * The default port on which the web server runs.
   */
  public static final int PORT = 8080;
  private static final int BACKLOG = 0;
  /**
   * The character encoding used for all HTTP responses.
   */
  public static final Charset CHARSET = StandardCharsets.UTF_8;
  private final SearchEngine searchEngine;
  private final HttpServer server;

  /**
   * Constructs a new {@code WebServer} instance.
   *
   * @param port         The port to run the web server on. If set to {@code 0},
   *                     the server will
   *                     not start on a default port unless configured.
   * @param searchEngine The {@link SearchEngine} instance for handling queries.
   *                     Must not be {@code null}.
   * @throws IOException if the server cannot be created.
   */
  public WebServer(int port, SearchEngine searchEngine) throws IOException {
    this.searchEngine = searchEngine;
    server = HttpServer.create(new InetSocketAddress(port), BACKLOG);
    setupHTTP();
  }

  /**
   * Configures the routes for the HTTP server by creating contexts for predefined
   * paths.
   * Each context serves a specific file or handles a specific type of request.
   */
  public void setupHTTP() {
    createContext("/", "text/html", "web/index.html");
    createContext("/favicon.ico", "image/x-icon", "web/favicon.ico");
    createContext("/code.js", "application/javascript", "web/code.js");
    createContext("/style.css", "text/css", "web/style.css");
    server.createContext("/search", this::respondToClient);
  }

  /**
   * Prints the server's status to the console, including the port it's running
   * on.
   *
   * @param port The port number the server is running on.
   */
  public void printServerStatus(int port) {
    String msg = " WebServer running on http://localhost:" + port + " ";
    System.out.println("╭" + "─".repeat(msg.length()) + "╮");
    System.out.println("│" + msg + "│");
    System.out.println("╰" + "─".repeat(msg.length()) + "╯");
  }

  /**
   * Starts the server and prints the link to the console.
   */
  public void startServer() {
    server.start();
    printServerStatus(PORT);
  }

  /**
   * Creates a context on the server, mapping a specific URL path to a response
   * handler
   * that serves a file with the given MIME type.
   *
   * @param path     The URL path that this context will handle (e.g.,
   *                 "/index.html").
   * @param mime     The MIME type of the content to be served (e.g.,
   *                 "text/html").
   * @param filename The name of the file whose content will be served when the
   *                 path is accessed.
   */
  public void createContext(String path, String mime, String filename) {
    server.createContext(path, io -> respond(io, 200, mime, convertFileToByteArray(filename)));
  }

  /**
   * Fetches a list of pages in the database that satisfy the user query and sorts
   * the
   * results based on the user's preferences.
   *
   * @param io The {@link HttpExchange} object representing the HTTP request.
   * @return A byte array containing a formatted list of pages matching the query.
   */
  public byte[] fetchSearchResults(HttpExchange io) {
    try {
      String rawQuery = io.getRequestURI().getRawQuery();
      QueryHandler queryHandler = new QueryHandler();
      String query = queryHandler.extractQueryParams(rawQuery);
      String algorithm = queryHandler.extractAlgorithm(rawQuery);
      List<List<String>> parsedQuery = queryHandler.parseQuery(query);
      boolean andIsTrue = queryHandler.getAndIsTrue();

      List<Page> searchResults = searchEngine.search(parsedQuery, andIsTrue);
      SortHandler sortHandler = new SortHandler();
      List<Page> sortedResults = sortHandler.sortResults(searchResults, parsedQuery, searchEngine, algorithm);

      return formatResponse(sortedResults);
    } catch (Exception e) {
      e.printStackTrace();
      String errorMessage = "An error occurred while processing your request.";
      return errorMessage.getBytes(CHARSET);
    }
  }

  /**
   * Formats a list of pages into a JSON-like byte array response.
   * If the list is empty, a "404" message is returned as a byte array.
   *
   * @param results The list of pages to format.
   * @return A byte array containing the formatted response.
   */
  public byte[] formatResponse(List<Page> results) {
    if (results.isEmpty()) {
      String noMatchMessage = "404";
      return noMatchMessage.getBytes(CHARSET);
    }

    ArrayList<String> response = new ArrayList<>();
    for (Page page : results) {
      response.add(String.format("{\"url\": \"%s\", \"title\": \"%s\"}", page.getUrl(), page.getTitle()));
    }
    return response.toString().getBytes(CHARSET);
  }

  /**
   * Responds to a client request by fetching and sending the appropriate data.
   *
   * @param io The {@link HttpExchange} object representing the HTTP request.
   */
  public void respondToClient(HttpExchange io) {
    try {
      byte[] responseBytes = fetchSearchResults(io);
      int responseCode = new String(responseBytes, CHARSET).equals("404") ? 404 : 200;
      String contentType = responseCode == 404 ? "text/plain" : "application/json";

      respond(io, responseCode, contentType, responseBytes);
    } catch (Exception e) {
      e.printStackTrace();
      byte[] errorBytes = "An error occurred while sending the response.".getBytes(CHARSET);
      respond(io, 500, "text/plain", errorBytes);
    }
  }

  /**
   * Converts the contents of a file into a byte array.
   *
   * @param filename The name of the file to convert.
   * @return A byte array containing the file's contents, or an empty array if an
   *         error occurs.
   */
  public byte[] convertFileToByteArray(String filename) {
    if (filename == null) {
      return new byte[0];
    }
    try {
      return Files.readAllBytes(Paths.get(filename));
    } catch (IOException e) {
      e.printStackTrace();
      return new byte[0];
    }
  }

  /**
   * Sends a response to the client through the {@link HttpExchange} object.
   *
   * @param io       The {@link HttpExchange} object.
   * @param code     The HTTP status code.
   * @param mime     The MIME type of the response.
   * @param response The response content as a byte array.
   */
  public void respond(HttpExchange io, int code, String mime, byte[] response) {
    try {
      io.getResponseHeaders().set("Content-Type", String.format("%s; charset=%s", mime, CHARSET.name()));
      io.sendResponseHeaders(code, response.length);
      io.getResponseBody().write(response);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      io.close();
    }
  }

  /**
   * Handles a search query for testing purposes by simulating a search request.
   *
   * @param query The search query string.
   * @return A byte array containing the search results or a "404" message.
   */
  public byte[] handleRequest(String query) {
    if (query == null || query.isEmpty()) {
      return "404".getBytes(CHARSET);
    }
    List<List<String>> queryAsList = List.of(List.of(query));
    List<Page> results = searchEngine.search(queryAsList, true);
    if (results == null || results.isEmpty()) {
      return "404".getBytes(CHARSET);
    }
    return formatResponse(results);
  }
}