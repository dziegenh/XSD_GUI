package de.uos.se.xsd2gui.app;

import de.uos.se.xsd2gui.generators.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import de.uos.se.xsd2gui.xsdparser.WidgetGeneratorController;

/**
 * Test application for the XSD-to-GUI parser/generator.
 *
 * @author dziegenhagen
 */
public class XsdParserApp extends Application {

    private static final String XSD_BASE_DIR = "src\\main\\resources\\";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // add XSD Filename to arguments 
        // TODO check if argument already exists :)
        String[] args2 = Arrays.copyOf(args, args.length + 1);
        args2[args.length] = "--xsdFile=" + XSD_BASE_DIR + "config\\components\\Debug.xsd";

        launch(args2);
    }

    @Override
    public void start(Stage primaryStage) {

        // get the XSD filename argument
        String xsdFilename = this.getParameters().getNamed().get("xsdFile");

        // JavaFX SceneGraph root element.
        VBox root = new VBox();

        // The Parsers/Generators can use this common namespace context.
        NamespaceContext namespaceContext = new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                if (prefix == null) {
                    throw new NullPointerException("Null prefix");
                } else if ("xs".equals(prefix)) {
                    return "http://www.w3.org/2001/XMLSchema";
                }
                return XMLConstants.NULL_NS_URI;
            }

            // not used
            public String getPrefix(String uri) {
                return null;
            }

            // not used
            public Iterator getPrefixes(String uri) {
                return null;
            }
        };

        // Create the main widget generator controller with the shared namespace.
        WidgetGeneratorController widgetGeneratorController = new WidgetGeneratorController();
        widgetGeneratorController.setDefaultNamespaceContext(namespaceContext);

        // Add the Generators
        // TODO create missing parsers (e.g. for sequence tags)
        widgetGeneratorController.addWidgetGenerator(new BasicAttributeParser());
        widgetGeneratorController.addWidgetGenerator(new SimpleTypeParser());
        widgetGeneratorController.addWidgetGenerator(new ContainerParser());
        widgetGeneratorController.addWidgetGenerator(new BasicSequenceParser());
        widgetGeneratorController.addWidgetGenerator(new CustomTypesParser("ct:", XSD_BASE_DIR + "config\\Components\\CommonTypes.xsd"));
        widgetGeneratorController.addWidgetGenerator(new CustomTypesParser("st:", XSD_BASE_DIR + "config\\Components\\StructuredTypes.xsd"));

        // Read the XSD and start parsing
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            factory.setNamespaceAware(true);

            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(xsdFilename);

            // Generated widgets are added to the root node
            widgetGeneratorController.parseXsd(doc, root);

        } catch (Exception ex) {
            Logger.getLogger(XsdParserApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        Scene scene = new Scene(root, 1200, 750);

        primaryStage.setTitle("XSD-to-GUI Prototype");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
