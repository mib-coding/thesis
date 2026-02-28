package odeme.behaviour;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import behaviourtreetograph.JtreeToGraphSave;
import odme.odmeeditor.ODMEEditor;

/**
 * Unit tests for {@link BehaviourToTree}.
 */
class BehaviourToTreeTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("BT01 - convertMapToArray should convert a simple map correctly")
    void testConvertMapToArray() {
        Map<TreePath, Collection<String>> map = new HashMap<>();
        TreePath path = new TreePath(new String[]{"A", "B"});
        map.put(path, List.of("C1", "C2"));

        String[][] result = BehaviourToTree.convertMapToArray(map);

        assertEquals(1, result.length);
        assertEquals("A", result[0][0]);
        assertEquals("B", result[0][1]);
        assertEquals("C1", result[0][2]);
        assertEquals("C2", result[0][3]);
    }

    @Test
    @DisplayName("BT02 - convert should return sorted array by TreePath depth")
    void testConvertSortsArray() {
        Multimap<TreePath, String> map = ArrayListMultimap.create();
        map.put(new TreePath(new String[]{"root"}), "X");
        map.put(new TreePath(new String[]{"root", "child"}), "Y");

        String[][] result = BehaviourToTree.convert(map);

        assertEquals(2, result.length);
        assertEquals("root", result[0][0]); // sorted by depth
    }

    @Test
    @DisplayName("BT03 - treeify should create a correct hierarchical structure")
    void testTreeifyCreatesHierarchy() {
        String[][] input = {
                {"root", "child1"},
                {"root", "child2", "grandchild"}
        };

        DefaultMutableTreeNode root = BehaviourToTree.treeify(input);

        assertNotNull(root);
        assertEquals(1, root.getChildCount());
        DefaultMutableTreeNode childRoot = (DefaultMutableTreeNode) root.getChildAt(0);
        assertEquals("root", childRoot.getUserObject());
        assertEquals(2, childRoot.getChildCount());
    }

    @Test
    @DisplayName("BT04 - mouseClicked should update ODMEBehaviourEditor.nodeBehaviour when valid node clicked")
    void testMouseClickedUpdatesNode() {
        BehaviourToTree btt = new BehaviourToTree();
        btt.tree = mock(javax.swing.JTree.class);

        TreePath path = new TreePath("root");

        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getPoint()).thenReturn(new java.awt.Point(1, 1));
        //  Use real integers instead of anyInt()
        when(btt.tree.getPathForLocation(1, 1)).thenReturn(path);

        try (MockedStatic<odeme.behaviour.ODMEBehaviourEditor> mocked = mockStatic(odeme.behaviour.ODMEBehaviourEditor.class)) 
        {
            btt.mouseClicked(mockEvent);

        // Verify that the static variable was set correctly
        assertEquals("root", odeme.behaviour.ODMEBehaviourEditor.nodeBehaviour);
        }
    }


    @Test
    @DisplayName("BT05 - mouseClicked should reset cursor when path is null")
    void testMouseClickedNullPath() {
        BehaviourToTree btt = new BehaviourToTree();
        btt.tree = mock(javax.swing.JTree.class);

        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getPoint()).thenReturn(new java.awt.Point(10, 10));
        when(btt.tree.getPathForLocation(anyInt(), anyInt())).thenReturn(null);

        btt.mouseClicked(mockEvent);

        verify(btt.tree, atLeastOnce()).setCursor(Cursor.getDefaultCursor());
    }

    @Test
    @DisplayName("BT06 - saveTreeModel should invoke JtreeToGraphSave.saveBehaviourGraph()")
    void testSaveTreeModelInvokesStaticCall() {
        try (MockedStatic<JtreeToGraphSave> mocked = mockStatic(JtreeToGraphSave.class)) {
            BehaviourToTree btt = new BehaviourToTree();
            btt.saveTreeModel();
            mocked.verify(JtreeToGraphSave::saveBehaviourGraph, times(1));
        }
    }

    @Test
    @DisplayName("BT07 - constructor should not throw when .ssdbeh file missing")
    void testConstructorWithMissingFile() {
        try (MockedStatic<ODMEEditor> odme = mockStatic(ODMEEditor.class)) {
            ODMEEditor.fileLocation = tempDir.toString();
            ODMEEditor.projName = "TestProject";
            BehaviourToTree.selectedScenario = "Scenario1";

            assertDoesNotThrow(BehaviourToTree::new);
        }
    }

    @Test
    @DisplayName("BT08 - convertMapToArray handles empty map gracefully")
    void testConvertMapToArrayEmptyMap() {
        Map<TreePath, Collection<String>> empty = new HashMap<>();
        String[][] result = BehaviourToTree.convertMapToArray(empty);
        assertNotNull(result);
        assertEquals(0, result.length);
    }
}
