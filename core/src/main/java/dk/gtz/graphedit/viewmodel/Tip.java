package dk.gtz.graphedit.viewmodel;

import java.util.Optional;

public record Tip(String category, String description, Integer imageWidth, Optional<String> image) {
    public Tip(String category, String description) {
        this(category, description, 500, Optional.empty());
    }

    public Tip(String category, String description, String image) {
        this(category, description, 500, Optional.of(image));
    }

    public Tip(String category, String description, Integer imageWidth, String image) {
        this(category, description, imageWidth, Optional.of(image));
    }
}
