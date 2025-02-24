package searchengine;

/**
 * Implementation of the {@link ScoringMethod} interface using simple frequency
 * scoring.
 */
public class SimpleFrequencyScoring implements ScoringMethod {

    /**
     * Constructs a new {@code SimpleFrequencyScoring} instance.
     * <p>
     * This class does not require any specific initialization.
     * </p>
     */
    public SimpleFrequencyScoring() {

    }

    /**
     * Calculates the score of a word on a page using its frequency.
     *
     * @param word     the word to score.
     * @param page     the page where the word's frequency is evaluated.
     * @param database the database containing the page.
     * @return the frequency of the word on the page.
     */
    @Override
    public double calculateScore(String word, Page page, Database database) {
        return page.getWordFrequency(word);
    }
}
