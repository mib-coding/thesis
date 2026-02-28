package odme.contextmenus;

import static org.mockito.Mockito.*;

import javax.swing.JTree;

import org.junit.jupiter.api.*;
import odme.odmeeditor.ODMEEditor;
import odme.odmeeditor.ProjectTree;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ProjectTreePopupTest {

    @BeforeAll
    static void headless() {
        System.setProperty("java.awt.headless", "true");
    }

    @BeforeEach
    void setup() {
        // ProjectTree is a concrete class in ODME; mocking avoids running real UI logic
        ODMEEditor.projectPanel = mock(ProjectTree.class);
    }

    @Test
    @DisplayName("TC-CM-06: ProjectTreePopup delete action calls projectPanel.removeCurrentNode()")
    void tc_cm_06_deleteCallsRemoveCurrentNode() {
        ProjectTreePopup popup = new ProjectTreePopup(new JTree());

        // directly call exposed method
        popup.popUpActionDeleteProjectTree();

        verify(ODMEEditor.projectPanel, times(1)).removeCurrentNode();
    }
}