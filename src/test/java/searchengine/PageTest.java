package searchengine;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Page} class.
 * <p>
 * This test class verifies the functionality of the {@link Page} class,
 * including:
 * </p>
 * <ul>
 * <li>Retrieving metadata (URL and title).</li>
 * <li>Calculating word frequency and total word count.</li>
 * <li>Checking if a page contains a specific search term.</li>
 * <li>Overridden {@code equals()} method behavior.</li>
 * </ul>
 */
class PageTest {

    private static final Path TEST_FILE_PATH = Paths.get("src/test/java/searchengine/resources/test-file.txt");
    private List<String> fileLines;

    /**
     * Loads the test file before each test.
     *
     * @throws IOException if the test file cannot be read.
     */
    @BeforeEach
    public void loadTestFile() throws IOException {
        fileLines = Files.readAllLines(TEST_FILE_PATH);
    }

    /**
     * Tests that the URL and title are correctly retrieved from the page.
     */
    @Test
    public void testPageMetadata() {
        Page page = new Page(fileLines.subList(0, 4));

        assertEquals("http://page1.com", page.getUrl(), "URL should match the expected value.");
        assertEquals("title1", page.getTitle(), "Title should match the expected value.");
    }

    /**
     * Tests the {@code getWordFrequency} method for calculating the frequency of
     * specific words.
     * Verifies proper handling of valid words, null, empty input, and ignored
     * lines.
     */
    @Test
    public void testGetWordFrequency() {
        Page page = new Page(Arrays.asList(
                null,
                "*PAGE: URL",
                "title",
                "word1",
                "word2",
                "word1",
                "",
                "WORD3"));

        assertEquals(2, page.getWordFrequency("word1"), "'word1' frequency should be 2.");
        assertEquals(0, page.getWordFrequency("word4"), "'word4' is not present, so frequency should be 0.");
        assertEquals(0, page.getWordFrequency(null), "Null input should return 0.");
        assertEquals(0, page.getWordFrequency(""), "Empty input should return 0.");
        assertEquals(0, page.getWordFrequency("*PAGE:"), "Ignored lines should return 0.");
        assertEquals(0, page.getWordFrequency("title"), "Ignored title lines should return 0.");
    }

    /**
     * Tests the {@code getTotalWords} method for counting valid words on a page.
     * Verifies proper handling of null lines, ignored lines, and empty pages.
     */
    @Test
    public void testGetTotalWords() {
        Page page1 = new Page(Arrays.asList(
                null,
                "*PAGE: 1",
                "title",
                "word1",
                "word2",
                ""));
        assertEquals(2, page1.getTotalWords(), "Total words should count only valid lines, ignoring others.");

        Page page2 = new Page(Arrays.asList(null, null, null));
        assertEquals(0, page2.getTotalWords(), "Total words should be 0 for a page with only null lines.");

        Page page3 = new Page(Arrays.asList("*PAGE: 1", "title", "*PAGE: 2"));
        assertEquals(0, page3.getTotalWords(), "Total words should be 0 for a page with only ignored lines.");

        Page page5 = new Page(Collections.emptyList());
        assertEquals(0, page5.getTotalWords(), "Total words should be 0 for an empty page.");
    }

    /**
     * Tests the {@code containsSearchterm} method for checking if a page contains a
     * specific search term.
     * Verifies proper behavior for pages with valid content, empty pages, and
     * invalid input.
     */
    @Test
    public void testContainsSearchterm() {
        Page page1 = new Page(Arrays.asList("URL", "title", "word1", "word2, word3"));
        assertTrue(page1.containsSearchterm("word1"), "Page should contain 'word1' and have more than 2 lines.");

        Page page2 = new Page(Arrays.asList("URL", "title", "word2", "content"));
        assertFalse(page2.containsSearchterm("word1"), "Page does not contain 'word1', so it should return false.");

        Page page3 = new Page(Arrays.asList("URL", "word1"));
        assertFalse(page3.containsSearchterm("word1"), "Page has 2 or fewer lines, so it should return false.");

        Page page4 = new Page(Arrays.asList("URL", "title"));
        assertFalse(page4.containsSearchterm("word1"), "Page does not contain 'word1' and has 2 or fewer lines.");

        Page page5 = new Page(Collections.emptyList());
        assertFalse(page5.containsSearchterm("word1"), "Empty page should not contain any search terms.");

        Page page6 = new Page(Arrays.asList("URL", "title", "word1", "content"));
        assertFalse(page6.containsSearchterm(null), "Null search term should return false.");
        assertFalse(page6.containsSearchterm(""), "Empty search term should return false.");
    }

    /**
     * Tests the overridden {@code equals} method for verifying object equality.
     * Ensures compliance with reflexivity, symmetry, transitivity, and null-check
     * rules.
     */
    @Test
    public void testEquals() {
        Page page1 = new Page(Collections.singletonList("Content A"));
        Page page2 = new Page(Collections.singletonList("Content B"));
        Page pageWithSameId = clonePageWithSameId(page1);

        assertTrue(page1.equals(page1), "A page should equal itself.");
        assertTrue(page1.equals(pageWithSameId), "Pages with the same ID should be equal.");
        assertTrue(pageWithSameId.equals(page1), "Symmetry failed.");

        Page page3 = clonePageWithSameId(page1);
        assertTrue(page1.equals(pageWithSameId) && pageWithSameId.equals(page3) && page1.equals(page3),
                "Transitivity failed.");

        assertFalse(page1.equals(null), "Page should not equal null.");
        assertFalse(page1.equals("NotAPage"), "Page should not equal an object of a different class.");
        assertFalse(page1.equals(page2), "Pages with different IDs should not be equal.");
    }

    /**
     * Helper method to simulate a {@link Page} object with the same ID for testing.
     *
     * @param original The original {@link Page} object to clone.
     * @return A new {@link Page} object with the same ID as the original.
     */
    private Page clonePageWithSameId(Page original) {
        Page cloned = new Page(Collections.emptyList());
        setPageId(cloned, original.getId());
        return cloned;
    }

    /**
     * Sets the private {@code id} field of a {@link Page} object for testing
     * purposes using reflection.
     *
     * @param page The {@link Page} object whose ID needs to be set.
     * @param id   The ID value to set.
     */
    private void setPageId(Page page, int id) {
        try {
            java.lang.reflect.Field field = Page.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(page, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Unable to set page ID for testing", e);
        }
    }
}