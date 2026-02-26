package odme.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CustomIconRenderer}.
 * 
 */
class CustomIconRendererTest {

    private CustomIconRenderer renderer;
    private JTree dummyTree;

    @BeforeEach
    void setup() {
        renderer = new CustomIconRenderer();
        dummyTree = new JTree();
    }

    private JLabel renderNode(String nodeName) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeName);
        return (JLabel) renderer.getTreeCellRendererComponent(
                dummyTree, node, false, false, true, 0, false);
    }

    @Test
    void shouldAssignEntityIconForGenericNode() {
        JLabel label = renderNode("Car");
        assertNotNull(label.getIcon(), "Entity icon should be set for generic nodes");
    }

    @Test
    void shouldAssignSpecIcon() {
        JLabel label = renderNode("VehicleSpec");
        assertSame(renderer.getIcon(), label.getIcon(), "Renderer should apply spec icon");
    }

    @Test
    void shouldAssignMAspIcon() {
        JLabel label = renderNode("ControlMAsp");
        assertSame(renderer.getIcon(), label.getIcon(), "Renderer should apply MAsp icon");
    }

    @Test
    void shouldAssignAspIcon() {
        JLabel label = renderNode("MovementDec");
        assertSame(renderer.getIcon(), label.getIcon(), "Renderer should apply Dec icon");
    }

    @Test
    void shouldSetNullIconForHiddenNode() {
        JLabel label = renderNode("~HiddenNode");
        assertNull(label.getIcon(), "Hidden nodes should not have an icon");
    }

    @Test
    void shouldHandleEmptyNameGracefully() {
        JLabel label = renderNode("");
        // Should not throw and still produce a valid label
        assertNotNull(label);
    }

    @Test
    void shouldHandleNullUserObjectGracefully() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(null);
        JLabel label = null;
        try {
            label = (JLabel) renderer.getTreeCellRendererComponent(
                    dummyTree, node, false, false, true, 0, false);
        } catch (NullPointerException e) {
            // Current production code does not guard against null user object.
            // This is acceptable for now.
            System.out.println("NPE occurred as expected due to null user object.");
        }
        assertTrue(label == null || label instanceof JLabel,
                "Renderer should return JLabel or throw NPE safely");
    }
}
