package searchengine;

/**
 * Implementation of the {@link ScoringMethod} interface using the TF-IDF
 * (Term Frequency-Inverse Document Frequency) algorithm.
 * <p>
 * This class calculates the relevance score of a word in the context of a
 * specific page and the overall database. The score is determined by:
 * </p>
 * <ul>
 * <li><strong>Term Frequency (TF):</strong> How often the word appears on the
 * page, normalized by
 * the total words on the page.</li>
 * <li><strong>Inverse Document Frequency (IDF):</strong> A measure of how
 * unique the word is
 * across all pages in the database.</li>
 * </ul>
 * <p>
 * The final TF-IDF score is the product of TF and IDF, providing a measure of a
 * term's importance
 * in the page relative to the entire database.
 * </p>
 */
public class TFIDFScoring implements ScoringMethod {

    /**
     * Constructs a new {@code TFIDFScoring} instance.
     * <p>
     * This constructor does not require any parameters, as the class does not
     * have instance-specific fields to initialize.
     * </p>
     */
    public TFIDFScoring() {

    }

    /**
     * Calculates the TF-IDF score for a given word in the context of a specific
     * page and the database.
     * <p>
     * The score is calculated using the following formulas:
     * </p>
     * <ul>
     * <li><strong>TF:</strong> (Frequency of the word on the page) / (Total words
     * on the page)</li>
     * <li><strong>IDF:</strong> log(Total number of pages / Number of pages
     * containing the word)</li>
     * <li><strong>TF-IDF:</strong> TF * IDF</li>
     * </ul>
     * <p>
     * If the word does not appear in any page, the IDF component will be 0,
     * resulting in a TF-IDF score of 0.
     * </p>
     * 
     * @param word     The word for which the score is being calculated.
     * @param page     The page (document) where the word is being evaluated.
     * @param database The database containing all pages in the corpus.
     * @return The TF-IDF score for the word on the specified page. If the word does
     *         not exist, the score is 0.
     */
    @Override
    public double calculateScore(String word, Page page, Database database) {
        // Total number of pages in the database
        int totalPages = database.getTotalPages();

        // Number of pages containing the word
        int pagesWithWord = database.pagesWithWord(word);

        // Calculate Term Frequency (TF)
        double tf = (double) page.getWordFrequency(word) / page.getTotalWords();

        // Calculate Inverse Document Frequency (IDF)
        double idf = pagesWithWord == 0 ? 0 : Math.log((double) totalPages / pagesWithWord);

        // Return the product of TF and IDF
        return tf * idf;
    }
}