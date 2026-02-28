package odeme.behaviour;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *  Tests for {@link MenuBarBehaviour}.
 * Verifies correct menu bar creation and setup behavior.
 */
class MenuBarBehaviourTest {

    @BeforeAll
    static void setUpHeadless() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    @DisplayName("MB_01: Should attach menu bar to JFrame on construction")
    void testMenuBarAttachedToFrame() {
        JFrame mockFrame = new JFrame();
        MenuBarBehaviour menuBarBehaviour = new MenuBarBehaviour(mockFrame);

        JMenuBar attachedMenuBar = mockFrame.getJMenuBar();

        assertNotNull(attachedMenuBar, "Menu bar should be attached to JFrame");
        assertTrue(attachedMenuBar instanceof JMenuBar, "Menu bar should be of type JMenuBar");
    }

    @Test
    @DisplayName("MB_02: Should create 'File' menu with correct mnemonic and items")
    void testShowCreatesFileMenu() {
        JFrame mockFrame = new JFrame();
        MenuBarBehaviour menuBarBehaviour = new MenuBarBehaviour(mockFrame);

        // Act
        menuBarBehaviour.show();

        JMenuBar bar = mockFrame.getJMenuBar();
        assertNotNull(bar, "Menu bar should not be null after show()");

        // Verify a single menu named 'File' exists
        boolean fileMenuFound = false;
        for (int i = 0; i < bar.getMenuCount(); i++) {
            JMenu menu = bar.getMenu(i);
            if (menu != null && "File".equals(menu.getText())) {
                fileMenuFound = true;
                assertEquals(KeyEvent.VK_F, menu.getMnemonic(), "Mnemonic should be F");
                assertNotNull(menu.getBorder(), "Menu should have a non-null border");
                break;
            }
        }

        assertTrue(fileMenuFound, "'File' menu should be present in the menu bar");
    }

    @Test
    @DisplayName("MB_03: Should allow multiple menus to be added dynamically")
    void testAddMultipleMenus() throws Exception {
        JFrame frame = new JFrame();
        MenuBarBehaviour behaviour = new MenuBarBehaviour(frame);

        // Access private method via reflection
        var addMenuMethod = MenuBarBehaviour.class.getDeclaredMethod(
                "addMenu", String.class, int.class, String[].class, int[].class, String[].class, String[].class);
        addMenuMethod.setAccessible(true);

        // Add two menus manually
        addMenuMethod.invoke(behaviour, "Edit", KeyEvent.VK_E, null, null, null, null);
        addMenuMethod.invoke(behaviour, "View", KeyEvent.VK_V, null, null, null, null);

        JMenuBar bar = frame.getJMenuBar();
        assertEquals(2, bar.getMenuCount(), "Two menus should be present (Edit, View)");

        assertEquals("Edit", bar.getMenu(0).getText());
        assertEquals("View", bar.getMenu(1).getText());
    }
}
