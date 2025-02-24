package searchengine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * The {@code Database} class represents a data structure for storing and managing an inverted index 
 * of web pages. It processes web page data from a file, builds an efficient inverted index,
 * and provides methods to query and retrieve information for search engine functionality.
 * 
 * <p>Errors during initialization are logged but not rethrown, ensuring the application can handle 
 * issues gracefully.</p>
 */
public class Database {
    private Map<String, Set<Page>> database;
    private int totalPages; 

    /**
     * Constructs a new {@code Database} instance and initializes the inverted index
     * and page count from the specified file.
     *
     * @param filename the path to the file containing web page data. This file is used
     *                 to build the database and the inverted index.
     * @throws IOException if an error occurs while reading the file.
     */
    public Database(String filename) throws IOException {
        database = new HashMap<>();
        initializePages(filename);
    }

    /**
     * Retrieves the inverted index of the database.
     *
     * @return a {@link Map} where keys are words and values are sets of
     *         {@link Page} objects containing those words.
     */
    public Map<String, Set<Page>> getDatabase() {
        return database;
    }

    /**
     * Counts the number of pages in the database that contain the specified word.
     * This serves as a helper method for TF-IDF ranking.
     *
     * @param word the word to search for in the database.
     * @return the number of pages that contain the specified word.
     */
    public int pagesWithWord(String word) {
        if (database.containsKey(word)) {
            return database.get(word).size();
        } else {
            return 0;
        }
    }

    /**
     * Retrieves the total number of pages in the database.
     * This serves as a helper method for TF-IDF ranking.
     * 
     * @return the total amount of pages in the database.
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Initializes the database of pages and simultaneously builds the inverted index.
     * This method reads a file containing web page data, processes each page to extract
     * its content, and populates a data structure for efficient inverted index-based searching.
     * Each word in the page content is mapped to the set of {@link Page} objects where it appears.
     * 
     * @param filename the path to the file containing the web page data.
     * @throws IOException if an error occurs while reading the file.
     *                     Note: {@code FileNotFoundException} is caught and logged but not rethrown.
     */
    public void initializePages(String filename) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            int lastIndex = lines.size();
            for (int i = lines.size() - 1; i >= 0; --i) {
                if (lines.get(i).startsWith("*PAGE")) {
                    Page page = new Page(lines.subList(i, lastIndex));
                    totalPages++; 

                    for (String word : page.getPage()) {

                        database.putIfAbsent(word, new HashSet<>());

                        if (!database.get(word).contains(page)) { 
                            database.get(word).add(page);
                        }
                    }
                    lastIndex = i;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
