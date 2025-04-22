package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Stream;

import dk.gtz.graphedit.exceptions.UncomparableException;
import dk.gtz.graphedit.util.MetadataUtils;

/**
 * Represents a difference between two graphs.
 * A diff consists of deletions and additions of both vertices and edges.
 * Note that if e.g. a vertex has changed an attribute it is considered deleted
 * and a new value added.
 */
public class ViewModelDiff {
	private final String syntaxStyle;
	private final List<ViewModelVertex> vertexDeletions;
	private final List<ViewModelEdge> edgeDeletions;
	private final List<ViewModelVertex> vertexAdditions;
	private final List<ViewModelEdge> edgeAdditions;

	private ViewModelDiff(String syntaxStyle) {
		this.syntaxStyle = syntaxStyle;
		vertexDeletions = new ArrayList<>();
		edgeDeletions = new ArrayList<>();
		vertexAdditions = new ArrayList<>();
		edgeAdditions = new ArrayList<>();
	}

	/**
	 * Create a new empty diff
	 * 
	 * @param syntaxStyle The syntax style of the new diff
	 * @return A new empty diff
	 */
	public static ViewModelDiff empty(String syntaxStyle) {
		return new ViewModelDiff(syntaxStyle);
	}

	/**
	 * Get the vertices that should be deleted.
	 * 
	 * @return A list of viewmodel vertices.
	 */
	public List<ViewModelVertex> getVertexDeletions() {
		return vertexDeletions;
	}

	/**
	 * Get the edges that should be deleted.
	 * 
	 * @return A list of viewmodel edges.
	 */
	public List<ViewModelEdge> getEdgeDeletions() {
		return edgeDeletions;
	}

	/**
	 * Get the vertices that should be added.
	 * 
	 * @return A list of viewmodel vertices.
	 */
	public List<ViewModelVertex> getVertexAdditions() {
		return vertexAdditions;
	}

	/**
	 * Get the edges that should be added.
	 * 
	 * @return A list of viewmodel edges.
	 */
	public List<ViewModelEdge> getEdgeAdditions() {
		return edgeAdditions;
	}

	/**
	 * Get the syntax style that this diff operates on.
	 * 
	 * @return The syntax style name.
	 */
	public String getSyntaxStyle() {
		return syntaxStyle;
	}

	/**
	 * Get the combined size of changes.
	 * 
	 * @return The size of changes.
	 */
	public int size() {
		return vertexDeletions.size() +
				edgeDeletions.size() +
				vertexAdditions.size() +
				edgeAdditions.size();
	}

	/**
	 * Checks if the diff has no changes. This is useful for detecting if two graphs
	 * are excactly equal.
	 * 
	 * @return True if no changes are present, otherwise false.
	 */
	public boolean isEmpty() {
		return size() <= 0;
	}

	/**
	 * Checks if two resources are comparable and if a diff can be calculated.
	 * 
	 * @param a The first project resource.
	 * @param b The second project resource.
	 * @return True if the syntaxes are the same, otherwise false.
	 */
	public static boolean areComparable(ViewModelProjectResource a, ViewModelProjectResource b) {
		try {
			raiseIfNotComparable(a, b);
			return true;
		} catch (UncomparableException e) {
			return false;
		}
	}

	private static void raiseIfNotComparable(ViewModelProjectResource a, ViewModelProjectResource b)
			throws UncomparableException {
		var aSyntaxName = a.getSyntaxName();
		var bSyntaxName = b.getSyntaxName();
		if (aSyntaxName.isEmpty() || bSyntaxName.isEmpty())
			throw new UncomparableException(
					"cannot compare graphs because (at least) one of them dont have a syntax name metadata field");
		if (!aSyntaxName.get().equals(bSyntaxName.get()))
			throw new UncomparableException("cannot compare graphs of different syntaxes '%s' and '%s'"
					.formatted(aSyntaxName.get(), bSyntaxName.get()));
	}

	/**
	 * Compare two resources and produce a diff.
	 * 
	 * @param a The first project resource.
	 * @param b The second project resource.
	 * @return A new diff that contains the changes needed to transform a to b
	 * @throws UncomparableException If the two resources aren't comparable.
	 */
	public static ViewModelDiff compare(ViewModelProjectResource a, ViewModelProjectResource b)
			throws UncomparableException {
		raiseIfNotComparable(a, b);
		var aSyntaxName = a.getSyntaxName();
		return compare(aSyntaxName.get(), a.syntax(), b.syntax());
	}

	private static ViewModelDiff compare(String syntaxStyle, ViewModelGraph a, ViewModelGraph b)
			throws UncomparableException {
		var result = new ViewModelDiff(syntaxStyle);
		var sortedA = Stream
				.concat(a.vertices().keySet().stream().sorted(), a.edges().keySet().stream().sorted())
				.toList();
		var sortedB = Stream
				.concat(b.vertices().keySet().stream().sorted(), b.edges().keySet().stream().sorted())
				.toList();
		var n = sortedA.size();
		var m = sortedB.size();
		if (n + m <= 0)
			return result;
		var max = n + m + 1; // 0 (inclusive) to N+M (inclusive)
		var offset = max - 1;
		var stack = new Stack<List<Optional<Integer>>>();
		var v = new ArrayList<Optional<Integer>>(max * 2);
		for (var i = 0; i < max * 2; i++)
			v.add(Optional.empty());
		v.set(1 + offset, Optional.of(0));
		for (var d = 0; d <= max; d++) {
			for (var k = -d; k <= d; k += 2) {
				var ki = k + offset;
				Integer x = null;
				if (k == -d || (k != d && v.get(ki - 1).get() < v.get(ki + 1).get()))
					x = v.get(ki + 1).get();
				else
					x = v.get(ki - 1).get() + 1;
				var y = x - k;
				var outOfRange = x < 0 || x >= n ||
						y < 0 || y >= m;
				while (x < n && y < m && !outOfRange
						&& areSyntaxElementsEqual(sortedA.get(x), sortedB.get(y), a, b)) {
					x++;
					y++;
				}
				v.set(ki, Optional.of(x));
				if (x >= n && y >= m) {
					stack.push(new ArrayList<>(v));
					return getEditScript(sortedA, sortedB, k, offset, stack, result, a, b);
				}
			}
			stack.push(new ArrayList<>(v));
		}
		throw new UncomparableException("no result");
	}

	private static boolean areSyntaxElementsEqual(UUID idA, UUID idB, ViewModelGraph a, ViewModelGraph b) {
		if (a.vertices().containsKey(idA))
			return a.vertices().get(idA).equals(b.vertices().get(idB));
		if (a.edges().containsKey(idA))
			return a.edges().get(idA).equals(b.edges().get(idB));
		return false;
	}

	private static ViewModelDiff getEditScript(List<UUID> a, List<UUID> b, int k, int offset,
			Stack<List<Optional<Integer>>> v, ViewModelDiff acc, ViewModelGraph ga, ViewModelGraph gb)
			throws UncomparableException {
		var vd = v.pop();
		if (v.empty())
			return acc;
		var ki = k + offset;
		var vd_1 = v.peek();
		var x = vd.get(ki).get();
		var y = x - k;
		var xd = x;
		var yd = y;
		do {
			if (vd_1.get(ki + 1).isPresent()) {
				var xd_1v = vd_1.get(ki + 1).get();
				var yd_1v = xd_1v - (k + 1);
				if (xd_1v == xd && yd_1v == yd - 1) {
					acc.addInsert(b.get(yd - 1), gb);
					return getEditScript(a, b, k + 1, offset, v, acc, ga, gb);
				}
			}
			if (vd_1.get(ki - 1).isPresent()) {
				var xd_1h = vd_1.get(ki - 1).get();
				var yd_1h = xd_1h - (k - 1);
				if (xd_1h == xd - 1 && yd_1h == yd) {
					acc.addDelete(a.get(xd - 1), ga);
					return getEditScript(a, b, k - 1, offset, v, acc, ga, gb);
				}
			}
			xd--;
			yd--;
		} while (areSyntaxElementsEqual(a.get(xd), b.get(yd), ga, gb));
		throw new UncomparableException("bad vstack");
	}

	private void addInsert(UUID id, ViewModelGraph b) {
		if (b.vertices().containsKey(id)) {
			var element = b.vertices().get(id);
			vertexAdditions.add(element);
		} else if (b.edges().containsKey(id)) {
			var element = b.edges().get(id);
			edgeAdditions.add(element);
		} else
			throw new RuntimeException(
					"cant add insertion action to diff. Provided id is not present in provided graph");
	}

	private void addDelete(UUID id, ViewModelGraph a) {
		if (a.vertices().containsKey(id)) {
			var element = a.vertices().get(id);
			vertexDeletions.add(element);
		} else if (a.edges().containsKey(id)) {
			var element = a.edges().get(id);
			edgeDeletions.add(element);
		} else
			throw new RuntimeException(
					"cant add deletion action to diff. Provided id is not present in provided graph");
	}

	/**
	 * Apply the changes of a diff to the provided resource.
	 * 
	 * @param resource The resource to apply the diff to.
	 * @param diff     The diff to apply to the resource.
	 * @throws UncomparableException if the provided resource is not of the expected
	 *                               syntax.
	 */
	public static void apply(ViewModelProjectResource resource, ViewModelDiff diff) throws UncomparableException {
		var graphSyntaxName = resource.getSyntaxName();
		if (graphSyntaxName.isEmpty())
			throw new UncomparableException(
					"graph has no syntax name metadata field, refusing to apply diffs to it");
		if (!diff.syntaxStyle.equals(graphSyntaxName.get()))
			throw new UncomparableException("mismatched syntaxes '%s' diff vs '%s' graph"
					.formatted(diff.syntaxStyle, graphSyntaxName.get()));

		var g = resource.syntax();
		for (var edgeDeletion : diff.edgeDeletions)
			g.edges().remove(edgeDeletion.id());
		for (var vertexDeletion : diff.vertexDeletions)
			g.vertices().remove(vertexDeletion.id());
		for (var vertexAddition : diff.vertexAdditions)
			g.vertices().put(vertexAddition.id(), vertexAddition);
		for (var edgeAddition : diff.edgeAdditions)
			g.edges().put(edgeAddition.id(), edgeAddition);
	}

	/**
	 * Apply the changes of a diff to the provided resource, but only apply the
	 * additions.
	 * 
	 * @param resource The resource to apply the diff to.
	 * @param diff     The diff to apply to the resource.
	 * @throws UncomparableException if the provided resource is not of the expected
	 *                               syntax.
	 */
	public static void applyAdditiveOnly(ViewModelProjectResource resource, ViewModelDiff diff)
			throws UncomparableException {
		var graphSyntaxName = resource.getSyntaxName();
		if (graphSyntaxName.isEmpty())
			throw new UncomparableException(
					"graph has no syntax name metadata field, refusing to apply diffs to it");
		if (!diff.syntaxStyle.equals(graphSyntaxName.get()))
			throw new UncomparableException("mismatched syntaxes '%s' diff vs '%s' graph"
					.formatted(diff.syntaxStyle, graphSyntaxName.get()));

		var g = resource.syntax();
		for (var vertexAddition : diff.vertexAdditions)
			g.vertices().put(vertexAddition.id(), vertexAddition);
		for (var edgeAddition : diff.edgeAdditions)
			g.edges().put(edgeAddition.id(), edgeAddition);
	}

	/**
	 * Apply the changes of a diff to a copy of the provided resource and return the
	 * copy
	 * 
	 * @param resource The resource to apply the diff to.
	 * @param diff     The diff to apply to the resource.
	 * @throws UncomparableException if the provided resource is not of the expected
	 *                               syntax.
	 * @return A new copy of the resource with the changes applied.
	 */
	public static ViewModelProjectResource applyCopy(ViewModelProjectResource resource, ViewModelDiff diff)
			throws UncomparableException {
		var modelCpy = resource.toModel();
		var syntaxName = resource.getSyntaxName();
		if (syntaxName.isEmpty())
			throw new UncomparableException(
					"graph has no syntax name metadata field, refusing to apply diffs to it");
		var result = new ViewModelProjectResource(modelCpy, MetadataUtils.getSyntaxFactory(syntaxName.get()));
		apply(result, diff);
		return result;
	}

	/**
	 * Inversely apply the changes of a diff to the provided resource.
	 * 
	 * @param resource The resource to revert the diff on.
	 * @param diff     The diff to revert on the resource.
	 * @throws UncomparableException if the provided resource is not of the expected
	 *                               syntax.
	 */
	public static void revert(ViewModelProjectResource resource, ViewModelDiff diff) throws UncomparableException {
		var graphSyntaxName = resource.getSyntaxName();
		if (graphSyntaxName.isEmpty())
			throw new UncomparableException(
					"graph has no syntax name metadata field, refusing to apply diffs to it");
		if (!diff.syntaxStyle.equals(graphSyntaxName.get()))
			throw new UncomparableException("mismatched syntaxes '%s' diff vs '%s' graph"
					.formatted(diff.syntaxStyle, graphSyntaxName.get()));

		var g = resource.syntax();
		for (var edgeAddition : diff.edgeAdditions)
			g.edges().remove(edgeAddition.id());
		for (var vertexAddition : diff.vertexAdditions)
			g.vertices().remove(vertexAddition.id());
		for (var vertexDeletion : diff.vertexDeletions)
			g.vertices().put(vertexDeletion.id(), vertexDeletion);
		for (var edgeDeletion : diff.edgeDeletions)
			g.edges().put(edgeDeletion.id(), edgeDeletion);
	}

	/**
	 * Inversely apply the changes of a diff to the provided resource, but only
	 * apply the deletions.
	 * 
	 * @param resource The resource to revert the diff on.
	 * @param diff     The diff to revert on the resource.
	 * @throws UncomparableException if the provided resource is not of the expected
	 *                               syntax.
	 */
	public static void revertSubtractiveOnly(ViewModelProjectResource resource, ViewModelDiff diff)
			throws UncomparableException {
		var graphSyntaxName = resource.getSyntaxName();
		if (graphSyntaxName.isEmpty())
			throw new UncomparableException(
					"graph has no syntax name metadata field, refusing to apply diffs to it");
		if (!diff.syntaxStyle.equals(graphSyntaxName.get()))
			throw new UncomparableException("mismatched syntaxes '%s' diff vs '%s' graph"
					.formatted(diff.syntaxStyle, graphSyntaxName.get()));

		var g = resource.syntax();
		for (var edgeAddition : diff.edgeAdditions)
			g.edges().remove(edgeAddition.id());
		for (var vertexAddition : diff.vertexAdditions)
			g.vertices().remove(vertexAddition.id());
	}

	/**
	 * Inversely apply the changes of a diff to a copy of the provided resource and
	 * return the copy
	 * 
	 * @param resource The resource to revert the diff on.
	 * @param diff     The diff to revert on the resource.
	 * @throws UncomparableException if the provided resource is not of the expected
	 *                               syntax.
	 * @return A new copy of the resource with the changes reverted.
	 */
	public static ViewModelProjectResource revertCopy(ViewModelProjectResource resource, ViewModelDiff diff)
			throws UncomparableException {
		var modelCpy = resource.toModel();
		var syntaxName = resource.getSyntaxName();
		if (syntaxName.isEmpty())
			throw new UncomparableException(
					"graph has no syntax name metadata field, refusing to apply diffs to it");
		var result = new ViewModelProjectResource(modelCpy, MetadataUtils.getSyntaxFactory(syntaxName.get()));
		revert(result, diff);
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof ViewModelDiff vother))
			return false;
		if (!syntaxStyle.equals(vother.syntaxStyle))
			return false;
		for (var x : vertexDeletions)
			if (!vother.vertexDeletions.stream().anyMatch(e -> e.equals(x)))
				return false;
		for (var x : vertexAdditions)
			if (!vother.vertexAdditions.stream().anyMatch(e -> e.equals(x)))
				return false;
		for (var x : edgeDeletions)
			if (!vother.edgeDeletions.stream().anyMatch(e -> e.equals(x)))
				return false;
		for (var x : edgeAdditions)
			if (!vother.edgeAdditions.stream().anyMatch(e -> e.equals(x)))
				return false;
		return true;
	}

	@Override
	public int hashCode() {
		return syntaxStyle.hashCode() ^
				vertexDeletions.hashCode() ^
				edgeDeletions.hashCode() ^
				vertexAdditions.hashCode() ^
				edgeAdditions.hashCode();
	}

	@Override
	public String toString() {
		var result = new StringBuilder();
		result.append("syntaxStyle=").append(syntaxStyle);
		for (var x : vertexDeletions)
			result.append("\n").append("-v ").append(x.id().toString());
		for (var x : vertexAdditions)
			result.append("\n").append("+v ").append(x.id().toString());
		for (var x : edgeDeletions)
			result.append("\n").append("-e ").append(x.id().toString());
		for (var x : edgeAdditions)
			result.append("\n").append("+e ").append(x.id().toString());
		return result.toString();
	}
}
