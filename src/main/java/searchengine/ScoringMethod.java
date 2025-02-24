package searchengine;

/**
 * Interface for scoring methods.
 * Provides a method for calculating a score for a word on a specific page,
 * based on data from the database.
 */
public interface ScoringMethod {
    /**
     * Calculates a score for a word on a given page relative to the database.
     * 
     * @param word     The word to score.
     * @param page     The page on which the score is calculated.
     * @param database The database containing all pages.
     * @return A double value representing the score.
     */
    double calculateScore(String word, Page page, Database database);
}
