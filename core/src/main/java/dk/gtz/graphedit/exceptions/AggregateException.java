package dk.gtz.graphedit.exceptions;

import java.util.List;

public class AggregateException extends RuntimeException {
    private final List<Exception> causes;

    public AggregateException(List<Exception> causes) {
        super("Errors occurred: "+causes.size());
        this.causes = causes;
    }

    public List<Exception> getCauses() {
        return causes;
    }

    @Override
    public String getMessage() {
        var sb = new StringBuilder();
        for(var c : causes)
            sb.append(c.getMessage()).append("\n");
        return sb.toString();
    }
}

