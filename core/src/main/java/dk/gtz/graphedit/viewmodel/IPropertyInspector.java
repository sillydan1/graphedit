package dk.gtz.graphedit.viewmodel;

/**
 * Interface for classes that can inspect properties of {@link IInspectable}.
 */
public interface IPropertyInspector {
    void inspect(IInspectable inspectable, String propertyName);
}

