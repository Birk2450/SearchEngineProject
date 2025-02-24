package searchengine;

import java.util.List;
import java.util.Objects;

/**
 * The {@code Page} class represents a web page object with a unique ID, content, and metadata
 * such as the URL and title. The content is stored as a list of strings, where the first line 
 * is expected to contain the URL and the second line is expected to contain the title.
 * <p>
 * This class provides methods for extracting information about the page and calculating 
 * statistics used in ranking algorithms like TF-IDF.
 * </p>
 */
public class Page {
    private static int nextPageId = 1;
    private List<String> page;
    private int id;

    /**
     * Constructs a new {@code Page} instance with the specified content and assigns a unique ID.
     *
     * @param page the list of strings representing the content of the page. The first line is
     *             expected to contain the URL, and the second line is expected to contain the title.
     */
    public Page(List<String> page) {
        this.page = page;
        id = nextPageId++;
    }

    /**
     * Retrieves the unique ID of the page.
     *
     * @return the page ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the content of the page.
     *
     * @return the list of strings representing the content of the page.
     */
    public List<String> getPage() {
        return page;
    }

    /**
     * Retrieves the URL of the page, which is expected to be on the first line of the content.
     *
     * @return the URL of the page.
     */
    public String getUrl() {
        return page.get(0).substring(6);
    }

    /**
     * Retrieves the title of the page, which is expected to be on the second line of the content.
     *
     * @return the title of the page.
     */
    public String getTitle() {
        return page.get(1);
    }

    /**
     * Calculates the frequency of the specified word in the page content.
     *
     * @param word the word to count in the page content.
     * @return the frequency of the specified word. Returns 0 if the word is null or empty.
     */
    public int getWordFrequency(String word) {
        if (word == null || word.isEmpty()) {
            return 0;
        }

        int frequency = 0;
        for (String line : page) {
            if (line != null && !line.isEmpty() && !line.startsWith("*PAGE:") && !line.startsWith("title")) {
                if (word.equalsIgnoreCase(line)) {
                    frequency++;
                }
            }
        }
        return frequency;
    }

    /**
     * Calculates the total number of words in the page content.
     *
     * @return the total number of words in the page.
     */
    public int getTotalWords() {
        int totalWords = 0;
        for (String line : page) {
            if (line != null && !line.isEmpty() && !line.startsWith("*PAGE:") && !line.startsWith("title")) {
                totalWords++;
            }
        }
        return totalWords;
    }

    /**
     * Checks if the page content contains the specified search term.
     *
     * @param searchterm the search term to look for.
     * @return {@code true} if the page contains the search term and has content beyond the metadata; 
     *         {@code false} otherwise.
     */
    public boolean containsSearchterm(String searchterm) {
        if (page.contains(searchterm) && page.size() > 2) {
            return true;
        }
        return false;
    }

    /**
     * Compares this page with the specified object for equality. Two pages are considered equal if
     * their IDs are the same, regardless of whether they are the same instance in memory.
     *
     * @param o the reference object with which to compare.
     * @return {@code true} if the specified object is equal to this page; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true; 
        if (o == null || getClass() != o.getClass())
            return false; 
        Page page = (Page) o; 
        return id == page.id; 
    }

    /**
     * Returns the hash code for this page, which is based on its unique ID.
     *
     * @return the hash code of the page.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id); 
    }

}
