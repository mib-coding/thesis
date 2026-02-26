package odeme.behaviour;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import behaviourtreetograph.JtreeToGraphConvert;
import behaviourtreetograph.JtreeToGraphGeneral;

/**
 * Unit tests for {@link ToolBarBehaviour}.
 * Focuses on toolbar creation, button wiring, and action dispatching.
 */
@DisplayName("ToolBarBehaviour Tests")
class ToolBarBehaviourTest {

    private JFrame frame;
    private ToolBarBehaviour toolBarBehaviour;

    @BeforeEach
    void setUp() {
        frame = new JFrame();
        toolBarBehaviour = new ToolBarBehaviour(frame);

        ToolBarBehaviour.btnItems.clear();
        ODMEBehaviourEditor.nodeAddDecorator = "";
    }

    @Test
    @DisplayName("Toolbar is added to the frame")
    void shouldAddToolbarToFrame() {
        toolBarBehaviour.show();

        boolean found = false;
        for (java.awt.Component c : frame.getContentPane().getComponents()) {
            if (c instanceof JToolBar) {
                found = true;
                break;
            }
        }
        assertTrue(found, "JToolBar should be added to the frame");
    }

    @Test
    @DisplayName("All expected toolbar buttons are created")
    void shouldCreateAllToolbarButtons() {
        toolBarBehaviour.show();

        String[] expectedButtons = {
                "Selector", "Add Decorator", "Add Selector", "Add Sequence",
                "Add Parallel", "Delete Node", "Save Graph",
                "Undo", "Redo", "Zoom In", "Zoom Out", "Merge"
        };

        for (String name : expectedButtons) {
            assertTrue(
                ToolBarBehaviour.btnItems.containsKey(name),
                "Missing toolbar button: " + name
            );
        }
    }

    @Test
    @DisplayName("Selector button clears decorator mode")
    void selectorButtonClearsDecorator() {
        toolBarBehaviour.show();
        ODMEBehaviourEditor.nodeAddDecorator = "Decorator";

        ToolBarBehaviour.btnItems.get("Selector").doClick();

        assertEquals("", ODMEBehaviourEditor.nodeAddDecorator);
    }

    @Test
    @DisplayName("Add Decorator button sets decorator mode")
    void addDecoratorButtonSetsDecorator() {
        toolBarBehaviour.show();

        ToolBarBehaviour.btnItems.get("Add Decorator").doClick();

        assertEquals("Decorator", ODMEBehaviourEditor.nodeAddDecorator);
    }

    @Test
    @DisplayName("Add Selector button sets selector mode")
    void addSelectorButtonSetsSelector() {
        toolBarBehaviour.show();

        ToolBarBehaviour.btnItems.get("Add Selector").doClick();

        assertEquals("Selector", ODMEBehaviourEditor.nodeAddDecorator);
    }

    @Test
    @DisplayName("Add Sequence button sets sequence mode")
    void addSequenceButtonSetsSequence() {
        toolBarBehaviour.show();

        ToolBarBehaviour.btnItems.get("Add Sequence").doClick();

        assertEquals("Sequence", ODMEBehaviourEditor.nodeAddDecorator);
    }

    @Test
    @DisplayName("Add Parallel button sets parallel mode")
    void addParallelButtonSetsParallel() {
        toolBarBehaviour.show();

        ToolBarBehaviour.btnItems.get("Add Parallel").doClick();

        assertEquals("Parallel", ODMEBehaviourEditor.nodeAddDecorator);
    }

    @Test
    @DisplayName("Delete Node button sets delete mode")
    void deleteNodeButtonSetsDelete() {
        toolBarBehaviour.show();

        ToolBarBehaviour.btnItems.get("Delete Node").doClick();

        assertEquals("delete", ODMEBehaviourEditor.nodeAddDecorator);
    }

    @Test
    @DisplayName("Undo button triggers graph undo")
    void undoButtonTriggersUndo() {
        toolBarBehaviour.show();

        try (MockedStatic<JtreeToGraphGeneral> mocked =
                     mockStatic(JtreeToGraphGeneral.class)) {

            ToolBarBehaviour.btnItems.get("Undo").doClick();

            mocked.verify(JtreeToGraphGeneral::undo);
        }
    }

    @Test
    @DisplayName("Redo button triggers graph redo")
    void redoButtonTriggersRedo() {
        toolBarBehaviour.show();

        try (MockedStatic<JtreeToGraphGeneral> mocked =
                     mockStatic(JtreeToGraphGeneral.class)) {

            ToolBarBehaviour.btnItems.get("Redo").doClick();

            mocked.verify(JtreeToGraphGeneral::redo);
        }
    }

    @Test
    @DisplayName("Zoom In button triggers zoom in")
    void zoomInButtonTriggersZoomIn() {
        toolBarBehaviour.show();

        try (MockedStatic<JtreeToGraphGeneral> mocked =
                     mockStatic(JtreeToGraphGeneral.class)) {

            ToolBarBehaviour.btnItems.get("Zoom In").doClick();

            mocked.verify(JtreeToGraphGeneral::zoomIn);
        }
    }

    @Test
    @DisplayName("Zoom Out button triggers zoom out")
    void zoomOutButtonTriggersZoomOut() {
        toolBarBehaviour.show();

        try (MockedStatic<JtreeToGraphGeneral> mocked =
                     mockStatic(JtreeToGraphGeneral.class)) {

            ToolBarBehaviour.btnItems.get("Zoom Out").doClick();

            mocked.verify(JtreeToGraphGeneral::zoomOut);
        }
    }

    @Test
    @DisplayName("Merge button triggers behaviour-XSD merge")
    void mergeButtonTriggersMerge() {
        toolBarBehaviour.show();

        try (MockedStatic<JtreeToGraphConvert> mocked =
                     mockStatic(JtreeToGraphConvert.class)) {

            ToolBarBehaviour.btnItems.get("Merge").doClick();

            mocked.verify(JtreeToGraphConvert::mergeBehaviourWithXSD);
        }
    }

    @Test
    @DisplayName("Each toolbar button has at least one ActionListener")
    void eachButtonHasActionListener() {
        toolBarBehaviour.show();

        for (JButton btn : ToolBarBehaviour.btnItems.values()) {
            assertTrue(
                btn.getActionListeners().length > 0,
                "Button should have an ActionListener: " + btn.getName()
            );
        }
    }
}
