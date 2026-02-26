package odeme.behaviour;

import odme.jtreetograph.JtreeToGraphDelete;
import odme.jtreetograph.JtreeToGraphVariables;
import odme.odmeeditor.Main;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BehaviourTest {

    @BeforeEach
    void setup() {
        // Reset static references before each test
        Behaviour.model = null;
        Behaviour.table = null;
    }

    @Test
    void shouldInitializeWithTableAndModel() {
        Behaviour behaviour = new Behaviour();

        assertNotNull(Behaviour.table, "Table should be initialized");
        assertNotNull(Behaviour.model, "Model should be initialized");
        assertEquals(100, Behaviour.model.getRowCount(), "Should initialize 100 empty rows");
    }

    @Test
    void shouldResetRowsWhenSetNullToAllRowsCalled() {
        Behaviour behaviour = new Behaviour();
        DefaultTableModel model = Behaviour.model;

        model.addRow(new Object[]{"ExtraRow"});
        assertTrue(model.getRowCount() > 100);

        Behaviour.setNullToAllRows();
        assertEquals(100, model.getRowCount(), "After reset, should contain exactly 100 rows");
        assertEquals("", model.getValueAt(0, 0), "Rows should be empty after reset");
    }

    @Test
    void shouldShowBehavioursInTableCorrectly() {
        Behaviour behaviour = new Behaviour();

        String[] nodes = {"B1", "B2"};
        behaviour.showBehavioursInTable("Node1", nodes);

        DefaultTableModel model = Behaviour.model;
        assertEquals("Node1", model.getValueAt(0, 0));
        assertEquals("B1", model.getValueAt(0, 1));
        assertEquals("Node1", model.getValueAt(1, 0));
        assertEquals("B2", model.getValueAt(1, 1));
    }

    @Test
    void shouldShowBehaviourInTableWithParsedValues() {
        Behaviour behaviour = new Behaviour();

        String[] data = {"val1,extra", "val2,extra"};
        behaviour.showBehaviourInTable("NodeX", data);

        DefaultTableModel model = Behaviour.model;
        assertEquals("NodeX", model.getValueAt(0, 0));
        assertEquals("val1", model.getValueAt(0, 1));
        assertEquals("NodeX", model.getValueAt(1, 0));
        assertEquals("val2", model.getValueAt(1, 1));
    }

    @Test
    void shouldHandleEmptyBehaviourArrayGracefully() {
        Behaviour behaviour = new Behaviour();
        behaviour.showBehavioursInTable("NodeZ", new String[]{});

        DefaultTableModel model = Behaviour.model;
        assertEquals(100, model.getRowCount(), "Should still have 100 rows even with empty input");
    }

    @Test
    void shouldInvokeUpdateBehaviourOnDoubleClick() {
        Behaviour behaviour = new Behaviour();
        DefaultTableModel model = Behaviour.model;

        model.setValueAt("Node1", 0, 0);
        model.setValueAt("Behaviour1", 0, 1);

        try (
                MockedStatic<JtreeToGraphDelete> mockedStaticDelete = mockStatic(JtreeToGraphDelete.class);
                MockedStatic<JtreeToGraphVariables> mockedStaticVars = mockStatic(JtreeToGraphVariables.class);
                MockedStatic<JOptionPane> mockedStaticOption = mockStatic(JOptionPane.class)
            ) 
        {
                mockedStaticOption.when(() -> JOptionPane.showConfirmDialog(
                    any(Component.class), any(Object[].class), anyString(), anyInt(), anyInt()
                )).thenReturn(JOptionPane.OK_OPTION);

                // ✅ For void static method
                mockedStaticDelete.when(() ->
                    JtreeToGraphDelete.deleteBehaviourFromScenarioTableForUpdate(any(), anyString(), anyString())
                    ).thenAnswer(invocation -> null);  // OR doNothing().when(...)

                Behaviour behaviourPanel = new Behaviour();
                behaviourPanel.showBehaviourInTable("Node", new String[]{"a,b"});

                // Simulate the double-click
                JTable table = Behaviour.table;
                MouseListener[] listeners = table.getMouseListeners();
                MouseEvent event = new MouseEvent(table, MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), 0, 1, 1, 2, false);
                    for (MouseListener l : listeners) {
                        l.mouseClicked(event);
                    }

                // Verify it was called once
                mockedStaticDelete.verify(() ->
                    JtreeToGraphDelete.deleteBehaviourFromScenarioTableForUpdate(any(), anyString(), anyString()), times(1)
                );
        }

    }

    @Test
    void shouldNotCrashWhenNullValuesInTable() {
        Behaviour behaviour = new Behaviour();
        DefaultTableModel model = Behaviour.model;
        model.setValueAt(null, 0, 0);
        model.setValueAt(null, 0, 1);

        assertDoesNotThrow(() -> {
            MouseEvent e = new MouseEvent(Behaviour.table, MouseEvent.MOUSE_CLICKED,
                    System.currentTimeMillis(), 0, 10, 10, 2, false);
            for (MouseListener listener : Behaviour.table.getMouseListeners()) {
                listener.mouseClicked(e);
            }
        });
    }
}
