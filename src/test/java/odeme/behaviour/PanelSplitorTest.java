package odeme.behaviour;

import odme.odmeeditor.GraphWindow;
import odme.odmeeditor.ProjectTree;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class PanelSplitorTest {

    private PanelSplitor panelSplitor;

    @BeforeEach
    void setUp() {
        panelSplitor = new PanelSplitor();
        //PanelSplitor.dividerLocation = 0; // ✅ reset before each test
    }

    @AfterEach
    void tearDown() {
    PanelSplitor.dividerLocation = 0;
    }


    @Test
    @DisplayName("PS_01 | createSpliPane should configure split pane correctly")
    void testCreateSpliPane() {
        JPanel left = new JPanel();
        JPanel right = new JPanel();

        JSplitPane pane = invokeCreateSplitPane(panelSplitor, JSplitPane.HORIZONTAL_SPLIT, left, right, 200);

        assertNotNull(pane);
        assertEquals(JSplitPane.HORIZONTAL_SPLIT, pane.getOrientation());
        assertEquals(left, pane.getLeftComponent());
        assertEquals(right, pane.getRightComponent());
        assertEquals(200, pane.getDividerLocation());
        assertEquals(6, pane.getDividerSize());
        assertTrue(pane.isOneTouchExpandable());
    }

    // Use reflection to access private helper
    private JSplitPane invokeCreateSplitPane(PanelSplitor target, int orientation, java.awt.Component left, java.awt.Component right, int dividerLocation) {
        try {
            var method = PanelSplitor.class.getDeclaredMethod("createSpliPane", int.class, java.awt.Component.class, java.awt.Component.class, int.class);
            method.setAccessible(true);
            return (JSplitPane) method.invoke(target, orientation, left, right, dividerLocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("PS_02 | addSplitor should return valid split pane combining panels")
    void testAddSplitorCreatesValidPane() {
        // ✅ Use real lightweight components, not mocks
        ProjectTree projectTree = new ProjectTree();
        GraphWindow graphWindow = new GraphWindow();

        JSplitPane result = panelSplitor.addSplitor(projectTree, graphWindow);

        assertNotNull(result);
        assertEquals(JSplitPane.VERTICAL_SPLIT, result.getOrientation());
        assertEquals(graphWindow, result.getTopComponent());
        assertEquals(projectTree, result.getBottomComponent());
        assertEquals(750, result.getDividerLocation());
    }

    @Test
    @DisplayName("PS_03 | graphtreeFunc listener updates divider location correctly")
    void testDividerLocationUpdate() throws Exception {
        JSplitPane dummyPane = new JSplitPane();
        dummyPane.setDividerLocation(100);

        // Reflectively inject it
        var field = PanelSplitor.class.getDeclaredField("graphtree");
        field.setAccessible(true);
        field.set(panelSplitor, dummyPane);

        var method = PanelSplitor.class.getDeclaredMethod("graphtreeFunc");
        method.setAccessible(true);
        method.invoke(panelSplitor);

        // Fire a change event
        dummyPane.setDividerLocation(300);
        for (PropertyChangeListener l : dummyPane.getPropertyChangeListeners(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
            l.propertyChange(new PropertyChangeEvent(dummyPane, JSplitPane.DIVIDER_LOCATION_PROPERTY, 100, 300));
        }

        // Let SwingUtilities.invokeLater execute
        SwingUtilities.invokeAndWait(() -> {});

        assertEquals(300, PanelSplitor.dividerLocation);
    }

    @Test
    @DisplayName("PS_04 | dividerLocation static field initialized to 0")
    void testInitialDividerLocation() {
        assertEquals(0, PanelSplitor.dividerLocation);
    }

    @Test
    @DisplayName("PS_05 | createSpliPane handles null components gracefully")
    void testCreateSpliPaneWithNulls() {
        JSplitPane pane = invokeCreateSplitPane(panelSplitor, JSplitPane.HORIZONTAL_SPLIT, null, null, 100);
        assertNotNull(pane);
    }
}
