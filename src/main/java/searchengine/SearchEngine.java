package searchengine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The {@code SearchEngine} class represents a search engine that interacts with
 * a {@link Database} to
 * retrieve search results based on a provided search term.
 * It also provides functionality to start a {@link WebServer} for frontend
 * interaction.
 */
public class SearchEngine {
    private Database database;

    /**
     * Constructs a new {@code SearchEngine} instance and initializes it with the
     * specified database file.
     * 
     * @param databaseFile the file path to the database file used to initialize the
     *                     {@link Database}.
     * @throws IOException if an I/O error occurs while reading the database file.
     */
    public SearchEngine(String databaseFile) throws IOException {
        this.database = new Database(databaseFile);
    }

    /**
     * Searches the database's inverted index for pages containing the specified
     * word.
     * 
     * @param word the term to search for in the database.
     * @return a set of {@link Page} objects that contain the specified word. If no
     *         pages are found,
     *         an empty set is returned.
     */
    public Set<Page> accessDatabase(String word) {
        return database.getDatabase().getOrDefault(word, Collections.emptySet());
    }

    /**
     * Retrieves the {@link Database} object used by this search engine.
     *
     * @return the {@link Database} object.
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Performs a search operation based on the provided parsed query.
     * <p>
     * This method processes a query parsed into groups of terms and retrieves pages
     * that
     * match the criteria specified by the {@code andIsTrue} parameter. The query
     * can be
     * interpreted using either an "AND" logic (intersection of results) or an "OR"
     * logic
     * (union of results) depending on the value of {@code andIsTrue}.
     * </p>
     *
     * @param parsedQuery a list of term groups, where each group is represented as
     *                    a list of strings.
     *                    Each group contains terms that should be matched together.
     * @param andIsTrue   a boolean flag that determines the search logic. If
     *                    {@code true},
     *                    the method uses "AND" logic to find pages matching all
     *                    term groups.
     *                    If {@code false}, it uses "OR" logic to find pages
     *                    matching any term group.
     * @return a list of {@code Page} objects representing the search results. The
     *         list contains
     *         unique pages that match the query, determined by the specified logic.
     * @throws IllegalArgumentException if {@code parsedQuery} is {@code null} or
     *                                  empty.
     */
    public List<Page> search(List<List<String>> parsedQuery, boolean andIsTrue) {
        if (parsedQuery == null || parsedQuery.isEmpty()) {
            throw new IllegalArgumentException("Query cannot be null or empty");
        }

        Set<Page> matchedPages = new HashSet<>();
        if (andIsTrue) {
            matchedPages = findMatchingPages(parsedQuery.get(0));
            for (int i = 1; i < parsedQuery.size(); i++) {
                Set<Page> groupMatchingPages = findMatchingPages(parsedQuery.get(i));
                matchedPages.retainAll(groupMatchingPages);
            }
        } else {
            for (List<String> group : parsedQuery) {
                Set<Page> groupMatchingPages = findMatchingPages(group);
                matchedPages.addAll(groupMatchingPages);
            }
        }
        return new ArrayList<>(matchedPages);
    }

    /**
     * Helper method to find pages matching all words in a group.
     *
     * @param group A group of words to search for.
     * @return A set of pages that match all words in the group.
     */
    public Set<Page> findMatchingPages(List<String> group) {
        if (group.isEmpty()) {
            return Set.of();
        }
        Set<Page> commonPages = new HashSet<>(accessDatabase(group.get(0)));
        for (int i = 1; i < group.size(); i++) {
            Set<Page> wordPages = new HashSet<>(accessDatabase(group.get(i)));
            commonPages.retainAll(wordPages);
            if (commonPages.isEmpty()) {
                break;
            }
        }
        return commonPages;
    }
}
