package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link SortHandler} class.
 * <p>
 * This test class validates the functionality of the {@link SortHandler},
 * including selecting scoring methods, sorting pages using different
 * algorithms,
 * and handling edge cases like empty inputs.
 * </p>
 */
public class SortHandlerTest {

    private static final Path TEST_FILE_PATH = Paths.get("src/test/java/searchengine/resources/test-file.txt");
    private SortHandler sortHandler;
    private SearchEngine searchEngine;

    /**
     * Sets up the test environment by initializing the {@link SortHandler}
     * and {@link SearchEngine}.
     *
     * @throws IOException if the test file cannot be read.
     */
    @BeforeEach
    public void setup() throws IOException {
        searchEngine = new SearchEngine(TEST_FILE_PATH.toString());
        sortHandler = new SortHandler();
    }

    /**
     * Tests that selecting the "SIMPLE" algorithm returns an instance of
     * {@link SimpleFrequencyScoring}.
     */
    @Test
    public void testSelectScoringMethodSimple() {
        String algo = "SIMPLE";
        assertEquals(sortHandler.selectScoringMethod(algo).getClass(), new SimpleFrequencyScoring().getClass());
    }

    /**
     * Tests that selecting the "TFIDF" algorithm returns an instance of
     * {@link TFIDFScoring}.
     */
    @Test
    public void testSelectScoringMethodTFIDF() {
        String algo = "TFIDF";
        assertEquals(sortHandler.selectScoringMethod(algo).getClass(), new TFIDFScoring().getClass());
    }

    /**
     * Tests that selecting an invalid scoring algorithm throws an
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testSelectScoringMethodThrowsIllegalArgumentException() {
        String algo = "dsæfwefbkfdgio";
        assertThrows(IllegalArgumentException.class, () -> sortHandler.selectScoringMethod(algo));
    }

    /**
     * Tests that sorting pages using the "SIMPLE" algorithm does not return a
     * {@code null} or empty list.
     *
     * @throws IOException if an error occurs while accessing the database.
     */
    @Test
    public void testSortByAlgorithmSIMPLEDoesNotReturnNullOrEmptyList() throws IOException {
        QueryHandler queryHandler = new QueryHandler();
        List<List<String>> query = queryHandler.parseQuery("word1");

        Set<Page> pages = searchEngine.accessDatabase("word1");
        List<Page> pagesList = new ArrayList<>(pages);
        ScoringMethod algo = sortHandler.selectScoringMethod("SIMPLE");
        List<Page> sortedPages = sortHandler.sortByAlgorithm(pagesList, query, searchEngine.getDatabase(), algo);

        assertNotNull(sortedPages, "Sorted pages should not be null.");
        assertTrue(sortedPages.size() > 0, "Sorted pages should contain entries.");
    }

    /**
     * Tests that sorting pages using the "TFIDF" algorithm does not return a
     * {@code null} or empty list.
     *
     * @throws IOException if an error occurs while accessing the database.
     */
    @Test
    public void testSortByAlgorithmTFIDFDoesNotReturnNullOrEmptyList() throws IOException {
        QueryHandler queryHandler = new QueryHandler();
        List<List<String>> query = queryHandler.parseQuery("word1");

        Set<Page> pages = searchEngine.accessDatabase("word1");
        List<Page> pagesList = new ArrayList<>(pages);
        ScoringMethod algo = sortHandler.selectScoringMethod("TFIDF");
        List<Page> sortedPages = sortHandler.sortByAlgorithm(pagesList, query, searchEngine.getDatabase(), algo);

        assertNotNull(sortedPages, "Sorted pages should not be null.");
        assertTrue(sortedPages.size() > 0, "Sorted pages should contain entries.");
    }

    /**
     * Tests that pages sorted using the "SIMPLE" algorithm are ordered by term
     * frequency (TF).
     */
    @Test
    public void testSortByAlgorithmSIMPLEReturnsListSortedByTF() {
        QueryHandler queryHandler = new QueryHandler();
        List<List<String>> query = queryHandler.parseQuery("word1");

        Set<Page> pages = searchEngine.accessDatabase("word1");
        List<Page> pagesList = new ArrayList<>(pages);
        ScoringMethod algo = sortHandler.selectScoringMethod("SIMPLE");
        List<Page> sortedPages = sortHandler.sortByAlgorithm(pagesList, query, searchEngine.getDatabase(), algo);

        double previousScore = Double.MAX_VALUE; // Assuming higher scores should come first
        for (Page page : sortedPages) {
            double currentScore = sortHandler.calculatePageScore(page, query, searchEngine.getDatabase(), algo);
            assertTrue(currentScore <= previousScore,
                    "Pages are not sorted correctly by score. Found a page with score " + currentScore
                            + " after one with score " + previousScore);
            previousScore = currentScore;
        }
    }

    /**
     * Tests that pages sorted using the "TFIDF" algorithm are ordered by their
     * TF-IDF score.
     */
    @Test
    public void testSortByAlgorithmTFIDFReturnsListSortedByTFIDF() {
        QueryHandler queryHandler = new QueryHandler();
        List<List<String>> query = queryHandler.parseQuery("word1");

        Set<Page> pages = searchEngine.accessDatabase("word1");
        List<Page> pagesList = new ArrayList<>(pages);
        ScoringMethod algo = sortHandler.selectScoringMethod("TFIDF");
        List<Page> sortedPages = sortHandler.sortByAlgorithm(pagesList, query, searchEngine.getDatabase(), algo);

        double previousScore = Double.MAX_VALUE; // Assuming higher scores should come first
        for (Page page : sortedPages) {
            double currentScore = sortHandler.calculatePageScore(page, query, searchEngine.getDatabase(), algo);
            assertTrue(currentScore <= previousScore,
                    "Pages are not sorted correctly by score. Found a page with score " + currentScore
                            + " after one with score " + previousScore);
            previousScore = currentScore;
        }
    }

    /**
     * Tests that sorting an empty list of pages results in an empty sorted list.
     */
    @Test
    public void testSortResultsWithEmptyPages() {
        QueryHandler queryHandler = new QueryHandler();
        List<Page> pages = new ArrayList<>();
        List<List<String>> parsedQuery = queryHandler.parseQuery("word1");
        List<Page> sortedPages = sortHandler.sortResults(pages, parsedQuery, searchEngine, "SIMPLE");
        assertTrue(sortedPages.isEmpty(), "Sorted list should be empty for empty input pages.");
    }
}