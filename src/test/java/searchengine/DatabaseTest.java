package searchengine;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Database} class.
 * <p>
 * This test class verifies the functionality of the {@link Database} class,
 * including
 * initialization, word frequency tracking, and total page count accuracy.
 * </p>
 */
class DatabaseTest {

    private static final Path TEST_FILE_PATH = Paths.get("src/test/java/searchengine/resources/test-file.txt");
    private Database database;

    /**
     * Sets up the test environment by initializing a {@link Database} instance
     * using a test file before each test.
     *
     * @throws IOException if the test file cannot be read or the database fails to
     *                     initialize.
     */
    @BeforeEach
    public void setup() throws IOException {
        database = new Database(TEST_FILE_PATH.toAbsolutePath().toString());
    }

    /**
     * Tests that the {@link Database} instance is initialized correctly.
     * Ensures the instance is not {@code null} after setup.
     */
    @Test
    public void testDatabaseInitialization() {
        assertNotNull(database, "Database should be initialized.");
    }

    /**
     * Tests that the database correctly counts the number of pages containing a
     * specific word.
     * Verifies the word "word1" appears in 2 pages within the database.
     */
    @Test
    public void testPagesWithWord() {
        int pages = database.pagesWithWord("word1");
        assertEquals(2, pages, "'word1' should be found in 2 pages.");
    }

    /**
     * Tests that the database returns 0 for words not found in any page.
     * Verifies the word "word100" does not appear in the database.
     */
    @Test
    public void testPagesWithNoWord() {
        int noPages = database.pagesWithWord("word100");
        assertEquals(0, noPages, "'word100' should be found in 0 pages.");
    }

    /**
     * Tests that the database correctly calculates the total number of pages.
     * Verifies that the total page count is 4, based on the test file data.
     */
    @Test
    public void testTotalPages() {
        int totalPages = database.getTotalPages();
        assertEquals(4, totalPages, "Total pages in the database should be 4.");
    }
}