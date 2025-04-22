package dk.gtz.graphedit.viewmodel;

import java.util.List;

/**
 * Represents a container of tips that can be shown to the user via the
 * Tip-of-the-day system.
 */
public class TipContainer {
	private final List<Tip> tips;

	/**
	 * Creates a new tip container with the given tips.
	 * 
	 * @param tips A list of tips
	 */
	public TipContainer(List<Tip> tips) {
		this.tips = tips;
		if (this.tips.isEmpty())
			throw new IllegalArgumentException("Tips cannot be empty");
	}

	/**
	 * Gets the tip at the given index.
	 * 
	 * @param index The index of the tip to get
	 * @return The tip at the given index
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 */
	public Tip get(int index) {
		return tips.get(index % tips.size());
	}

	/**
	 * Adds a tip to the container.
	 * 
	 * @param tip The tip to add
	 */
	public void add(Tip tip) {
		tips.add(tip);
	}

	/**
	 * Merges the given container with this container.
	 * All tips from the provided container will be appended to this one.
	 * 
	 * @param container The container to merge with
	 */
	public void merge(TipContainer container) {
		tips.addAll(container.tips);
	}

	/**
	 * Gets the number of tips in the container.
	 * 
	 * @return The number of tips in the container
	 */
	public int size() {
		return tips.size();
	}
}
