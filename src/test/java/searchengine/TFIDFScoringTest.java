package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Unit tests for the {@link TFIDFScoring} class.
 * <p>
 * This test class validates the correctness of the TF-IDF scoring mechanism,
 * which calculates
 * the relevance of a word in the context of a page and the entire database.
 * </p>
 */
public class TFIDFScoringTest {

    private TFIDFScoring tfidfScoring;
    private Page page1, page4;
    private Database database;

    private static final Path TEST_FILE_PATH = Paths.get("src/test/java/searchengine/resources/test-file.txt");

    /**
     * Sets up the test environment by initializing the {@link TFIDFScoring}
     * instance
     * and loading the test database.
     * <p>
     * Extracts specific pages (page1 and page4) from the database for testing.
     * </p>
     *
     * @throws IOException if the test file cannot be read or the database fails to
     *                     initialize.
     */
    @BeforeEach
    public void setup() throws IOException {
        database = new Database(TEST_FILE_PATH.toAbsolutePath().toString());
        tfidfScoring = new TFIDFScoring();

        for (Set<Page> pages : database.getDatabase().values()) {
            for (Page page : pages) {
                if (page.getUrl().equals("http://page1.com")) {
                    page1 = page;
                } else if (page.getUrl().equals("http://page4.com")) {
                    page4 = page;
                }
            }
        }
    }

    /**
     * Tests the TF-IDF calculation for a specific word on page1.
     * <p>
     * Validates that the TF-IDF score matches the expected value based on the
     * formula:
     * 
     * <pre>
     * TF = (frequency of the word on the page) / (total words on the page)
     * IDF = log(total number of pages / number of pages containing the word)
     * TF-IDF = TF * IDF
     * </pre>
     * </p>
     */
    @Test
    public void testCalculateScoreForWord1OnPage1() {
        String word = "word1";
        double actualScore = tfidfScoring.calculateScore(word, page1, database);
        int totalPages = database.getTotalPages();
        int pagesWithWord = database.pagesWithWord(word);
        double tf = (double) page1.getWordFrequency(word) / page1.getTotalWords();
        double idf = pagesWithWord == 0 ? 0 : Math.log((double) totalPages / pagesWithWord);
        double expectedScore = tf * idf;

        assertEquals(expectedScore, actualScore, 0.0001, "TF-IDF score for 'word1' on page1 should match.");
    }

    /**
     * Tests the TF-IDF calculation for a specific word on page4.
     * <p>
     * Validates that the TF-IDF score matches the expected value based on the
     * TF-IDF formula.
     * </p>
     */
    @Test
    public void testCalculateScoreForWord3OnPage4() {
        String word = "word3";
        double actualScore = tfidfScoring.calculateScore(word, page4, database);
        int totalPages = database.getTotalPages();
        int pagesWithWord = database.pagesWithWord(word);
        double tf = (double) page4.getWordFrequency(word) / page4.getTotalWords();
        double idf = pagesWithWord == 0 ? 0 : Math.log((double) totalPages / pagesWithWord);
        double expectedScore = tf * idf;
        assertEquals(expectedScore, actualScore, 0.0001, "TF-IDF score for 'word3' on page4 should match.");
    }

    /**
     * Tests the TF-IDF calculation for a word that does not exist in the database.
     * <p>
     * Ensures that the score is {@code 0.0}, as the word does not appear on the
     * page or in the database.
     * </p>
     */
    @Test
    public void testCalculateScoreForNonExistentWord() {
        String word = "nonexistentword";
        double actualScore = tfidfScoring.calculateScore(word, page1, database);
        double expectedScore = 0.0;
        assertEquals(expectedScore, actualScore, 0.0001, "TF-IDF score for a non-existent word should be 0.");
    }
}