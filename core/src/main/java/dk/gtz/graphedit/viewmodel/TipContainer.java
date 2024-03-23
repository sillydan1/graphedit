package dk.gtz.graphedit.viewmodel;

import java.util.List;

public class TipContainer {
    private final List<Tip> tips;

    public TipContainer(List<Tip> tips) {
        this.tips = tips;
        if(this.tips.isEmpty())
            throw new IllegalArgumentException("Tips cannot be empty");
    }

    public Tip get(int index) {
        return tips.get(index % tips.size());
    }

    public void add(Tip tip) {
        tips.add(tip);
    }

    public void merge(TipContainer container) {
        tips.addAll(container.tips);
    }

    public int size() {
        return tips.size();
    }
}
