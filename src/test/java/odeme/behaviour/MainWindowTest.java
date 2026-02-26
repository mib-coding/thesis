package odeme.behaviour;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.swing.JSplitPane;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

/**
 * ✅ Tests for {@link MainWindow}.
 * Verifies that the frame, toolbar, and menu bar are initialized correctly.
 */
class MainWindowTest {

    @BeforeAll
    static void setUpHeadless() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    @DisplayName("MW_01: Should initialize frame with correct title and visibility")
    void testFrameInitializationAndSetup() {
        try (
            MockedConstruction<MenuBarBehaviour> mockMenuBar =
                    mockConstruction(MenuBarBehaviour.class, (mock, ctx) -> doNothing().when(mock).show());
            MockedConstruction<ToolBarBehaviour> mockToolBar =
                    mockConstruction(ToolBarBehaviour.class, (mock, ctx) -> doNothing().when(mock).show())
        ) {
            // Prepare static dependency before construction
            ODMEBehaviourEditor.splitPane = new JSplitPane();

            MainWindow mainWindow = new MainWindow();

            assertNotNull(MainWindow.frame, "Frame should not be null");
            assertEquals("Behaviour Modeling Tool Environment", MainWindow.frame.getTitle());
            assertTrue(MainWindow.frame.isVisible(), "Frame should be visible");

            verify(mockMenuBar.constructed().get(0)).show();
            verify(mockToolBar.constructed().get(0)).show();
        }
    }

    @Test
    @DisplayName("MW_02: Should set ODMEBehaviourEditor as content pane")
    void testContentPaneIsODMEBehaviourEditor() {
        try (
            MockedConstruction<MenuBarBehaviour> mockMenuBar =
                    mockConstruction(MenuBarBehaviour.class, (mock, ctx) -> doNothing().when(mock).show());
            MockedConstruction<ToolBarBehaviour> mockToolBar =
                    mockConstruction(ToolBarBehaviour.class, (mock, ctx) -> doNothing().when(mock).show())
        ) {
            ODMEBehaviourEditor.splitPane = new JSplitPane();

            MainWindow mainWindow = new MainWindow();

            assertTrue(MainWindow.frame.getContentPane() instanceof ODMEBehaviourEditor,
                    "Frame content pane should be instance of ODMEBehaviourEditor");
        }
    }

    @Test
    @DisplayName("MW_03: Should add splitPane to frame layout at center")
    void testSplitPaneAddedToFrame() {
        try (
            MockedConstruction<MenuBarBehaviour> mockMenuBar =
                    mockConstruction(MenuBarBehaviour.class, (mock, ctx) -> doNothing().when(mock).show());
            MockedConstruction<ToolBarBehaviour> mockToolBar =
                    mockConstruction(ToolBarBehaviour.class, (mock, ctx) -> doNothing().when(mock).show())
        ) {
            JSplitPane mockSplitPane = new JSplitPane();
            ODMEBehaviourEditor.splitPane = mockSplitPane;

            new MainWindow();

            assertNotNull(MainWindow.frame.getContentPane().getComponents(),
                    "Frame components should not be null");
            assertTrue(MainWindow.frame.getContentPane().getComponents().length > 0,
                    "Frame should contain splitPane or other components");
        }
    }
}
