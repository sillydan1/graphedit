package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.util.MetadataUtils;

public class ViewModelProjectResourceSnapshot {
    private final ViewModelProjectResource resource;
    private final ModelProjectResource model;

    public ViewModelProjectResourceSnapshot(ViewModelProjectResource resource) {
        this.resource = resource;
        this.model = this.resource.toModel(); // TODO: This might be expensive
    }

    public ViewModelDiff getDiff() {
        var before = new ViewModelProjectResource(model, MetadataUtils.getSyntaxFactory(model.metadata()));
        var after = new ViewModelProjectResource(resource.toModel(), MetadataUtils.getSyntaxFactory(resource.metadata()));
        return ViewModelDiff.compare(before, after);
    }

    public ViewModelProjectResource getResource() {
        return resource;
    }
}
