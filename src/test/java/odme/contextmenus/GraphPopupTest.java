package odme.contextmenus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Component;

import javax.swing.JMenuItem;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import odme.jtreetograph.JtreeToGraphAdd;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class GraphPopupTest {

    @BeforeAll
    static void headless() {
        System.setProperty("java.awt.headless", "true");
    }

    private JMenuItem findItem(GraphPopup popup, String text) {
        for (Component c : popup.getComponents()) {
            if (c instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) c;
                if (text.equals(item.getText())) {
                    return item;
                }
            }
        }
        fail("Menu item not found: " + text);
        return null;
    }

    @Test
    @DisplayName("TC-CM-01: GraphPopup creates 4 menu items")
    void tc_cm_01_shouldCreateFourMenuItems() {
        GraphPopup popup = new GraphPopup(10, 20);

        assertNotNull(findItem(popup, "Add Entity"));
        assertNotNull(findItem(popup, "Add Specialization"));
        assertNotNull(findItem(popup, "Add Aspect"));
        assertNotNull(findItem(popup, "Add MultiAspect"));
    }

    @Test
    @DisplayName("TC-CM-02: Clicking 'Add Entity' calls addNodeFromGraphPopup(Entity,x,y)")
    void tc_cm_02_addEntityCallsStaticAdd() {
        try (MockedStatic<JtreeToGraphAdd> mocked = mockStatic(JtreeToGraphAdd.class)) {
            GraphPopup popup = new GraphPopup(11, 22);
            findItem(popup, "Add Entity").doClick();

            mocked.verify(() -> JtreeToGraphAdd.addNodeFromGraphPopup("Entity", 11, 22), times(1));
        }
    }

    @Test
    @DisplayName("TC-CM-03: Clicking 'Add Specialization' calls addNodeFromGraphPopup(Spec,x,y)")
    void tc_cm_03_addSpecCallsStaticAdd() {
        try (MockedStatic<JtreeToGraphAdd> mocked = mockStatic(JtreeToGraphAdd.class)) {
            GraphPopup popup = new GraphPopup(1, 2);
            findItem(popup, "Add Specialization").doClick();

            mocked.verify(() -> JtreeToGraphAdd.addNodeFromGraphPopup("Spec", 1, 2), times(1));
        }
    }

    @Test
    @DisplayName("TC-CM-04: Clicking 'Add Aspect' calls addNodeFromGraphPopup(Dec,x,y)")
    void tc_cm_04_addDecCallsStaticAdd() {
        try (MockedStatic<JtreeToGraphAdd> mocked = mockStatic(JtreeToGraphAdd.class)) {
            GraphPopup popup = new GraphPopup(5, 6);
            findItem(popup, "Add Aspect").doClick();

            mocked.verify(() -> JtreeToGraphAdd.addNodeFromGraphPopup("Dec", 5, 6), times(1));
        }
    }

    @Test
    @DisplayName("TC-CM-05: Clicking 'Add MultiAspect' calls addNodeFromGraphPopup(MAsp,x,y)")
    void tc_cm_05_addMAspCallsStaticAdd() {
        try (MockedStatic<JtreeToGraphAdd> mocked = mockStatic(JtreeToGraphAdd.class)) {
            GraphPopup popup = new GraphPopup(7, 8);
            findItem(popup, "Add MultiAspect").doClick();

            mocked.verify(() -> JtreeToGraphAdd.addNodeFromGraphPopup("MAsp", 7, 8), times(1));
        }
    }
}