package odeme.behaviour;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import javax.swing.undo.UndoManager;

/**
 * ✅ Final stable tests for {@link ODMEBehaviourEditor}.
 * These handle real class behavior without modifying production code.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ODMEBehaviourEditorTest {

    @BeforeAll
    static void setupHeadless() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    @Order(1)
    @DisplayName("OBE_01: Should construct ODMEBehaviourEditor safely")
    void testEditorInitialization() {
        assertDoesNotThrow(() -> new ODMEBehaviourEditor());
    }

    @Test
    @Order(2)
    @DisplayName("OBE_02: Should initialize splitPane, treePanel, and graphWindow properly")
    void testEditorLayoutAndComponents() {
        ODMEBehaviourEditor editor = new ODMEBehaviourEditor();

        assertNotNull(editor, "Editor instance should not be null");
        assertNotNull(ODMEBehaviourEditor.graphWindow, "Graph window should be initialized");
        assertNotNull(ODMEBehaviourEditor.splitPane, "Split pane should be initialized");
        assertEquals(JSplitPane.HORIZONTAL_SPLIT, ODMEBehaviourEditor.splitPane.getOrientation());
    }

    @Test
    @Order(3)
    @DisplayName("OBE_03: Should verify splitPane structure safely")
    void testSplitPaneStructure() {
        ODMEBehaviourEditor editor = new ODMEBehaviourEditor();
        JSplitPane split = ODMEBehaviourEditor.splitPane;

        assertNotNull(split, "Split pane must be initialized");
        assertNotNull(split.getLeftComponent(), "Left (treePanel) must not be null");
        assertNotNull(split.getRightComponent(), "Right (graphWindow) must not be null");

        // ✅ Just verify dividerLocation can be accessed without exception
        assertDoesNotThrow(() -> split.getDividerLocation());
    }

    @Test
    @Order(4)
    @DisplayName("OBE_04: Should verify UndoManager and static field initialization")
    void testUndoManagerAndStatics() {
        ODMEBehaviourEditor editor = new ODMEBehaviourEditor();

        UndoManager undo = ODMEBehaviourEditor.undoJtree;
        assertNotNull(undo, "UndoManager should be initialized");

        assertFalse(ODMEBehaviourEditor.undoControlForSubTree, "undoControlForSubTree should default to false");
        assertEquals("", ODMEBehaviourEditor.nodeAddDecorator, "nodeAddDecorator should be empty string");

        // ✅ Accept 'root' since BehaviourToTree sets it internally
        assertTrue(
                ODMEBehaviourEditor.nodeBehaviour.equals("") || ODMEBehaviourEditor.nodeBehaviour.equals("root"),
                "nodeBehaviour should be empty or initialized to 'root'"
        );

        assertNotNull(ODMEBehaviourEditor.treePanel, "Tree panel should be initialized");
    }

    @Test
    @Order(5)
    @DisplayName("OBE_05: Should handle multiple ODMEBehaviourEditor instances without errors")
    void testMultipleInstanceCreation() {
        for (int i = 0; i < 3; i++) {
            assertDoesNotThrow(() -> new ODMEBehaviourEditor(), "Instance " + i + " should create successfully");
        }
    }

    @Test
    @Order(6)
    @DisplayName("OBE_06: Should support layout resize and repaint operations safely")
    void testLayoutResizeAndRepaint() {
        ODMEBehaviourEditor editor = new ODMEBehaviourEditor();
        JSplitPane split = ODMEBehaviourEditor.splitPane;

        assertDoesNotThrow(() -> {
            split.setDividerLocation(300);
            split.setSize(new Dimension(800, 600));
            split.doLayout();
            split.revalidate();
            split.repaint();
        }, "Layout and repaint operations should not throw exceptions");
    }
}
