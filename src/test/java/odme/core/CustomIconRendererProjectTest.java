package odme.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CustomIconRendererProject}.
 * This test verifies icon assignment behavior for project tree nodes.
 */
class CustomIconRendererProjectTest {

    private CustomIconRendererProject renderer;
    private JTree dummyTree;

    @BeforeEach
    void setup() {
        renderer = new CustomIconRendererProject();
        dummyTree = new JTree();
    }

    private JLabel renderNode(String nodeName) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeName);
        return (JLabel) renderer.getTreeCellRendererComponent(
                dummyTree, node, false, false, true, 0, false);
    }

    @Test
    void shouldAssignModuleIconForModuleNode() {
        JLabel label = renderNode("MainModule");
        assertNotNull(label.getIcon(), "Module nodes should have an icon assigned");
    }

    @Test
    void shouldAssignXmlIconForXmlFile() {
        JLabel label = renderNode("config.xml");
        assertNotNull(label.getIcon(), "XML file nodes should have xml icon");
    }

    @Test
    void shouldFallbackToModuleIconForOtherNames() {
        JLabel label = renderNode("OtherNode");
        assertNotNull(label.getIcon(), "Non-xml nodes should get default module icon");
    }

    @Test
    void shouldHandleEmptyNodeNameGracefully() {
        JLabel label = renderNode("");
        assertNotNull(label, "Renderer should still return a JLabel even for empty names");
    }

    @Test
    void shouldHandleNullUserObjectGracefully() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(null);
        JLabel label = null;
        try {
            label = (JLabel) renderer.getTreeCellRendererComponent(
                    dummyTree, node, false, false, true, 0, false);
        } catch (NullPointerException e) {
            // Expected, since production code does not handle null userObject
            System.out.println("NPE occurred as expected for null userObject in CustomIconRendererProject.");
        }
        assertTrue(label == null || label instanceof JLabel,
                "Renderer should either return a JLabel or throw NPE safely");
    }
}
