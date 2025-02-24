package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchEngineTest {
    private SearchEngine searchEngine;
    private boolean andIsTrue;
    private List<List<String>> parsedQuery;

    private static final String TEST_FILE_PATH = "data/test-file.txt";

    /**
     * Sets up the test environment by initializing the search engine and handler.
     *
     * @throws IOException if the test file cannot be read.
     */
    @BeforeEach
    public void setup() throws IOException {
        searchEngine = new SearchEngine(TEST_FILE_PATH);
        andIsTrue = true;
        parsedQuery = new ArrayList<>();
    }

    // Verifies that the search engine is initialized correctly.
    @Test
    public void testSearchEngineInitialization() throws IOException {
        SearchEngine searchEngine = new SearchEngine(TEST_FILE_PATH);

        assertNotNull(searchEngine, "SearchEngine should be initialized.");
    }

    @Test
    public void testGetDatabase() {
        // Call the getter
        Database result = searchEngine.getDatabase();

        // Assert the getter returns the same instance as initialized
        assertNotNull(result, "The returned database instance should not be null.");
        assertSame(searchEngine.getDatabase(), result,
                "The returned database instance should be the same as the one provided.");
    }

    /**
     * Tests that a null query throws an IllegalArgumentException.
     */
    @Test
    public void testSearchPagesThrowsIllegalArgumentExceptionForNullQuery() {
        parsedQuery = null;
        assertThrows(IllegalArgumentException.class, () -> searchEngine.search(parsedQuery, andIsTrue));
    }

    /**
     * Tests that an empty query throws an IllegalArgumentException.
     */
    @Test
    public void testSearchPagesThrowsIllegalArgumentExceptionForEmptyQuery() {
        parsedQuery = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> searchEngine.search(parsedQuery, andIsTrue));
    }

    /**
     * Tests that searching for nonexistent words returns an empty list.
     */
    @Test
    public void testSearchPagesReturnsEmptyListForNoMatch() {
        parsedQuery.add(List.of("nonexistentword"));
        List<Page> result = searchEngine.search(parsedQuery, andIsTrue);
        assertEquals(new ArrayList<Page>(), result, "Search for unmatched query should return an empty list.");
    }

    /**
     * Tests that grouped queries return results.
     */
    @Test
    public void testSearchPagesReturnsListOfCorrectSizeForGroupedQuery() {
        parsedQuery.add(List.of("word1"));
        parsedQuery.add(List.of("word3"));
        List<Page> result = searchEngine.search(parsedQuery, false);
        assertNotNull(result, "Search result should not be null.");
        assertTrue(result.size() > 0, "Grouped query should return results.");
    }

    /**
     * Tests that the result contains pages with the queried word.
     */
    @Test
    public void testSearchPagesReturnsListContainingQueryWord() {
        parsedQuery.add(List.of("word1"));
        List<Page> result = searchEngine.search(parsedQuery, true);
        assertNotNull(result, "Search result should not be null.");
        for (Page page : result) {
            assertTrue(page.containsSearchterm("word1"), "Resulting pages should contain the queried word.");
        }
    }

    /**
     * Tests that queries with AND logic return pages containing all specified
     * words.
     */
    @Test
    public void testSearchPagesReturnsListForANDQuery() {
        parsedQuery.add(List.of("word1", "word2"));
        List<Page> result = searchEngine.search(parsedQuery, true);
        assertNotNull(result, "Search result should not be null.");
        for (Page page : result) {
            assertTrue(page.containsSearchterm("word1") && page.containsSearchterm("word2"),
                    "Pages should contain both 'word1' and 'word2'.");
        }
    }

    /**
     * Tests that queries with OR logic return pages containing any of the specified
     * words.
     */
    @Test
    public void testSearchPagesReturnsListForORQuery() {
        parsedQuery.add(List.of("word1"));
        parsedQuery.add(List.of("word3"));
        List<Page> result = searchEngine.search(parsedQuery, false);
        assertNotNull(result, "Search result should not be null.");
        assertTrue(
                result.stream().anyMatch(page -> page.containsSearchterm("word1") || page.containsSearchterm("word3")),
                "At least one page should contain 'word1' or 'word3'.");
    }

    /**
     * Tests that queries with empty groups do not cause errors.
     */
    @Test
    public void testSearchPagesHandlesEmptyGroup() {
        parsedQuery.add(List.of("word1"));
        parsedQuery.add(new ArrayList<>());
        List<Page> result = searchEngine.search(parsedQuery, false);
        assertNotNull(result, "Search result should not be null.");
        assertTrue(result.stream().anyMatch(page -> page.containsSearchterm("word1")),
                "Pages should contain 'word1' despite an empty group.");
    }

    /**
     * Tests that large queries do not cause errors.
     */
    @Test
    public void testSearchPagesHandlesLargeQuery() {
        for (int i = 0; i < 1000; i++) {
            parsedQuery.add(List.of("word" + i));
        }
        List<Page> result = searchEngine.search(parsedQuery, false);
        assertNotNull(result, "Search result should not be null.");
        assertTrue(result.size() >= 0, "Large query should not cause errors.");
    }

    /**
     * Tests that multiple groups with AND logic return pages matching all groups.
     */
    @Test
    public void testSearchPagesWithMultipleGroupsAndANDLogic() {
        parsedQuery.add(List.of("word1"));
        parsedQuery.add(List.of("word2"));
        List<Page> result = searchEngine.search(parsedQuery, true);

        assertNotNull(result, "Search result should not be null.");
        for (Page page : result) {
            assertTrue(page.containsSearchterm("word1") && page.containsSearchterm("word2"),
                    "Pages should contain both 'word1' and 'word2'.");
        }
    }

    /**
     * Tests that findMatchingPages returns an empty set when no matches are found.
     */
    @Test
    public void testFindMatchingPagesReturnsEmptySet() {
        parsedQuery.add(List.of("nonexistentword1", "nonexistentword2"));
        Set<Page> result = searchEngine.findMatchingPages(parsedQuery.get(0));
        assertTrue(result.isEmpty(), "Expected an empty set when no pages match the query.");
    }
}
