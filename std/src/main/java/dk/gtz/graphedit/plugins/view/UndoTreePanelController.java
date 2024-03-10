package dk.gtz.graphedit.plugins.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

// NOTE: BOOKMARK: You're working on an undo-tree visualizer. This seems to be good enough for now
// NOTE: BOOKMARK: Plan is to make these lines monospace font and hoverable and selectable and highlight the current
// NOTE: BOOKMARK: Undoable with something...
public class UndoTreePanelController extends StackPane {
    private final VBox container;
    private final ListView<String> list;

    public UndoTreePanelController() {
	container = new VBox();
	list = new ListView<>();
	list.getItems().addAll(prototype());
	list.getStyleClass().add(Styles.DENSE);
	list.getStyleClass().add(Tweaks.EDGE_TO_EDGE);
	list.getStyleClass().add("text-monospace");
	container.getChildren().add(list);
	getChildren().add(container);
    }

    static class TreeNode {
        String message;
        List<TreeNode> children;

        public TreeNode(String message) {
            this.message = message;
            this.children = new ArrayList<>();
        }

        public void addChild(TreeNode child) {
            this.children.add(child);
        }

        public void printTree() {
            printTreeHelper(this, "");
        }

        private List<String> printTreeHelper(TreeNode node, String prefix) {
	    // TODO: Implement this method
	    throw new UnsupportedOperationException("Not implemented yet");
        }
    }

    private static List<String> prototype() {
        var tree = new HashMap<String, List<String>>();
        tree.put("root", List.of("1"));
        tree.put("1", List.of("2"));
        tree.put("2", List.of("3"));
        tree.put("3", List.of("4", "5"));
        tree.put("4", List.of());
        tree.put("5", List.of("6"));
        tree.put("6", List.of("7", "10", "12"));
        tree.put("7", List.of("8"));
        tree.put("8", List.of("9"));
        tree.put("9", List.of());
        tree.put("10", List.of("11"));
        tree.put("11", List.of());
        tree.put("12", List.of("13"));
        tree.put("13", List.of());

        var result = helper(0, false, "root", tree, "11");
        Collections.reverse(result);
	return result;
        //
        // System.out.println("ChatGPTs attempt:");
        // var root = new TreeNode("Root");
        // var child1 = new TreeNode("Child 1");
        // var child2 = new TreeNode("Child 2");
        // var child3 = new TreeNode("Child 3");
        // var grandchild1 = new TreeNode("Grandchild 1");
        // var grandchild2 = new TreeNode("Grandchild 2");
        // var grandchild3 = new TreeNode("Grandchild 3");
        // var grandchild4 = new TreeNode("Grandchild 4");
        // var grandchild5 = new TreeNode("Grandchild 5");
        //
        // child1.addChild(grandchild1);
        // child1.addChild(grandchild2);
        // child2.addChild(grandchild3);
        // child2.addChild(grandchild4);
        // child2.addChild(grandchild5);
        //
        // root.addChild(child1);
        // root.addChild(child2);
        // root.addChild(child3);
        //
        // root.printTree();
        // return true;
    }

    private static List<String> helper(int indent, boolean hasSiblings, String current, Map<String, List<String>> tree, String currentlySelected) {
        var result = new ArrayList<String>();
        var node = tree.get(current);
        if(node == null)
            return result;
        var prefix = "";
        for(var i = 0; i < indent; i++) {
            if(hasSiblings && i+1 == indent)
                prefix += " ├─";
            else
                prefix += " │ ";
        }
        if(current.equals(currentlySelected))
            prefix += " * ";
        else
            prefix += " ○ ";
        result.add(prefix + "<message>");
        for(var i = node.size()-1; i >= 0; i--) {
            result.addAll(helper(indent + i, i != 0, node.get(i), tree, currentlySelected));
        }
        return result;
    }

}
