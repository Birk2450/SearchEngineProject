package searchengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles parsing and executing queries for the search engine.
 * This class provides methods to parse query strings, extract parameters,
 * and structure the queries for logical operator handling.
 */
public class QueryHandler {
    private List<List<String>> parsedQuery;
    private boolean andIsTrue;

    /**
     * Initializes a new instance of the {@link QueryHandler} class.
     * The parsed query list and logical operator flag are initialized.
     */
    public QueryHandler() {
        parsedQuery = new ArrayList<>();
        andIsTrue = false;
    }

    /**
     * Retrieves the value of the logical operator flag.
     * 
     * @return {@code true} if the query contains the "AND" logical operator,
     *         {@code false} otherwise.
     */
    public boolean getAndIsTrue() {
        return andIsTrue;
    }

    /**
     * Parses a raw query string into a map of parameters.
     * This method splits the query string into key-value pairs and validates each
     * pair before adding it to the map.
     *
     * @param rawQuery The raw query string from the request URI.
     * @return A map of query parameters where keys are parameter names and values
     *         are parameter values.
     */
    public Map<String, String> parseQueryParams(String rawQuery) {
        Map<String, String> paramsMap = new HashMap<>();
        if (rawQuery != null && !rawQuery.isEmpty()) {
            String[] params = rawQuery.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (isValidKeyValue(keyValue)) {
                    paramsMap.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return paramsMap;
    }

    /**
     * Validates whether the given key-value pair is valid.
     * A valid key-value pair must have exactly two non-empty elements.
     *
     * @param keyValue The array containing a key-value pair split by '='.
     * @return {@code true} if the key-value pair is valid, {@code false} otherwise.
     */
    public boolean isValidKeyValue(String[] keyValue) {
        return keyValue.length == 2 && !keyValue[0].isEmpty() && !keyValue[1].isEmpty();
    }

    /**
     * Extracts the query parameter value for the key "q" from a raw query string.
     *
     * @param rawQuery The raw query string.
     * @return The value of the "q" parameter, or {@code null} if it is not present.
     */
    public String extractQueryParams(String rawQuery) {
        Map<String, String> queryParams = parseQueryParams(rawQuery);
        return queryParams.get("q");
    }

    /**
     * Extracts the query parameter value for the key "algorithm" from a raw query
     * string.
     *
     * @param rawQuery The raw query string.
     * @return The value of the "algorithm" parameter, or {@code null} if it is not
     *         present.
     */
    public String extractAlgorithm(String rawQuery) {
        Map<String, String> queryParams = parseQueryParams(rawQuery);
        return queryParams.get("algorithm");
    }

    /**
     * Parses a raw query string into a structured list format.
     * The query is split into groups based on the presence of logical operators
     * ("AND" or "OR"). Each group is further split into individual words.
     * Sets the {@code andIsTrue} flag if "AND" is found in the query.
     *
     * @param query The raw query string.
     * @return A list of lists, where each inner list represents a group of words
     *         from the query.
     */
    public List<List<String>> parseQuery(String query) {
        String[] groups = new String[] { query };
        if (query.contains("AND")) {
            groups = query.split("(%20)*AND(%20)*");
            andIsTrue = true;
        } else {
            groups = query.split("(%20)*OR(%20)*");
        }

        for (String group : groups) {
            String[] words = Arrays.stream(group.split("%20+"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
            List<String> wordList = new ArrayList<>();
            for (String word : words) {
                wordList.add(word);
            }
            parsedQuery.add(wordList);
        }
        return parsedQuery;
    }
}