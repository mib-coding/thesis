// package xml.schema;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.io.TempDir;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import static org.junit.jupiter.api.Assertions.*;

// /**
//  * Tests for schema generation and output.
//  */
// class TypeInfoWriterTest {

//     @Test
//     void shouldWriteSchemaToFile(@TempDir Path tempDir) throws Exception {
//         Path out = tempDir.resolve("schema.xsd");
//         TypeInfoWriter writer = new TypeInfoWriter();
//         writer.writeSchemaToFile(out.toString());
//         assertTrue(Files.exists(out));
//         String schema = Files.readString(out);
//         assertTrue(schema.contains("<xs:schema"));
//     }

//     @Test
//     void shouldHandleNullInputGracefully() {
//         TypeInfoWriter writer = new TypeInfoWriter();
//         assertDoesNotThrow(() -> writer.writeSchemaToFile(null));
//     }
// }
