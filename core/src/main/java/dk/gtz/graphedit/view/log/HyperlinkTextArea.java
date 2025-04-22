package dk.gtz.graphedit.view.log;

import javafx.geometry.VPos;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.richtext.model.TextOps;
import org.reactfx.util.Either;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Richtext Textarea implementation that can contain {@link Hyperlink}s.
 * Styled with {@link TextStyle}.
 */
public class HyperlinkTextArea extends GenericStyledArea<Void, Either<String, Hyperlink>, TextStyle> {
	private static final TextOps<String, TextStyle> STYLED_TEXT_OPS = SegmentOps.styledTextOps();
	private static final HyperlinkOps<TextStyle> HYPERLINK_OPS = new HyperlinkOps<>();
	private static final TextOps<Either<String, Hyperlink>, TextStyle> EITHER_OPS = STYLED_TEXT_OPS
			._or(HYPERLINK_OPS, (s1, s2) -> Optional.empty());

	/**
	 * Construct a new instance
	 * 
	 * @param showLink     Callback function for when a {@link Hyperlink} is clicked
	 * @param styleClasses Optionally extra css styleclasses
	 */
	public HyperlinkTextArea(Consumer<Hyperlink> showLink, String... styleClasses) {
		super(null,
				(t, p) -> {
				},
				TextStyle.EMPTY,
				EITHER_OPS,
				e -> e.getSegment().unify(
						text -> createStyledTextNode(t -> {
							t.setText(text);
							t.getStyleClass().addAll(styleClasses);
						}),
						hyperlink -> createStyledTextNode(t -> {
							if (!hyperlink.isEmpty()) {
								t.setText(hyperlink.getDisplayedText());
								t.getStyleClass().addAll(styleClasses);
								t.getStyleClass().add("hyperlink");
								t.setStyle(e.getStyle().toCss());
								t.setOnMouseClicked(ae -> showLink.accept(hyperlink));
							}
						})));
		getStyleClass().add("text-hyperlink-area");
	}

	/**
	 * Add styled text to the text area
	 * 
	 * @param text  The text value to add
	 * @param style The style that the text should have
	 */
	public void append(String text, TextStyle style) {
		replace(getLength(), getLength(), ReadOnlyStyledDocument.fromString(text, null, style, EITHER_OPS));
	}

	/**
	 * Add a styled {@link Hyperlink} to the text area
	 * 
	 * @param displayedText The text to display in the link
	 * @param link          The link value
	 * @param style         The style that the text should have
	 */
	public void appendWithLink(String displayedText, String link, TextStyle style) {
		replaceWithLink(getLength(), getLength(), displayedText, link, style);
	}

	/**
	 * Add a {@link Hyperlink} to the text area
	 * 
	 * @param displayedText The text to display in the link
	 * @param link          The link value
	 */
	public void appendWithLink(String displayedText, String link) {
		appendWithLink(displayedText, link, TextStyle.EMPTY.setTextColor("-color-accent-fg"));
	}

	/**
	 * Replace a segment of the text area with a {@link Hyperlink}
	 * 
	 * @param start         the beginning index, inclusive
	 * @param end           the ending index, exclusive
	 * @param displayedText The text to display in the link
	 * @param link          The link value
	 * @param style         The style that the text should have
	 */
	public void replaceWithLink(int start, int end, String displayedText, String link, TextStyle style) {
		replace(start, end, ReadOnlyStyledDocument.fromSegment(
				Either.right(new Hyperlink(displayedText, displayedText, link)),
				null,
				style,
				EITHER_OPS));
	}

	@Override
	public void replaceText(int start, int end, String text) {
		if (start > 0 && end > 0) {
			var s = Math.max(0, start - 1);
			var e = Math.min(end + 1, getLength() - 1);
			var segList = getDocument().subSequence(s, e).getParagraph(0).getSegments();
			if (!segList.isEmpty() && segList.get(0).isRight()) {
				var link = segList.get(0).getRight().getLink();
				replaceWithLink(start, end, text, link, TextStyle.EMPTY);
				return;
			}
		}
		replace(start, end, ReadOnlyStyledDocument.fromString(text, getParagraphStyleForInsertionAt(start),
				getTextStyleForInsertionAt(start), EITHER_OPS));
	}

	private static TextExt createStyledTextNode(Consumer<TextExt> applySegment) {
		var t = new TextExt();
		t.setTextOrigin(VPos.TOP);
		applySegment.accept(t);
		return t;
	}
}
