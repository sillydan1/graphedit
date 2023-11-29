package dk.gtz.graphedit.model.lsp;

public record ModelLanguageServerProgress(
        String token,
        ModelLanguageServerProgressType type,
        String title,
        String message) {}
