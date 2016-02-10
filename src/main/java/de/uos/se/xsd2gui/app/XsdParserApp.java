package de.uos.se.xsd2gui.app;

import de.uos.se.xsd2gui.generators.*;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.xsdparser.WidgetFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

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
      args2[args.length] = "--xsdFile=" + XSD_BASE_DIR + "config\\components\\PWM.xsd";

      launch(args2);
   }

   @Override
   public void start(Stage primaryStage) {

      // get the XSD filename argument
      String xsdFilename = this.getParameters().getNamed().get("xsdFile");

      // JavaFX SceneGraph root element.
      VBox root = new VBox();

      // Create the main widget generator controller with the shared namespace.
      WidgetFactory widgetFactory = new WidgetFactory();

      // Add the Generators
      // TODO create missing parsers (e.g. for sequence tags)
      widgetFactory.addWidgetGenerator(new BasicAttributeParser());
      widgetFactory.addWidgetGenerator(new SimpleTypeParser());
      widgetFactory.addWidgetGenerator(new ContainerParser());
      widgetFactory.addWidgetGenerator(new BasicSequenceParser());
      widgetFactory.addWidgetGenerator(new CustomTypesParser("ct:", XSD_BASE_DIR + "config\\Components\\CommonTypes.xsd"));
      widgetFactory.addWidgetGenerator(new CustomTypesParser("st:", XSD_BASE_DIR + "config\\Components\\StructuredTypes.xsd"));

      // Read the XSD and start parsing
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setIgnoringComments(true);
         factory.setIgnoringElementContentWhitespace(true);
         factory.setNamespaceAware(true);

         DocumentBuilder documentBuilder = factory.newDocumentBuilder();
         Document doc = documentBuilder.parse(xsdFilename);

         // Generated widgets are added to the root node
         XSDModel xsdModel = widgetFactory.parseXsd(doc, root);
         Document newDoc = documentBuilder.newDocument();
         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            xsdModel.parseToXML(newDoc, null);
            try (FileOutputStream out = new FileOutputStream("text.xml")) {
               TransformerFactory tFactory =
                     TransformerFactory.newInstance();
               Transformer transformer =
                     tFactory.newTransformer();

               DOMSource source = new DOMSource(newDoc);
               StreamResult result = new StreamResult(out);
               transformer.transform(source, result);
            } catch (IOException | TransformerException e) {
               e.printStackTrace();
            }
         }));


      } catch (Exception ex) {
         Logger.getLogger(XsdParserApp.class.getName()).log(Level.SEVERE, null, ex);
      }

      Scene scene = new Scene(root, 1200, 750);

      primaryStage.setTitle("XSD-to-GUI Prototype");
      primaryStage.setScene(scene);
      primaryStage.show();
   }

}
