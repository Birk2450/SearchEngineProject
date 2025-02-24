package searchengine;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link QueryHandler} class.
 * This test suite verifies the correctness of query parsing, parameter
 * extraction,
 * and logical operator handling in queries.
 */
class QueryHandlerTest {
    private QueryHandler queryHandler;
    private List<List<String>> parsedQuery;

    /**
     * Sets up the {@link QueryHandler} instance and initializes a parsed query.
     *
     * @throws IOException if an I/O error occurs during setup
     */
    @BeforeEach
    public void setup() throws IOException {
        queryHandler = new QueryHandler();
        parsedQuery = queryHandler.parseQuery("word1%20OR%20word3");
    }

    /**
     * Verifies the behavior of {@link QueryHandler#parseQueryParams(String)} with a
     * valid query.
     * Ensures the query parameters are correctly parsed into key-value pairs.
     */
    @Test
    public void testParseQueryParams_withValidQuery() {
        String rawQuery = "q=example&algorithm=simple";
        Map<String, String> result = queryHandler.parseQueryParams(rawQuery);

        assertEquals(2, result.size(), "Query params should contain two entries.");
        assertEquals("example", result.get("q"), "Query value should be 'example'.");
        assertEquals("simple", result.get("algorithm"), "Algorithm value should be 'simple'.");
    }

    /**
     * Verifies that query parameters without an equals sign are ignored.
     */
    @Test
    public void testNoEqualsSign() {
        String rawQuery = "key1value1&key2=value2";
        Map<String, String> expected = Map.of("key2", "value2");
        assertEquals(expected, queryHandler.parseQueryParams(rawQuery));
    }

    /**
     * Tests parsing an empty query string.
     * Ensures no parameters are parsed.
     */
    @Test
    public void testEmptyQuery() {
        String rawQuery = "";
        Map<String, String> expected = Map.of();
        assertEquals(expected, queryHandler.parseQueryParams(rawQuery));
    }

    /**
     * Tests parsing a null query string.
     * Ensures no parameters are parsed and the method handles null input safely.
     */
    @Test
    public void testNullQuery() {
        String rawQuery = null;
        Map<String, String> expected = Map.of();
        assertEquals(expected, queryHandler.parseQueryParams(rawQuery));
    }

    /**
     * Verifies that extra delimiters in the query string are ignored.
     */
    @Test
    public void testExtraDelimiters() {
        String rawQuery = "&key1=value1&&key2=value2&";
        Map<String, String> expected = Map.of("key1", "value1", "key2", "value2");
        assertEquals(expected, queryHandler.parseQueryParams(rawQuery));
    }

    /**
     * Verifies that query parameters with special characters are correctly parsed.
     */
    @Test
    public void testSpecialCharacters() {
        String rawQuery = "key1=val%20ue1&key2=val@ue2";
        Map<String, String> expected = Map.of("key1", "val%20ue1", "key2", "val@ue2");
        assertEquals(expected, queryHandler.parseQueryParams(rawQuery));
    }

    /**
     * Verifies the behavior of {@link QueryHandler#isValidKeyValue(String[])}.
     * Tests key-value pairs with valid, empty, and missing keys or values.
     */
    @Test
    public void testIsValidKeyValue() {
        String[] validKeyValue = { "key1", "value1" };
        String[] emptyValue = { "key1", "" };
        String[] emptyKey = { "", "value1" };

        assertTrue(queryHandler.isValidKeyValue(validKeyValue), "Valid key-value pair should return true.");
        assertFalse(queryHandler.isValidKeyValue(emptyValue), "Key-value pair with empty value should return false.");
        assertFalse(queryHandler.isValidKeyValue(emptyKey), "Key-value pair with empty key should return false.");
    }

    /**
     * Verifies the behavior of {@link QueryHandler#extractQueryParams(String)}.
     */
    @Test
    public void testExtractQueryParams() {
        String rawQuery = "q=example&algorithm=simple";
        assertEquals("example", queryHandler.extractQueryParams(rawQuery));
    }

    /**
     * Verifies the behavior of {@link QueryHandler#extractAlgorithm(String)}.
     */
    @Test
    public void testExtractAlgorithm() {
        String rawQuery = "query=example&algorithm=simple";
        assertEquals("simple", queryHandler.extractAlgorithm(rawQuery));
    }

    /**
     * Verifies that a query with "AND" is parsed into separate groups,
     * and the {@code andIsTrue} flag is set correctly.
     */
    @Test
    public void testParseQueryWithAND() {
        queryHandler = new QueryHandler();
        parsedQuery = queryHandler.parseQuery("word1%20AND%20word3");

        assertTrue(queryHandler.getAndIsTrue(), "AND flag should be true.");
        assertEquals(2, parsedQuery.size(), "Query should contain two groups.");
        assertEquals("word1", parsedQuery.get(0).get(0), "First group should contain 'word1'.");
        assertEquals("word3", parsedQuery.get(1).get(0), "Second group should contain 'word3'.");
    }

    /**
     * Verifies that splitting an array with "AND" results in the expected groups.
     */
    @Test
    public void testNormalAND() {
        String query = "group1%20AND%20group2%20AND%20group3";
        String[] expected = { "group1", "group2", "group3" };
        String[] groups = query.split("(%20)*AND(%20)*");
        assertArrayEquals(expected, groups);
    }

    /**
     * Verifies that a query with "OR" is parsed into separate groups.
     */
    @Test
    public void testParseQueryWithOR() {
        QueryHandler queryHandler = new QueryHandler();
        List<List<String>> parsedQuery = queryHandler.parseQuery("word1%20OR%20word3");

        assertNotNull(queryHandler, "QueryHandler should be initialized.");
        assertEquals(2, parsedQuery.size(), "Query should contain two groups.");
        assertEquals("word1", parsedQuery.get(0).get(0), "First group should contain 'word1'.");
        assertEquals("word3", parsedQuery.get(1).get(0), "Second group should contain 'word3'.");
    }

    /**
     * Verifies that a query without logical operators is parsed into a single
     * group.
     */
    @Test
    public void testParseQueryWithoutLogicalOperator() {
        QueryHandler queryHandler = new QueryHandler();
        List<List<String>> parsedQuery = queryHandler.parseQuery("word1%20%20word3");

        assertNotNull(queryHandler, "QueryHandler should be initialized.");
        assertEquals(1, parsedQuery.size(), "Query should contain one group.");
        assertEquals("word1", parsedQuery.get(0).get(0), "First group should contain 'word1'.");
        assertEquals("word3", parsedQuery.get(0).get(1), "First group should contain 'word3'.");
    }
}