package dk.gtz.graphedit.view.log;

/**
 * Class representing a clickable textual link.
 */
public class Hyperlink {
    private final String originalDisplayedText, displayedText, link;

    Hyperlink(String originalDisplayedText, String displayedText, String link) {
        this.originalDisplayedText = originalDisplayedText;
        this.displayedText = displayedText;
        this.link = link;
    }

    /**
     * Checks if the hyperlink display is empty or now
     * @return true if {@link Hyperlink#length} returns 0, false otherwise
     */
    public boolean isEmpty() {
        return length() <= 0;
    }

    /**
     * Checks if this hyperlink links to the same base link as another one.
     * @param other The other hyperlink to check
     * @return true if the other hyperlink is refering to the same link
     */
    public boolean shareSameAncestor(Hyperlink other) {
        return link.equals(other.link);
    }

    /**
     * Get the length of the displayed text in the hyperlink
     * @return The string-length of the displayed hyperlink text
     */
    public int length() {
        return displayedText.length();
    }

    /**
     * Get the displayed character at some index
     * @param index of the character to get
     * @return The char value at the specified index
     * @throws IndexOutOfBoundsException if the index argument is negative or larger than the displayed text length
     */
    public char charAt(int index) {
        return isEmpty() ? '\0' : displayedText.charAt(index);
    }

    /**
     * Get the original text
     * @return String with the original text
     */
    public String getOriginalDisplayedText() {
        return originalDisplayedText;
    }

    /**
     * Get the displayed text
     * @return String with the displayed text
     */
    public String getDisplayedText() {
        return displayedText;
    }

    /**
     * Get the link component of the hyperlink
     * @return String with the link
     */
    public String getLink() {
        return link;
    }

    /**
     * Create a new hyperlink where the displayed text is a subsequence of this hyperlink's displayed text
     * @param start the beginning index, inclusive
     * @param end the ending index, exclusive
     * @return A new hyperlink with the same original text and link, but a subsequence for the displayed text
     */
    public Hyperlink subSequence(int start, int end) {
        return new Hyperlink(originalDisplayedText, displayedText.substring(start, end), link);
    }

    /**
     * Create a new hyperlink where the displayed text is a subsequence of this hyperlink's displayed text
     * @param start the beginning index, inclusive
     * @return A new hyperlink with the same original text and link, but a subsequence for the displayed text
     */
    public Hyperlink subSequence(int start) {
        return new Hyperlink(originalDisplayedText, displayedText.substring(start), link);
    }

    /**
     * Create a new hyperlink where the displayed text is replaced with a provided string value
     * @param text The new displayed text
     * @return A new hyperlink with the same original text and link, but an updated displayed text
     */
    public Hyperlink mapDisplayedText(String text) {
        return new Hyperlink(originalDisplayedText, text, link);
    }

    @Override
    public String toString() {
        return isEmpty()
                ? String.format("EmptyHyperlink[original=%s link=%s]", originalDisplayedText, link)
                : String.format("RealHyperlink[original=%s displayedText=%s, link=%s]",
                originalDisplayedText, displayedText, link);
    }
}
