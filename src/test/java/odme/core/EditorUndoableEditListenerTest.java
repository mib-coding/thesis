package odme.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link EditorUndoableEditListener}.
 * This corrects imports and avoids getEdits() usage.
 */
class EditorUndoableEditListenerTest {

    private EditorUndoableEditListener listener;

    /** Simple test edit. */
    static class TestEdit extends AbstractUndoableEdit {
        private boolean called;
        @Override
        public void undo() {
            called = true;
        }
        public boolean wasCalled() { return called; }
    }

    @BeforeEach
    void setup() {
        listener = new EditorUndoableEditListener();

        // Reset static undo managers using correct imports
        odme.odmeeditor.ODMEEditor.undoJtree = new UndoManager();
        odeme.behaviour.ODMEBehaviourEditor.undoJtree = new UndoManager();
    }

    @Test
    void shouldAddEditToOdmeEditorUndoManager() {
        UndoManager manager = odme.odmeeditor.ODMEEditor.undoJtree;
        TestEdit edit = new TestEdit();
        UndoableEditEvent event = new UndoableEditEvent(this, edit);

        listener.undoableEditHappened(event);

        assertTrue(manager.canUndo() || manager.canRedo(),
                "UndoManager should contain at least one edit after event");
    }

    @Test
    void shouldAddEditToBehaviourEditorUndoManager() {
        UndoManager manager = odeme.behaviour.ODMEBehaviourEditor.undoJtree;
        TestEdit edit = new TestEdit();
        UndoableEditEvent event = new UndoableEditEvent(this, edit);

        listener.undoableEditHappenedBehavior(event);

        assertTrue(manager.canUndo() || manager.canRedo(),
                "Behaviour undo manager should contain an edit");
    }

    @Test
    void shouldHandleNullEditGracefully() {
        UndoableEditEvent event = new UndoableEditEvent(this, null);

        assertDoesNotThrow(() -> listener.undoableEditHappened(event),
                "Null edit should not cause exception");
        assertDoesNotThrow(() -> listener.undoableEditHappenedBehavior(event),
                "Null edit should not cause exception in behavior listener");
    }
}
