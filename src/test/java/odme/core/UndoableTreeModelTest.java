package odme.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoableEdit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link UndoableTreeModel}.
 * Covers insertion, removal, change edits, and undo/redo functionality.
 */
class UndoableTreeModelTest {

    private UndoableTreeModel model;
    private DefaultMutableTreeNode root;
    private TestUndoListener listener;

    static class TestUndoListener implements UndoableEditListener {
        UndoableEditEvent lastEvent;

        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            this.lastEvent = e;
        }
    }

    @BeforeEach
    void setup() {
        root = new DefaultMutableTreeNode("root");
        model = new UndoableTreeModel(root);
        listener = new TestUndoListener();
        model.addUndoableEditListener(listener);
    }

    @Test
    void shouldFireUndoableEditOnInsert() {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode("child");
        model.insertNodeInto(child, root, 0);

        // Verify tree structure updated
        assertEquals(1, root.getChildCount());
        assertEquals(child, root.getChildAt(0));

        // Verify event posted
        assertNotNull(listener.lastEvent, "Undoable edit should be posted for insertion");
        assertTrue(listener.lastEvent.getEdit() instanceof UndoableEdit);
    }

    @Test
    void shouldFireUndoableEditOnRemove() {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode("child");
        model.insertNodeInto(child, root, 0);
        listener.lastEvent = null;

        model.removeNodeFromParent(child);

        assertEquals(0, root.getChildCount(), "Child should be removed");
        assertNotNull(listener.lastEvent, "Undoable edit should be posted for removal");
    }

    @Test
    void shouldFireUndoableEditOnValueChange() {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode("old");
        model.insertNodeInto(child, root, 0);
        TreePath path = new TreePath(new Object[]{root, child});
        listener.lastEvent = null;

        model.valueForPathChanged(path, "new");

        assertEquals("new", child.getUserObject());
        assertNotNull(listener.lastEvent, "Undoable edit should be posted for value change");
    }

    @Test
    void nodeAddEditShouldUndoRedoProperly() {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode("child");
        model.insertNodeInto(child, root, 0);
        UndoableEdit edit = listener.lastEvent.getEdit();

        // Undo should remove the node
        edit.undo();
        assertEquals(0, root.getChildCount(), "Node should be removed after undo");

        // Redo should restore the node
        edit.redo();
        assertEquals(1, root.getChildCount(), "Node should be restored after redo");
    }

    @Test
    void nodeRemoveEditShouldUndoRedoProperly() {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode("child");
        model.insertNodeInto(child, root, 0);
        model.removeNodeFromParent(child);
        UndoableEdit edit = listener.lastEvent.getEdit();

        // Undo (should reinsert node)
        edit.undo();
        assertEquals(1, root.getChildCount());

        // Redo (should remove again)
        edit.redo();
        assertEquals(0, root.getChildCount());
    }

    @Test
    void nodeChangeEditShouldUndoRedoProperly() {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode("old");
        model.insertNodeInto(child, root, 0);
        TreePath path = new TreePath(new Object[]{root, child});
        model.valueForPathChanged(path, "new");
        UndoableEdit edit = listener.lastEvent.getEdit();

        // Undo should revert name
        edit.undo();
        assertEquals("old", child.getUserObject());

        // Redo should reapply new value
        edit.redo();
        assertEquals("new", child.getUserObject());
    }
}
