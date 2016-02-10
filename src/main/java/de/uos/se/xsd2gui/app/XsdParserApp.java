package de.uos.se.xsd2gui.app;

import de.uos.se.xsd2gui.generators.*;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.xsdparser.WidgetFactory;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
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
   private final DocumentBuilder _documentBuilder;
   private XSDModel _currentModel;

   public XsdParserApp() {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setIgnoringComments(true);
      factory.setIgnoringElementContentWhitespace(true);
      factory.setNamespaceAware(true);

      try {
         _documentBuilder = factory.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      }

   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {

      // add XSD Filename to arguments
      // TODO check if argument already exists :)
      String[] args2 = Arrays.copyOf(args, args.length + 1);
      args2[args.length] = "--xsdFiles=" + XSD_BASE_DIR + "config\\components";

      launch(args2);
   }

   @Override
   public void start(Stage primaryStage) {

      // get the XSD filename argument
      String xsdFilesname = this.getParameters().getNamed().get("xsdFiles");

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
      widgetFactory.addWidgetGenerator(new CustomTypesParser("ct:", XSD_BASE_DIR + "config\\predefined\\CommonTypes.xsd"));
      widgetFactory.addWidgetGenerator(new CustomTypesParser("st:", XSD_BASE_DIR + "config\\predefined\\StructuredTypes.xsd"));


      ComboBox<File> fc = new ComboBox<>();
      root.getChildren().add(fc);
      File dir = new File(xsdFilesname);
      fc.getItems().addAll(dir.listFiles(f -> f.isFile() && f.toString().endsWith(".xsd")));
      fc.valueProperty().addListener((observable, oldValue, newValue) -> {
         try {
            ObservableList<Node> rootChildren = root.getChildren();
            if (rootChildren.size() > 1)
               rootChildren.remove(1);
            VBox currentContent = new VBox();
            rootChildren.add(currentContent);
            Document doc = _documentBuilder.parse(newValue.getPath());
            // Generated widgets are added to the root node
            _currentModel = widgetFactory.parseXsd(doc, currentContent, newValue.getPath().replaceAll("\\" + File.separator, "/"));
         } catch (Exception ex) {
            Logger.getLogger(XsdParserApp.class.getName()).log(Level.SEVERE, null, ex);
         }
      });


      // Read the XSD and start parsing
      try {
         Document newDoc = _documentBuilder.newDocument();
         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            _currentModel.parseToXML(newDoc, null);
            try (FileOutputStream out = new FileOutputStream("out.xml")) {
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
