package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Unit tests for the {@link SimpleFrequencyScoring} class.
 * <p>
 * This class verifies that the {@link SimpleFrequencyScoring} correctly
 * calculates
 * word frequencies from a given page in the context of a database.
 * </p>
 */
public class SimpleFrequencyScoringTest {

    private static final Path TEST_FILE_PATH = Paths.get("src/test/java/searchengine/resources/test-file.txt");

    private SimpleFrequencyScoring simpleFrequencyScoring;
    private Page testPage;
    private Database database;

    /**
     * Sets up the test environment by initializing the
     * {@link SimpleFrequencyScoring}
     * instance and loading a test database.
     * <p>
     * A specific page is extracted from the database to use for testing.
     * </p>
     *
     * @throws IOException if the database file cannot be read.
     */
    @BeforeEach
    public void setUp() throws IOException {
        simpleFrequencyScoring = new SimpleFrequencyScoring();
        database = new Database(TEST_FILE_PATH.toAbsolutePath().toString());

        Set<Page> pages = database.getDatabase().values().iterator().next();
        testPage = pages.iterator().next();
    }

    /**
     * Tests that the {@code calculateScore} method returns the correct frequency
     * of a word on a specific page.
     * <p>
     * This test compares the frequency calculated by the
     * {@link SimpleFrequencyScoring}
     * instance to the frequency obtained directly from the page.
     * </p>
     */
    @Test
    public void testCalculateScoreReturnsWordFrequency() {
        String word = "word1";

        double actualFrequency = simpleFrequencyScoring.calculateScore(word, testPage, database);
        double expectedFrequency = testPage.getWordFrequency(word);

        assertEquals(expectedFrequency, actualFrequency, "The score should match the word frequency on the page.");
    }
}