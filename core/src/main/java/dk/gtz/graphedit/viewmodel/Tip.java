package dk.gtz.graphedit.viewmodel;

import java.util.Optional;

public record Tip(String category, String description, Optional<String> image) {
    public Tip(String category, String description) {
        this(category, description, Optional.empty());
    }
}
