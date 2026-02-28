package odme.contextmenus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Component;

import javax.swing.JMenuItem;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import com.mxgraph.model.mxCell;

import odme.jtreetograph.JtreeToGraphAdd;
import odme.jtreetograph.JtreeToGraphCheck;
import odme.jtreetograph.JtreeToGraphDelete;
import odme.jtreetograph.JtreeToGraphPrune;
import odme.odmeeditor.ODMEEditor;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class GraphCellPopUpTest {

    @BeforeAll
    static void headless() {
        System.setProperty("java.awt.headless", "true");
    }

    private JMenuItem findItem(GraphCellPopUp popup, String text) {
        for (Component c : popup.getComponents()) {
            if (c instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) c;
                if (text.equals(item.getText())) return item;
            }
        }
        return null;
    }

    private mxCell vertexCell(String id, String value) {
        mxCell cell = new mxCell(value);
        cell.setId(id);
        cell.setVertex(true);
        return cell;
    }

    private mxCell edgeCell(String id, String value) {
        mxCell cell = new mxCell(value);
        cell.setId(id);
        cell.setVertex(false);
        cell.setEdge(true);
        return cell;
    }

    @Test
    @DisplayName("TC-CM-07: In SES mode, vertex cell shows core SES menu items")
    void tc_cm_07_sesVertexHasCoreItems() {
        ODMEEditor.toolMode = "ses"; // production uses '==', keep literal

        mxCell cell = vertexCell("n1", "SomeEntity");
        try (MockedStatic<JtreeToGraphCheck> check = mockStatic(JtreeToGraphCheck.class)) {
            check.when(() -> JtreeToGraphCheck.isConnectedToRoot(cell)).thenReturn(false);

            GraphCellPopUp popup = new GraphCellPopUp(cell);

            assertNotNull(findItem(popup, "Add Variable"));
            assertNotNull(findItem(popup, "Rename"));
            assertNotNull(findItem(popup, "Delete Variable"));
            assertNotNull(findItem(popup, "Delete All Variables"));
            assertNotNull(findItem(popup, "Delete Branch"));
            assertNotNull(findItem(popup, "Add Module"));
            assertNotNull(findItem(popup, "Save Module"));
            assertNotNull(findItem(popup, "Add Behaviour"));
        }
    }

    @Test
    @DisplayName("TC-CM-08: If vertex ends with Dec, constraint menu items appear")
    void tc_cm_08_decAddsConstraintItems() {
        ODMEEditor.toolMode = "ses";

        mxCell cell = vertexCell("n2", "RootDec");
        try (MockedStatic<JtreeToGraphCheck> check = mockStatic(JtreeToGraphCheck.class)) {
            check.when(() -> JtreeToGraphCheck.isConnectedToRoot(cell)).thenReturn(false);

            GraphCellPopUp popup = new GraphCellPopUp(cell);

            assertNotNull(findItem(popup, "Add Constraint"));
            assertNotNull(findItem(popup, "Delete All Constraint"));
        }
    }

    @Test
    @DisplayName("TC-CM-09: Edge cell shows 'Delete Edge' and triggers deleteEdgeFromGraphPopup")
    void tc_cm_09_edgeDeleteTriggersStaticDelete() {
        ODMEEditor.toolMode = "ses";

        mxCell cell = edgeCell("e1", "edge");
        try (MockedStatic<JtreeToGraphCheck> check = mockStatic(JtreeToGraphCheck.class);
             MockedStatic<JtreeToGraphDelete> del = mockStatic(JtreeToGraphDelete.class)) {

            check.when(() -> JtreeToGraphCheck.isConnectedToRoot(cell)).thenReturn(false);

            GraphCellPopUp popup = new GraphCellPopUp(cell);

            JMenuItem deleteEdge = findItem(popup, "Delete Edge");
            assertNotNull(deleteEdge);

            deleteEdge.doClick();
            del.verify(() -> JtreeToGraphDelete.deleteEdgeFromGraphPopup(cell), times(1));
        }
    }

    @Test
    @DisplayName("TC-CM-10: Clicking 'Add Variable' triggers JtreeToGraphAdd.addVariableFromGraphPopup")
    void tc_cm_10_addVariableTriggersStaticAdd() {
        ODMEEditor.toolMode = "ses";

        mxCell cell = vertexCell("n3", "MyEntity");
        try (MockedStatic<JtreeToGraphCheck> check = mockStatic(JtreeToGraphCheck.class);
             MockedStatic<JtreeToGraphAdd> add = mockStatic(JtreeToGraphAdd.class)) {

            check.when(() -> JtreeToGraphCheck.isConnectedToRoot(cell)).thenReturn(false);

            GraphCellPopUp popup = new GraphCellPopUp(cell);
            JMenuItem item = findItem(popup, "Add Variable");
            assertNotNull(item);

            item.doClick();
            add.verify(() -> JtreeToGraphAdd.addVariableFromGraphPopup(cell), times(1));
        }
    }

    @Test
    @DisplayName("TC-CM-11: In non-SES mode, non-Dec cell shows 'Prune It'")
    void tc_cm_11_nonSesShowsPruneItForNonDec() {
        ODMEEditor.toolMode = "scenario";

        mxCell cell = vertexCell("n4", "SomeSpec");
        try (MockedStatic<JtreeToGraphCheck> check = mockStatic(JtreeToGraphCheck.class)) {
            check.when(() -> JtreeToGraphCheck.isConnectedToRoot(cell)).thenReturn(false);

            GraphCellPopUp popup = new GraphCellPopUp(cell);
            assertNotNull(findItem(popup, "Prune It"));
        }
    }

    @Test
    @DisplayName("TC-CM-12: In non-SES mode, Dec cell does NOT show 'Prune It'")
    void tc_cm_12_nonSesDecDoesNotShowPruneIt() {
        ODMEEditor.toolMode = "scenario";

        mxCell cell = vertexCell("n5", "AnyDec");
        try (MockedStatic<JtreeToGraphCheck> check = mockStatic(JtreeToGraphCheck.class)) {
            check.when(() -> JtreeToGraphCheck.isConnectedToRoot(cell)).thenReturn(false);

            GraphCellPopUp popup = new GraphCellPopUp(cell);
            assertNull(findItem(popup, "Prune It"));
        }
    }

    @Test
    @DisplayName("TC-CM-13: Prune It on MAsp calls pruneMAspNodeFromGraphPopup")
    void tc_cm_13_pruneMAspCallsCorrectStatic() {
        ODMEEditor.toolMode = "scenario";

        mxCell cell = vertexCell("n6", "ComponentMAsp");
        try (MockedStatic<JtreeToGraphCheck> check = mockStatic(JtreeToGraphCheck.class);
             MockedStatic<JtreeToGraphPrune> prune = mockStatic(JtreeToGraphPrune.class)) {

            check.when(() -> JtreeToGraphCheck.isConnectedToRoot(cell)).thenReturn(false);

            GraphCellPopUp popup = new GraphCellPopUp(cell);
            JMenuItem pruneItem = findItem(popup, "Prune It");
            assertNotNull(pruneItem);

            pruneItem.doClick();
            prune.verify(() -> JtreeToGraphPrune.pruneMAspNodeFromGraphPopup(cell), times(1));
        }
    }

    @Test
    @DisplayName("TC-CM-14: Prune It on Spec calls pruneNodeFromGraphPopup")
    void tc_cm_14_pruneSpecCallsCorrectStatic() {
        ODMEEditor.toolMode = "scenario";

        mxCell cell = vertexCell("n7", "ChoiceSpec");
        try (MockedStatic<JtreeToGraphCheck> check = mockStatic(JtreeToGraphCheck.class);
             MockedStatic<JtreeToGraphPrune> prune = mockStatic(JtreeToGraphPrune.class)) {

            check.when(() -> JtreeToGraphCheck.isConnectedToRoot(cell)).thenReturn(false);

            GraphCellPopUp popup = new GraphCellPopUp(cell);
            JMenuItem pruneItem = findItem(popup, "Prune It");
            assertNotNull(pruneItem);

            pruneItem.doClick();
            prune.verify(() -> JtreeToGraphPrune.pruneNodeFromGraphPopup(cell), times(1));
        }
    }

    @Test
    @DisplayName("TC-CM-15: Prune It on other node calls pruneSiblingsFromGraphPopup")
    void tc_cm_15_pruneOtherCallsCorrectStatic() {
        ODMEEditor.toolMode = "scenario";

        mxCell cell = vertexCell("n8", "PlainEntity");
        try (MockedStatic<JtreeToGraphCheck> check = mockStatic(JtreeToGraphCheck.class);
             MockedStatic<JtreeToGraphPrune> prune = mockStatic(JtreeToGraphPrune.class)) {

            check.when(() -> JtreeToGraphCheck.isConnectedToRoot(cell)).thenReturn(false);

            GraphCellPopUp popup = new GraphCellPopUp(cell);
            JMenuItem pruneItem = findItem(popup, "Prune It");
            assertNotNull(pruneItem);

            pruneItem.doClick();
            prune.verify(() -> JtreeToGraphPrune.pruneSiblingsFromGraphPopup(cell), times(1));
        }
    }
}