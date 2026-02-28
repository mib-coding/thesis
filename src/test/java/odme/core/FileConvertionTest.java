// package odme.core;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.io.PrintWriter;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;

// import javax.swing.tree.DefaultMutableTreeNode;
// import javax.swing.tree.TreePath;

// import org.junit.jupiter.api.*;
// import org.junit.jupiter.api.io.TempDir;
// import org.mockito.MockedStatic;

// import odme.jtreetograph.JtreeToGraphGeneral;
// import odme.odmeeditor.ODMEEditor;

// /**
//  * Comprehensive JUnit 5 test suite for FileConvertion class.
//  * Verifies XML → XSD conversions, node additions, constraint handling,
//  * and related file operations.
//  */
// @TestMethodOrder(MethodOrderer.DisplayName.class)
// class FileConvertionTest {

//     private FileConvertion fileConvertion;

//     @TempDir
//     Path tempDir;

//     private String testFileLocation;
//     private final String testProjName = "testProject";
//     private final String testScenario = "testScenario";

//     @BeforeEach
//     void setUp() throws IOException {
//         fileConvertion = new FileConvertion();
//         testFileLocation = tempDir.toString();

//         // Configure static editor context
//         ODMEEditor.fileLocation = testFileLocation;
//         ODMEEditor.projName = testProjName;
//         ODMEEditor.currentScenario = testScenario;
//         ODMEEditor.toolMode = "ses";

//         Files.createDirectories(Paths.get(testFileLocation, testProjName));
//     }

//     @AfterEach
//     void tearDown() { /* @TempDir cleans automatically */ }

//     // -------------------------------------------------------------------------
//     // XML → XSD Conversion Tests
//     // -------------------------------------------------------------------------

//     @Test
//     @DisplayName("TC-Core8-01: Convert basic XML structure to valid XSD schema")
//     void testXmlToXSDConversion_BasicStructure() throws IOException {
//         String inputXml = "<?xml version=\"1.0\"?><TestEntity></TestEntity>";
//         createTestFile("/outputgraphxmlforxsd.xml", inputXml);

//         fileConvertion.xmlToXSDConversion();

//         File xsdFile = Paths.get(testFileLocation, testProjName, "xsdfromxml.xsd").toFile();
//         assertTrue(xsdFile.exists(), "XSD file should be created");
//         String content = Files.readString(xsdFile.toPath());
//         assertAll(
//             () -> assertTrue(content.contains("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"")),
//             () -> assertTrue(content.contains("<xs:schema")),
//             () -> assertTrue(content.contains("</xs:schema>"))
//         );
//     }

//     @Test
//     @DisplayName("TC-Core8-02: Handle Dec nodes as xs:sequence")
//     void testXmlToXSDConversion_DecNode() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><TestDec></TestDec>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.xmlToXSDConversion();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "xsdfromxml.xsd"));
//         assertTrue(content.contains("<xs:sequence"), "Dec → xs:sequence");
//     }

//     @Test
//     @DisplayName("TC-Core8-03: Handle MAsp nodes correctly")
//     void testXmlToXSDConversion_MAspNode() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><TestMAsp><ChildEntity/></TestMAsp>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.xmlToXSDConversion();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "xsdfromxml.xsd"));
//         assertTrue(content.contains("<xs:sequence"), "MAsp node handled");
//     }

//     @Test
//     @DisplayName("TC-Core8-04: Convert Spec nodes into xs:choice")
//     void testXmlToXSDConversion_SpecNode() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><TestSpec></TestSpec>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.xmlToXSDConversion();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "xsdfromxml.xsd"));
//         assertTrue(content.contains("<xs:choice"), "Spec → xs:choice");
//     }

//     // @Test
//     // @DisplayName("TC-Core8-05: Convert string variable definitions to XSD attributes")
//     // void testXmlToXSDConversion_VariableWithString() throws IOException { ... }

//     @Test
//     @DisplayName("TC-Core8-06: Generate numeric constraints (min/max) for integer variables")
//     void testXmlToXSDConversion_VariableWithNumericConstraints() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><TestEntity><myNum,integer,10,0,100,Var/></TestEntity>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.xmlToXSDConversion();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "xsdfromxml.xsd"));
//         assertAll(
//             () -> assertTrue(content.contains("<xs:minInclusive")),
//             () -> assertTrue(content.contains("<xs:maxInclusive"))
//         );
//     }

//     // @Test
//     // @DisplayName("TC-Core8-07: Create behaviour attributes for Behaviour nodes")
//     // void testXmlToXSDConversion_BehaviourNode() throws IOException { ... }

//     @Test
//     @DisplayName("TC-Core8-08: Create xs:assert for constraint nodes")
//     void testXmlToXSDConversion_ConstraintNode() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><TestEntity><@value > 0Con/></TestEntity>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.xmlToXSDConversion();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "xsdfromxml.xsd"));
//         assertTrue(content.contains("<xs:assert"));
//     }

//     @Test
//     @DisplayName("TC-Core8-09: Create element references for RefNode")
//     void testXmlToXSDConversion_RefNode() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><TestEntity><ReferencedNodeRefNode/></TestEntity>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.xmlToXSDConversion();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "xsdfromxml.xsd"));
//         assertTrue(content.contains("ref=\"ReferencedNode\""));
//     }

//     // -------------------------------------------------------------------------
//     // Node Manipulation Tests
//     // -------------------------------------------------------------------------

//     @Test
//     @DisplayName("TC-Core8-10: Add variable node under selected TreePath")
//     void testVariableAdditionToNode() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><Root><Parent><Child/></Parent></Root>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
//         DefaultMutableTreeNode parent = new DefaultMutableTreeNode("Parent");
//         DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
//         root.add(parent);
//         parent.add(child);

//         TreePath path = new TreePath(new Object[]{root, parent, child});
//         fileConvertion.variableAdditionToNode(path, "testVar,string,default,,,");

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "outputgraphxmlforxsd.xml"));
//         assertTrue(content.contains("Var"));
//     }

//     @Test
//     @DisplayName("TC-Core8-11: Add Behaviour marker to node")
//     void testBehaviourAdditionToNode() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><Root><Parent><Child/></Parent></Root>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
//         DefaultMutableTreeNode parent = new DefaultMutableTreeNode("Parent");
//         DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
//         root.add(parent); parent.add(child);

//         TreePath path = new TreePath(new Object[]{root, parent, child});
//         fileConvertion.behaviourAdditionToNode(path, "testBehaviour");

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "outputgraphxmlforxsd.xml"));
//         assertTrue(content.contains("Behaviour"));
//     }

//     @Test
//     @DisplayName("TC-Core8-12: Add constraint to node via TreePath")
//     void testConstraintAdditionToNode_WithTreePath() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><Root><Target/></Root>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
//         DefaultMutableTreeNode target = new DefaultMutableTreeNode("Target");
//         root.add(target);

//         fileConvertion.constraintAdditionToNode(
//             new TreePath(new Object[]{root, target}), "@value > 0");

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "outputgraphxmlforxsd.xml"));
//         assertTrue(content.contains("Con"));
//     }

//     @Test
//     @DisplayName("TC-Core8-13: Add uniformity RefNode element")
//     void testAddingUniformityRefNodeToXML() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><Root><Parent></Parent></Root>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.addingUniformityRefNodeToXML(new String[]{"Root","Parent"}, "ReferencedNode");

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "outputgraphxmlforxsd.xml"));
//         assertTrue(content.contains("RefNode"));
//     }

//     @Test
//     @DisplayName("TC-Core8-14: Fix nested sequence structure using Seq wrapper")
//     void testFixingSequenceProblem() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><Root><Parent><Child1/><Child2/></Parent></Root>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.fixingSequenceProblem(new String[]{"Root", "Parent"});

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "outputgraphxmlforxsd.xml"));
//         assertAll(
//             () -> assertTrue(content.contains("<Seq>")),
//             () -> assertTrue(content.contains("</Seq>"))
//         );
//     }

//     @Test
//     @DisplayName("TC-Core8-15: Modify XML output for RefNode handling")
//     void testModifyXmlOutputForRefNode() throws IOException {
//         createTestFile("/outputgraphxmlforxsdvar.xml",
//             "<?xml version=\"1.0\"?><Root><SelfClosing/></Root>");

//         fileConvertion.modifyXmlOutputForRefNode();

//         assertTrue(Paths.get(testFileLocation, testProjName, "outputgraphxmlforxsd.xml").toFile().exists());
//     }

//     @Test
//     @DisplayName("TC-Core8-16: Remove <start> tags when modifying XML for XSD")
//     void testModifyXmlOutputForXSD() throws IOException {
//         createTestFile("/graphxmluniformity.xml",
//             "<?xml version=\"1.0\"?><start><Root/></start>");

//         fileConvertion.modifyXmlOutputForXSD();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "outputgraphxmlforxsd.xml"));
//         assertFalse(content.contains("<start>"));
//     }

//     @Test
//     @DisplayName("TC-Core8-17: Create SES XSD skeleton with expected types")
//     void testCreateSES() throws IOException {
//         String path = Paths.get(testFileLocation, testProjName, "ses.xsd").toString();
//         fileConvertion.createSES(path);

//         String content = Files.readString(Paths.get(path));
//         assertAll(
//             () -> assertTrue(content.contains("aspectType")),
//             () -> assertTrue(content.contains("multiAspectType")),
//             () -> assertTrue(content.contains("specializationType")),
//             () -> assertTrue(content.contains("varType")),
//             () -> assertTrue(content.contains("behaviourType"))
//         );
//     }

//     @Test
//     @DisplayName("TC-Core8-18: Handle scenario mode paths correctly")
//     void testToolMode_Scenario() throws IOException {
//         ODMEEditor.toolMode = "scenario";
//         Files.createDirectories(Paths.get(testFileLocation, testScenario));

//         createTestFile("/outputgraphxmlforxsd.xml", "<?xml version=\"1.0\"?><Root/>", true);

//         fileConvertion.xmlToXSDConversion();

//         assertTrue(Paths.get(testFileLocation, testScenario, "xsdfromxml.xsd").toFile().exists());
//     }

//     @Test
//     @DisplayName("TC-Core8-19: Add constraint attribute to SES structure")
//     void testAddConstraintToSESStructure() throws IOException {
//         File xmlFile = Paths.get(testFileLocation, "xmlforxsd.xml").toFile();
//         try (PrintWriter pw = new PrintWriter(new FileWriter(xmlFile))) {
//             pw.println("<Root name=\"root\"><Aspect name=\"testAspect\"></Aspect></Root>");
//         }

//         fileConvertion.addConstraintToSESStructure(new String[]{"Root","Aspect"}, "@value > 0");

//         String content = Files.readString(Paths.get(testFileLocation, "testcon.xml"));
//         assertTrue(content.contains("constraint=\"@value > 0\""));
//     }

//     @Test
//     @DisplayName("TC-Core8-20: Copy XSD to root node name file (static mock)")
//     void testCopyxsdfromxmlToRootNodeNameXSD() throws IOException {
//         createTestFile("/outputgraphxmlforxsdvar.xml", "<?xml version=\"1.0\"?><xs:schema/>");
//         try (MockedStatic<JtreeToGraphGeneral> mocked = mockStatic(JtreeToGraphGeneral.class)) {
//             mocked.when(JtreeToGraphGeneral::rootNodeName).thenReturn("TestRoot");
//             fileConvertion.copyxsdfromxmlToRootNodeNameXSD();
//         }
//         assertTrue(Paths.get(testFileLocation, testProjName, "TestRoot.xsd").toFile().exists());
//     }

//     @Test
//     @DisplayName("TC-Core8-21: Validate complex nested XML conversion (Dec + MAsp + Var)")
//     void testXmlToXSDConversion_ComplexStructure() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><SystemDec><ComponentMAsp><ProcessorEntity><speed,integer,100,0,200,Var/></ProcessorEntity></ComponentMAsp></SystemDec>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.xmlToXSDConversion();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "xsdfromxml.xsd"));
//         assertAll(
//             () -> assertTrue(content.contains("<xs:schema")),
//             () -> assertTrue(content.contains("<xs:sequence")),
//             () -> assertTrue(content.contains("minOccurs=\"0\"")),
//             () -> assertTrue(content.contains("speed"))
//         );
//     }

//     // @Test
//     // @DisplayName("TC-Core8-22: Add constraint to root-level selected node")
//     // void testConstraintAdditionToNode_WithSelectedNode() throws IOException { ... }

//     @Test
//     @DisplayName("TC-Core8-23: Place xs:assert at correct position in XSD")
//     void testPlaceAssertInRightPosition() throws IOException {
//         createTestFile("/xsdfromxml.xsd",
//             "<?xml version=\"1.0\"?><xs:schema><xs:element name=\"t\"><xs:assert test=\"@v>0\"/><xs:attribute name=\"n\"/></xs:element></xs:schema>");

//         try (MockedStatic<JtreeToGraphGeneral> mocked = mockStatic(JtreeToGraphGeneral.class)) {
//             mocked.when(JtreeToGraphGeneral::rootNodeName).thenReturn("TestRoot");
//             fileConvertion.placeAssertInRightPosition();
//         }

//         assertTrue(Paths.get(testFileLocation, testProjName, "outputgraphxmlforxsdvar.xml").toFile().exists());
//     }

//     @Test
//     @DisplayName("TC-Core8-24: Handle Seq node conversion to xs:sequence")
//     void testXmlToXSDConversion_SeqNode() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><TestSeq></TestSeq>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.xmlToXSDConversion();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "xsdfromxml.xsd"));
//         assertTrue(content.contains("<xs:sequence"));
//     }

//     @Test
//     @DisplayName("TC-Core8-25: Handle DecRefNode as sequence reference")
//     void testXmlToXSDConversion_RefNodeDec() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><TestEntity><ReferencedDecRefNode/></TestEntity>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.xmlToXSDConversion();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "xsdfromxml.xsd"));
//         assertTrue(content.contains("ref=\"ReferencedDec\""));
//     }

//     @Test
//     @DisplayName("TC-Core8-26: Handle SpecRefNode as choice reference")
//     void testXmlToXSDConversion_RefNodeSpec() throws IOException {
//         String xml = "<?xml version=\"1.0\"?><TestEntity><ReferencedSpecRefNode/></TestEntity>";
//         createTestFile("/outputgraphxmlforxsd.xml", xml);

//         fileConvertion.xmlToXSDConversion();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "xsdfromxml.xsd"));
//         assertTrue(content.contains("ref=\"ReferencedSpec\""));
//     }

//     @Test
//     @DisplayName("TC-Core8-27: Copy var file to existing output XSD")
//     void testCopyFileToExistingOne() throws IOException {
//         createTestFile("/outputgraphxmlforxsdvar.xml", "<?xml version=\"1.0\"?><Root/>");
//         fileConvertion.copyFileToExistingOne();
//         assertTrue(Paths.get(testFileLocation, testProjName, "outputgraphxmlforxsd.xml").toFile().exists());
//     }

//     @Test
//     @DisplayName("TC-Core8-28: Copy sequence-fixed file to output XSD")
//     void testCopyfixingSequenceFileToExistingOne() throws IOException {
//         createTestFile("/outputgraphxmlforxsdseq.xml", "<?xml version=\"1.0\"?><Root/>");
//         fileConvertion.copyfixingSequenceFileToExistingOne();
//         assertTrue(Paths.get(testFileLocation, testProjName, "outputgraphxmlforxsd.xml").toFile().exists());
//     }

//     @Test
//     @DisplayName("TC-Core8-29: Ensure <start> tags fully removed from modified XML")
//     void testModifyXmlOutputForXSD_RemovesStartTags() throws IOException {
//         createTestFile("/graphxmluniformity.xml",
//             "<?xml version=\"1.0\"?><start><Root/></start><start/>");

//         fileConvertion.modifyXmlOutputForXSD();

//         String content = Files.readString(
//             Paths.get(testFileLocation, testProjName, "outputgraphxmlforxsd.xml"));
//         for (String line : content.split("\n")) {
//             assertFalse(line.trim().endsWith("start>"));
//         }
//     }

//     // -------------------------------------------------------------------------
//     // Helpers
//     // -------------------------------------------------------------------------

//     private void createTestFile(String relativePath, String content) throws IOException {
//         createTestFile(relativePath, content, false);
//     }

//     private void createTestFile(String relativePath, String content, boolean useScenario) throws IOException {
//         String base = (useScenario && ODMEEditor.toolMode.equals("scenario"))
//             ? Paths.get(testFileLocation, testScenario).toString()
//             : Paths.get(testFileLocation, testProjName).toString();

//         File file = new File(base + relativePath);
//         file.getParentFile().mkdirs();
//         try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
//             writer.print(content);
//         }
//     }
// }
