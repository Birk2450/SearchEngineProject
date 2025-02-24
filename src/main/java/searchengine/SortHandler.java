package searchengine;

import java.util.List;

/**
 * The {@code SortHandler} class provides functionality to sort a list of pages
 * based on their relevance to a user query using a specified scoring algorithm.
 * <p>
 * It supports multiple scoring methods, allowing users to choose how relevance
 * scores are calculated for pages based on parsed queries.
 * </p>
 */
public class SortHandler {

    /**
     * Constructs a new {@code SortHandler} instance.
     * <p>
     * This constructor does not require any parameters as the class does not
     * have instance-specific fields to initialize.
     * </p>
     */
    public SortHandler() {
        
    }

    /**
     * Sorts a list of pages based on their relevance to the user's query using
     * the selected scoring algorithm.
     *
     * @param pages        The list of pages to sort.
     * @param parsedQuery  The parsed query, represented as a list of term groups.
     *                     Each inner list contains terms that should be matched
     *                     together.
     * @param searchEngine The search engine instance, used to retrieve the
     *                     database.
     * @param algorithm    The user-selected scoring algorithm (e.g., "SIMPLE" or
     *                     "TFIDF").
     * @return A list of pages sorted by relevance based on the chosen algorithm.
     */
    public List<Page> sortResults(List<Page> pages, List<List<String>> parsedQuery, SearchEngine searchEngine,
            String algorithm) {
        ScoringMethod algo = selectScoringMethod(algorithm);
        return sortByAlgorithm(pages, parsedQuery, searchEngine.getDatabase(), algo);
    }

    /**
     * Sorts a list of pages based on their relevance score using the given scoring
     * method.
     *
     * @param listOfPages   The list of pages to sort.
     * @param parsedQuery   The parsed query structure.
     * @param database      The database used for scoring.
     * @param scoringMethod The scoring method to use for ranking the pages.
     * @return A sorted list of pages in descending order of relevance.
     */
    public List<Page> sortByAlgorithm(List<Page> listOfPages, List<List<String>> parsedQuery, Database database,
            ScoringMethod scoringMethod) {
        listOfPages.sort((page1, page2) -> {
            double score1 = calculatePageScore(page1, parsedQuery, database, scoringMethod);
            double score2 = calculatePageScore(page2, parsedQuery, database, scoringMethod);
            return Double.compare(score2, score1);
        });
        return listOfPages;
    }

    /**
     * Calculates the relevance score of a page based on the provided scoring
     * method.
     *
     * @param page          The page for which the score is being calculated.
     * @param parsedQuery   The parsed query structure, organized as groups of
     *                      terms.
     * @param database      The database containing all pages.
     * @param scoringMethod The scoring method used to calculate the page's
     *                      relevance.
     * @return The calculated relevance score for the page.
     */
    public double calculatePageScore(Page page, List<List<String>> parsedQuery, Database database,
            ScoringMethod scoringMethod) {
        double maxGroupScore = 0.0;

        for (List<String> group : parsedQuery) {
            double groupScore = 0.0;

            for (String word : group) {
                groupScore += scoringMethod.calculateScore(word, page, database);
            }
            maxGroupScore = Math.max(maxGroupScore, groupScore);
        }
        return maxGroupScore;
    }

    /**
     * Selects a scoring method based on the user's chosen algorithm.
     *
     * @param algorithm A string representing the user-selected algorithm
     *                  (e.g., "SIMPLE" or "TFIDF").
     * @return A new instance of the corresponding {@link ScoringMethod}
     *         implementation.
     * @throws IllegalArgumentException if the provided algorithm is unknown or
     *                                  unsupported.
     */
    public ScoringMethod selectScoringMethod(String algorithm) {
        switch (algorithm) {
            case "SIMPLE":
                return new SimpleFrequencyScoring();
            case "TFIDF":
                return new TFIDFScoring();
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}