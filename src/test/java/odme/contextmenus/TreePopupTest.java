package odme.contextmenus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import odme.jtreetograph.JtreeToGraphAdd;
import odme.odmeeditor.DynamicTree;
import odme.odmeeditor.Main;
import odme.odmeeditor.ODMEEditor;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class TreePopupTest {

    @BeforeAll
    static void headless() {
        System.setProperty("java.awt.headless", "true");
        Main.frame = new JFrame(); // required by JOptionPane calls
    }

    @BeforeEach
    void setup() {
        DynamicTree treePanel = mock(DynamicTree.class);
        treePanel.tree = new JTree();
        ODMEEditor.treePanel = treePanel;

        DynamicTree.varMap.clear();
        ODMEEditor.nodeName = null;
    }

    private JMenuItem findItem(TreePopup popup, String text) {
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
    @DisplayName("TC-CM-16: Delete Node menu triggers treePanel.removeCurrentNode()")
    void tc_cm_16_deleteNodeCallsRemoveCurrentNode() {
        TreePopup popup = new TreePopup(new JTree());

        findItem(popup, "Delete Node").doClick();

        verify(ODMEEditor.treePanel, times(1)).removeCurrentNode();
    }

    @Test
    @DisplayName("TC-CM-17: Add Node trims whitespace and calls addObject + addNodeWithJtreeAddition")
    void tc_cm_17_addNodeCallsGraphAdd() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("RootDec");
        DefaultMutableTreeNode selected = new DefaultMutableTreeNode("ParentDec");
        root.add(selected);

        TreePath selectionPath = new TreePath(new Object[]{root, selected});
        ODMEEditor.treePanel.tree.setSelectionPath(selectionPath);

        try (MockedStatic<JOptionPane> jp = mockStatic(JOptionPane.class);
             MockedStatic<JtreeToGraphAdd> add = mockStatic(JtreeToGraphAdd.class)) {

            jp.when(() -> JOptionPane.showInputDialog(any(), anyString(), anyString(), anyInt()))
              .thenReturn(" New Node ");

            TreePopup popup = new TreePopup(new JTree());
            findItem(popup, "Add Node").doClick();

            assertEquals("NewNode", ODMEEditor.nodeName);

            verify(ODMEEditor.treePanel, times(1)).addObject("NewNode");

            add.verify(() -> JtreeToGraphAdd.addNodeWithJtreeAddition(eq("NewNode"), argThat(arr ->
                arr.length == 2 && "RootDec".equals(arr[0]) && "ParentDec".equals(arr[1])
            )), times(1));
        }
    }

    @Test
    @DisplayName("TC-CM-18: Add Node cancelled (null) performs no actions")
    void tc_cm_18_addNodeNullDoesNothing() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("RootDec");
        ODMEEditor.treePanel.tree.setSelectionPath(new TreePath(root));

        try (MockedStatic<JOptionPane> jp = mockStatic(JOptionPane.class);
             MockedStatic<JtreeToGraphAdd> add = mockStatic(JtreeToGraphAdd.class)) {

            jp.when(() -> JOptionPane.showInputDialog(any(), anyString(), anyString(), anyInt()))
              .thenReturn(null);

            TreePopup popup = new TreePopup(new JTree());
            findItem(popup, "Add Node").doClick();

            verify(ODMEEditor.treePanel, never()).addObject(anyString());
            add.verifyNoInteractions();
        }
    }

    @Test
    @DisplayName("TC-CM-19: Add Variable with OK_OPTION stores varMap and refreshes variable table")
    void tc_cm_19_addVariableStoresMapAndRefreshes() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Entity");
        root.add(node);

        TreePath selectionPath = new TreePath(new Object[]{root, node});
        ODMEEditor.treePanel.tree.setSelectionPath(selectionPath);

        try (MockedStatic<JOptionPane> jp = mockStatic(JOptionPane.class)) {

            jp.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
              .thenAnswer(inv -> {
                  Object[] message = (Object[]) inv.getArgument(1);

                  ((JTextField) message[1]).setText("vName");
                  ((JTextField) message[3]).setText("int");
                  ((JTextField) message[5]).setText("10");
                  ((JTextField) message[7]).setText("0");
                  ((JTextField) message[9]).setText("100");
                  ((JTextField) message[11]).setText("cmt");

                  return JOptionPane.OK_OPTION;
              });

            TreePopup popup = new TreePopup(new JTree());
            findItem(popup, "Add Variable").doClick();

            assertTrue(DynamicTree.varMap.containsKey(selectionPath), "varMap should contain selection path");
            assertTrue(DynamicTree.varMap.get(selectionPath).contains("vName,int,10,0,100,cmt"));

            verify(ODMEEditor.treePanel, times(1)).refreshVariableTable(selectionPath);
        }
    }
}