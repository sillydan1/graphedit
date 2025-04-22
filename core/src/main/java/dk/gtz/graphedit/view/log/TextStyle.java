package dk.gtz.graphedit.view.log;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import javafx.scene.paint.Color;

/**
 * Holds information about the style of a text fragment.
 */
public class TextStyle {
	/**
	 * A text style with only default values
	 */
	public static final TextStyle EMPTY = new TextStyle();

	/**
	 * A text style using white text color
	 */
	public static final TextStyle WHITE = new TextStyle().setTextColor("white");

	/**
	 * Create a new text style with a random text color
	 * 
	 * @return A new randomly colored text style
	 */
	public static TextStyle randomTextColor() {
		var r = new Random();
		var c = Color.color(
				r.nextDouble(),
				r.nextDouble(),
				r.nextDouble());
		return EMPTY.setTextColor(cssColor(c));
	}

	/**
	 * Create a new text style with a specified text color
	 * 
	 * @param color The color to use
	 * @return A new colored text style
	 */
	public static String cssColor(Color color) {
		int red = (int) (color.getRed() * 255);
		int green = (int) (color.getGreen() * 255);
		int blue = (int) (color.getBlue() * 255);
		return "rgb(" + red + ", " + green + ", " + blue + ")";
	}

	final Optional<Boolean> bold;
	final Optional<Boolean> italic;
	final Optional<Boolean> underline;
	final Optional<Boolean> strikethrough;
	final Optional<Integer> fontSize;
	final Optional<String> fontFamily;
	final Optional<String> textColor;
	final Optional<String> backgroundColor;

	/**
	 * Construct a new text style instance
	 */
	public TextStyle() {
		this(Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty());
	}

	/**
	 * Construct a new text style istance
	 * 
	 * @param bold            Should the text be bold
	 * @param italic          Should the text be italic
	 * @param underline       Should the text be underlined
	 * @param strikethrough   Should the text be strikethrough
	 * @param fontSize        Text size of the text style
	 * @param fontFamily      The font to use
	 * @param textColor       The css color to use
	 * @param backgroundColor The css background color to use
	 */
	public TextStyle(Optional<Boolean> bold,
			Optional<Boolean> italic,
			Optional<Boolean> underline,
			Optional<Boolean> strikethrough,
			Optional<Integer> fontSize,
			Optional<String> fontFamily,
			Optional<String> textColor,
			Optional<String> backgroundColor) {
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
		this.strikethrough = strikethrough;
		this.fontSize = fontSize;
		this.fontFamily = fontFamily;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
	}

	@Override
	public int hashCode() {
		return Objects.hash(bold, italic, underline, strikethrough,
				fontSize, fontFamily, textColor, backgroundColor);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TextStyle that)
			return Objects.equals(this.bold, that.bold) &&
					Objects.equals(this.italic, that.italic) &&
					Objects.equals(this.underline, that.underline) &&
					Objects.equals(this.strikethrough, that.strikethrough) &&
					Objects.equals(this.fontSize, that.fontSize) &&
					Objects.equals(this.fontFamily, that.fontFamily) &&
					Objects.equals(this.textColor, that.textColor) &&
					Objects.equals(this.backgroundColor, that.backgroundColor);
		return false;
	}

	@Override
	public String toString() {
		var styles = new ArrayList<String>();
		bold.ifPresent(b -> styles.add(b.toString()));
		italic.ifPresent(i -> styles.add(i.toString()));
		underline.ifPresent(u -> styles.add(u.toString()));
		strikethrough.ifPresent(s -> styles.add(s.toString()));
		fontSize.ifPresent(s -> styles.add(s.toString()));
		fontFamily.ifPresent(f -> styles.add(f.toString()));
		textColor.ifPresent(c -> styles.add(c.toString()));
		backgroundColor.ifPresent(b -> styles.add(b.toString()));
		return String.join(",", styles);
	}

	/**
	 * Convert the current settings into one big javafx css expression
	 * 
	 * @return A new css expression string
	 */
	public String toCss() {
		var sb = new StringBuilder();
		bold.ifPresent(b -> sb.append("-fx-font-weight: ").append(b ? "bold" : "normal").append(";"));
		italic.ifPresent(b -> sb.append("-fx-font-style: ").append(b ? "italic" : "normal").append(";"));
		underline.ifPresent(b -> sb.append("-fx-underline: ").append(b ? "true" : "false").append(";"));
		strikethrough.ifPresent(b -> sb.append("-fx-strikethrough: ").append(b ? "true" : "false").append(";"));
		fontSize.ifPresent(integer -> sb.append("-fx-font-size: ").append(integer).append("pt;"));
		fontFamily.ifPresent(s -> sb.append("-fx-font-family: ").append(s).append(";"));
		textColor.ifPresent(color -> sb.append("-fx-fill: ").append(color).append(";"));
		backgroundColor.ifPresent(color -> sb.append("-rtfx-background-color: ").append(color).append(";"));
		return sb.toString();
	}

	/**
	 * Mix / merge a different text style with this one.
	 * 
	 * @param mixin The other text style to merge with
	 * @return A new merged / mixed text style
	 */
	public TextStyle updateWith(TextStyle mixin) {
		return new TextStyle(
				mixin.bold.isPresent() ? mixin.bold : bold,
				mixin.italic.isPresent() ? mixin.italic : italic,
				mixin.underline.isPresent() ? mixin.underline : underline,
				mixin.strikethrough.isPresent() ? mixin.strikethrough : strikethrough,
				mixin.fontSize.isPresent() ? mixin.fontSize : fontSize,
				mixin.fontFamily.isPresent() ? mixin.fontFamily : fontFamily,
				mixin.textColor.isPresent() ? mixin.textColor : textColor,
				mixin.backgroundColor.isPresent() ? mixin.backgroundColor : backgroundColor);
	}

	/**
	 * Set the bold attribute of the text style
	 * 
	 * @param bold Whether or not the text should be bold
	 * @return a new text style
	 */
	public TextStyle setBold(boolean bold) {
		return new TextStyle(Optional.of(bold), italic, underline, strikethrough, fontSize, fontFamily,
				textColor, backgroundColor);
	}

	/**
	 * Set the italic attribute of the text style
	 * 
	 * @param italic Whether or not the text should be italic
	 * @return a new text style
	 */
	public TextStyle setItalic(boolean italic) {
		return new TextStyle(bold, Optional.of(italic), underline, strikethrough, fontSize, fontFamily,
				textColor, backgroundColor);
	}

	/**
	 * Set the underline attribute of the text style
	 * 
	 * @param underline Whether or not the text should be underlined
	 * @return a new text style
	 */
	public TextStyle setUnderline(boolean underline) {
		return new TextStyle(bold, italic, Optional.of(underline), strikethrough, fontSize, fontFamily,
				textColor, backgroundColor);
	}

	/**
	 * Set the strikethrough attribute of the text style
	 * 
	 * @param strikethrough Whether or not the text should be strikethrough
	 * @return a new text style
	 */
	public TextStyle setStrikethrough(boolean strikethrough) {
		return new TextStyle(bold, italic, underline, Optional.of(strikethrough), fontSize, fontFamily,
				textColor, backgroundColor);
	}

	/**
	 * Set the font size attribute of the text style
	 * 
	 * @param fontSize The size (in pt's) of the text
	 * @return a new text style
	 */
	public TextStyle setFontSize(int fontSize) {
		return new TextStyle(bold, italic, underline, strikethrough, Optional.of(fontSize), fontFamily,
				textColor, backgroundColor);
	}

	/**
	 * Set the font of the text style
	 * 
	 * @param fontFamily The font to use
	 * @return a new text style
	 */
	public TextStyle setFontFamily(String fontFamily) {
		return new TextStyle(bold, italic, underline, strikethrough, fontSize, Optional.of(fontFamily),
				textColor, backgroundColor);
	}

	/**
	 * Set the text color attribute of the text style
	 * 
	 * @param textColor The new color of the text
	 * @return a new text style
	 */
	public TextStyle setTextColor(String textColor) {
		return new TextStyle(bold, italic, underline, strikethrough, fontSize, fontFamily,
				Optional.of(textColor), backgroundColor);
	}

	/**
	 * Set the text color attribute of the text style using hex-codes
	 * 
	 * @param webColor Hexcode formatted color of the text
	 * @return a new text style
	 */
	public TextStyle setTextColorWeb(String webColor) {
		return setTextColor(webColor);
	}

	/**
	 * Set the text background color attribute of the text style
	 * 
	 * @param backgroundColor The new background color of the text
	 * @return a new text style
	 */
	public TextStyle setBackgroundColor(String backgroundColor) {
		return new TextStyle(bold, italic, underline, strikethrough, fontSize, fontFamily, textColor,
				Optional.of(backgroundColor));
	}
}
