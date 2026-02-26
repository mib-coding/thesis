package odme.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link XmlJTree}.
 * Adapted to current implementation where only nodes with children are added.
 */
class XmlJTreeTest {

    @Test
    void shouldParseSimpleXmlIntoTree(@TempDir Path tempDir) throws IOException {
        Path xmlFile = tempDir.resolve("simple.xml");
        Files.writeString(xmlFile, "<root><child1/><child2/></root>");

        XmlJTree xmlJTree = new XmlJTree(xmlFile.toString());
        assertNotNull(xmlJTree.dtModel, "dtModel should be created for valid XML");

        TreeModel model = xmlJTree.dtModel;
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();

        // Root node should exist
        assertEquals("root", rootNode.getUserObject());
        // Since implementation only adds nodes with children, no children expected
        assertEquals(0, rootNode.getChildCount(), "Current implementation adds only non-leaf nodes");
    }

    @Test
    void shouldHandleNestedXmlStructure(@TempDir Path tempDir) throws IOException {
        Path xmlFile = tempDir.resolve("nested.xml");
        Files.writeString(xmlFile, "<root><level1><level2><leaf/></level2></level1></root>");

        XmlJTree xmlJTree = new XmlJTree(xmlFile.toString());
        assertNotNull(xmlJTree.dtModel);

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlJTree.dtModel.getRoot();
        assertEquals("root", root.getUserObject());

        // Current code will add only nodes that themselves have children:
        // So root should have one child: level1
        assertEquals(1, root.getChildCount(), "Root should have one child (level1)");
        DefaultMutableTreeNode level1 = (DefaultMutableTreeNode) root.getFirstChild();

        // level1 has one child (level2) since level2 has a leaf inside
        assertEquals("level1", level1.getUserObject());
        assertEquals(1, level1.getChildCount());

        // level2 will have no child (leaf not added)
        DefaultMutableTreeNode level2 = (DefaultMutableTreeNode) level1.getFirstChild();
        assertEquals("level2", level2.getUserObject());
        assertEquals(0, level2.getChildCount(), "Leaf not added since it has no children");
    }

    @Test
    void shouldHandleEmptyXml(@TempDir Path tempDir) throws IOException {
        Path xmlFile = tempDir.resolve("empty.xml");
        Files.writeString(xmlFile, "<root></root>");

        XmlJTree xmlJTree = new XmlJTree(xmlFile.toString());
        assertNotNull(xmlJTree.dtModel);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlJTree.dtModel.getRoot();
        assertEquals("root", root.getUserObject());
        assertEquals(0, root.getChildCount());
    }

    @Test
    void shouldNotCrashOnInvalidXml(@TempDir Path tempDir) throws IOException {
        Path xmlFile = tempDir.resolve("invalid.xml");
        Files.writeString(xmlFile, "<root><unclosed>");

        // Invalid XML should not throw — just log and dtModel stays null
        XmlJTree xmlJTree = new XmlJTree(xmlFile.toString());
        assertNull(xmlJTree.dtModel, "dtModel should be null for invalid XML");
    }

    @Test
    void shouldNotCrashWhenFileMissing() {
        XmlJTree xmlJTree = new XmlJTree("nonexistent.xml");
        // File missing → dtModel should remain null
        assertNull(xmlJTree.dtModel, "dtModel should remain null when file missing");
    }
}
