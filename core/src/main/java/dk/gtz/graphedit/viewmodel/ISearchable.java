package dk.gtz.graphedit.viewmodel;

import java.util.List;

/**
 * Interface for classes that can be searched using the graphedit search functionality
 */
public interface ISearchable {
    /**
     * Get a list of strings that should be part of the search
     * @return a list of strings
     */
    List<String> getSearchValues();
}

