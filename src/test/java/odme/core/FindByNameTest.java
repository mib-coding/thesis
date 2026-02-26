package odme.core;

import org.junit.jupiter.api.Test;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FindByName}.
 * Tests cover valid lookup, missing nodes, and boundary cases.
 */
class FindByNameTest {

    private JTree buildSimpleTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode a = new DefaultMutableTreeNode("A");
        DefaultMutableTreeNode b = new DefaultMutableTreeNode("B");
        DefaultMutableTreeNode leaf = new DefaultMutableTreeNode("Leaf");
        b.add(leaf);
        a.add(b);
        root.add(a);
        return new JTree(root);
    }

    @Test
    void shouldFindDeepChildPath() {
        JTree tree = buildSimpleTree();
        new FindByName(tree, new String[]{"Root", "A", "B", "Leaf"});
        TreePath result = FindByName.path;

        assertNotNull(result, "Expected a non-null path for existing node");
        assertEquals("Leaf", result.getLastPathComponent().toString());
    }

    @Test
    void shouldReturnNullWhenNodeMissing() {
        JTree tree = buildSimpleTree();
        new FindByName(tree, new String[]{"Root", "Missing"});
        assertNull(FindByName.path, "Expected null path when node not found");
    }

    @Test
    void shouldThrowExceptionForEmptyInput() {
        JTree tree = buildSimpleTree();
        assertThrows(ArrayIndexOutOfBoundsException.class,
            () -> new FindByName(tree, new String[]{}),
            "Expected exception when search array is empty");
}


    @Test
    void shouldReturnNullForNullTree() {
        assertThrows(NullPointerException.class, () -> new FindByName(null, new String[]{"Root"}));
    }
}
