package odme.core;

import org.junit.jupiter.api.Test;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FlagVariables class.
 */
class FlagVariablesTest {

    @Test
    void shouldInitializeWithDefaultValues() {
        FlagVariables fv = new FlagVariables();
        assertEquals(0, fv.nodeNumber, "Expected default nodeNumber = 0");
        assertEquals(0, fv.uniformityNodeNumber, "Expected default uniformityNodeNumber = 0");
    }

    @Test
    void shouldAllowFieldModification() {
        FlagVariables fv = new FlagVariables();
        fv.nodeNumber = 10;
        fv.uniformityNodeNumber = 5;

        assertEquals(10, fv.nodeNumber);
        assertEquals(5, fv.uniformityNodeNumber);
    }

    @Test
    void shouldKeepInstancesIndependent() {
        FlagVariables a = new FlagVariables();
        FlagVariables b = new FlagVariables();

        a.nodeNumber = 3;
        b.nodeNumber = 7;

        assertNotEquals(a.nodeNumber, b.nodeNumber, "Each instance should have independent values");
    }

    @Test
    void shouldBeSerializable() throws Exception {
        FlagVariables fv = new FlagVariables();
        fv.nodeNumber = 42;
        fv.uniformityNodeNumber = 24;

        // Serialize
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(fv);
        oos.close();

        // Deserialize
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        FlagVariables copy = (FlagVariables) ois.readObject();

        assertEquals(42, copy.nodeNumber);
        assertEquals(24, copy.uniformityNodeNumber);
    }
}
