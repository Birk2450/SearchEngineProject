package searchengine;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link WebServer} class.
 * <p>
 * This test class verifies the core functionality of the {@code WebServer},
 * including starting the server, handling requests, converting files to byte
 * arrays,
 * and processing search queries.
 * </p>
 */
public class WebServerTest {

    private static final Path TEST_FILE_PATH = Paths.get("src/test/java/searchengine/resources/test-file.txt");
    private WebServer webServer;
    private SearchEngine searchEngine;
    static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * Sets up the test environment by initializing the {@link WebServer} and
     * {@link SearchEngine}.
     *
     * @throws IOException if the test file cannot be read or the server cannot be
     *                     started.
     */
    @BeforeEach
    public void setup() throws IOException {
        searchEngine = new SearchEngine(TEST_FILE_PATH.toString());
        webServer = new WebServer(0, searchEngine);
    }

    /**
     * Tests that the server starts without throwing exceptions.
     */
    @Test
    public void testWebServerStarts() {
        assertDoesNotThrow(() -> webServer.startServer(), "Server should start without exceptions.");
    }

    /**
     * Tests that starting the server with an invalid port throws an
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testStartServerWithInvalidPort() {
        assertThrows(IllegalArgumentException.class, () -> new WebServer(-1, searchEngine).startServer(),
                "Expected an IllegalArgumentException when starting server on an invalid port.");
    }

    /**
     * Tests that converting an empty file path to a byte array returns an empty
     * array.
     */
    @Test
    public void testConvertFileToByteArrayReturnsEmptyByteArrayForEmptyFilename() {
        byte[] result = webServer.convertFileToByteArray("");
        assertArrayEquals(new byte[0], result, "Expected an empty byte array for an empty file path.");
    }

    /**
     * Tests that converting a null file path to a byte array returns an empty
     * array.
     */
    @Test
    public void testConvertFileToByteArrayReturnsEmptyByteArrayForNullFilename() {
        byte[] result = webServer.convertFileToByteArray(null);
        assertArrayEquals(new byte[0], result, "Expected an empty byte array for a null file path.");
    }

    /**
     * Tests that converting a nonexistent file path to a byte array returns an
     * empty array.
     */
    @Test
    public void testConvertFileToByteArrayReturnsEmptyByteArrayForNonexistentFile() {
        String nonexistentFilePath = "nonexistent-file.txt";
        byte[] result = webServer.convertFileToByteArray(nonexistentFilePath);
        assertArrayEquals(new byte[0], result, "Expected an empty byte array for a nonexistent file path.");
    }

    /**
     * Tests that converting a valid file to a byte array correctly reads the file
     * content.
     *
     * @throws IOException if there is an error creating or reading the temporary
     *                     file.
     */
    @Test
    public void testConvertFileToByteArrayReadsFileCorrectly() throws IOException {
        Path tempFile = Files.createTempFile("test-file", ".txt");
        String fileContent = "This is a test file.";
        Files.write(tempFile, fileContent.getBytes());
        byte[] result = webServer.convertFileToByteArray(tempFile.toString());
        assertArrayEquals(fileContent.getBytes(), result, "The byte array should match the file content.");
        Files.delete(tempFile);
    }

    /**
     * Tests formatting a response for valid pages.
     */
    @Test
    public void testFormatResponseWithValidPages() {
        List<Page> pages = new ArrayList<>();
        pages.add(new Page(List.of("http://test.com", "title", "content")));
        byte[] response = webServer.formatResponse(pages);
        assertNotNull(response, "Response should not be null for valid pages.");
        assertTrue(response.length > 0, "Response should not be empty.");
    }

    /**
     * Tests formatting a response for an empty page list.
     */
    @Test
    public void testFormatResponseWithEmptyPages() {
        List<Page> pages = new ArrayList<>();
        byte[] response = webServer.formatResponse(pages);
        assertArrayEquals("404".getBytes(CHARSET), response, "Response should be '404' for an empty page list.");
    }

    /**
     * Tests handling a valid query in the {@code handleRequest} method.
     */
    @Test
    public void testHandleRequestReturnsValidResponse() {
        String query = "word1";
        byte[] response = webServer.handleRequest(query);
        assertNotNull(response, "Response should not be null for a valid query.");
        assertTrue(response.length > 0, "Response should not be empty for a valid query.");
    }

    /**
     * Tests handling an invalid query in the {@code handleRequest} method.
     */
    @Test
    public void testHandleRequestReturns404ForInvalidQuery() {
        String query = "nonexistentword";
        byte[] response = webServer.handleRequest(query);
        assertArrayEquals("404".getBytes(CHARSET), response, "Response should be '404' for a query with no matches.");
    }

    /**
     * Tests handling of null or empty queries in the {@code handleRequest} method.
     */
    @Test
    public void testHandleRequestHandlesNullOrEmptyQuery() {
        byte[] responseNull = webServer.handleRequest(null);
        assertArrayEquals("404".getBytes(CHARSET), responseNull, "Response should be '404' for a null query.");
        byte[] responseEmpty = webServer.handleRequest("");
        assertArrayEquals("404".getBytes(CHARSET), responseEmpty, "Response should be '404' for an empty query.");
    }

    /**
     * Tests the {@code fetchSearchResults} method with a valid query.
     *
     * @throws IOException        if an I/O error occurs while interacting with the
     *                            server.
     * @throws URISyntaxException if the URI for the HTTP request is invalid.
     */
    @Test
    public void testFetchSearchResultsWithValidQuery() throws IOException, URISyntaxException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/search", exchange -> {
            byte[] response = webServer.fetchSearchResults(exchange);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();

        int port = server.getAddress().getPort();
        URI uri = new URI("http", null, "localhost", port, "/search", "q=word1&algorithm=SIMPLE", null);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        assertEquals(200, connection.getResponseCode(), "Expected HTTP 200 status code");

        String response = new String(connection.getInputStream().readAllBytes(), CHARSET);
        assertTrue(response.contains("\"url\""), "Response should contain search results.");

        server.stop(0);
    }

    /**
     * Tests the {@code fetchSearchResults} method with an invalid query.
     *
     * @throws IOException        if an I/O error occurs while interacting with the
     *                            server.
     * @throws URISyntaxException if the URI for the HTTP request is invalid.
     */
    @Test
    public void testFetchSearchResultsWithInvalidQuery() throws IOException, URISyntaxException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/search", exchange -> {
            byte[] response = webServer.fetchSearchResults(exchange);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();

        int port = server.getAddress().getPort();
        URI uri = new URI("http", null, "localhost", port, "/search", "q=nonexistentword&algorithm=SIMPLE", null);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        assertEquals(200, connection.getResponseCode(), "Expected HTTP 200 status code");

        String response = new String(connection.getInputStream().readAllBytes(), CHARSET);
        assertEquals("404", response, "Response should be '404' for no results.");

        server.stop(0);
    }
}