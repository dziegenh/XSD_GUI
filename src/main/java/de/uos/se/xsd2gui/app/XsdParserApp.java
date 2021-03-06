package de.uos.se.xsd2gui.app;

import de.uos.se.xsd2gui.model_generators.*;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.value_generators.DefaultValueGenerator;
import de.uos.se.xsd2gui.value_generators.LoadValueGenerator;
import de.uos.se.xsd2gui.xsdparser.DefaultWidgetFactory;
import de.uos.se.xsd2gui.xsdparser.IWidgetGenerator;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
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
public class XsdParserApp
        extends Application
{

    private static final String XSD_BASE_DIR = "src\\main\\resources\\";
    private final DocumentBuilder _documentBuilder;
    private XSDModel _currentModel;
    /**
     * A custom type parser is created for a XSD file itself when it is loaded.
     */
    private IWidgetGenerator localCustomTypeParser = null;

    public XsdParserApp()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(true);

        try
        {
            _documentBuilder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException(e);
        }

    }

    /**
     * @param args
     *         the command line arguments
     */
    public static void main(String[] args)
    {
        // add XSD Filename to arguments
        // TODO check if argument already exists :)
        String[] args2 = Arrays.copyOf(args, args.length + 1);
        args2[args.length] = "--xsdFiles=" + XSD_BASE_DIR + "config\\components";
        launch(args2);
    }

    @Override
    public void start(Stage primaryStage)
    {
        // get the XSD filename argument
        String xsdFilesname = this.getParameters().getNamed().get("xsdFiles");
        // JavaFX SceneGraph root element.
        VBox root = new VBox();
        ComboBox<File> fc = new ComboBox<>();
        root.getChildren().add(fc);
        File dir = new File(xsdFilesname);
        fc.getItems().addAll(dir.listFiles(f -> f.isFile() && f.toString().endsWith(".xsd")));
        fc.valueProperty().addListener((observable, oldValue, newValue) -> {
            DefaultWidgetFactory defaultWidgetFactory;
            try
            {
                // Create the main widget generator controller with the shared namespace.
                defaultWidgetFactory = new DefaultWidgetFactory(
                        new LoadValueGenerator(new File("out.xml")));
            }
            catch (IllegalArgumentException ex)
            {
                defaultWidgetFactory = new DefaultWidgetFactory(new DefaultValueGenerator());
            }


            try
            {
                ObservableList<Node> rootChildren = root.getChildren();
                if (rootChildren.size() > 1)
                    rootChildren.remove(1);
                VBox currentContent = new VBox();
                rootChildren.add(currentContent);
                Document doc = _documentBuilder.parse(newValue.getPath());
                // Add the Generators
                // TODO create missing parsers (e.g. for sequence tags)
                defaultWidgetFactory.addWidgetGenerator(new BasicAttributeParser());
                defaultWidgetFactory
                        .addWidgetGenerator(new SimpleTypeEnumerationRestrictionParser());
                defaultWidgetFactory.addWidgetGenerator(new ContainerParser());
                defaultWidgetFactory.addWidgetGenerator(new BasicSequenceParser());
                defaultWidgetFactory.addWidgetGenerator(new CustomTypesParser("ct:", XSD_BASE_DIR +
                                                                                     "config\\predefined\\CommonTypes.xsd"));
                defaultWidgetFactory.addWidgetGenerator(new CustomTypesParser("st:", XSD_BASE_DIR +
                                                                                     "config\\predefined\\StructuredTypes.xsd"));
                defaultWidgetFactory
                        .addWidgetGenerator(new CustomTypesParser("", newValue.getPath()));
                defaultWidgetFactory.addWidgetGenerator(new SimpleTypeIntegerRestrictionParser());
                // Generated widgets are added to the root node
                _currentModel = defaultWidgetFactory.parseXsd(doc, currentContent,
                                                              newValue.getPath().replaceAll(
                                                                      "\\" + File.separator, "/"));
            }
            catch (Exception ex)
            {
                Logger.getLogger(XsdParserApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // Read the XSD and start parsing
        try
        {
            Document newDoc = _documentBuilder.newDocument();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (! _currentModel.checkViolationDeep())
                    _currentModel.parseToXML(newDoc, null);
                try (FileOutputStream out = new FileOutputStream("out.xml"))
                {
                    TransformerFactory tFactory = TransformerFactory.newInstance();
                    Transformer transformer = tFactory.newTransformer();

                    DOMSource source = new DOMSource(newDoc);
                    StreamResult result = new StreamResult(out);
                    transformer.transform(source, result);
                }
                catch (IOException | TransformerException e)
                {
                    Logger.getLogger(this.getClass().getName())
                          .log(Level.SEVERE, "fatal error while writing output", e);
                }
            }));


        }
        catch (Exception ex)
        {
            Logger.getLogger(XsdParserApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        Scene scene = new Scene(new ScrollPane(root), 1200, 750);
        scene.getStylesheets().add("style.css");

        primaryStage.setTitle("XSD-to-GUI Prototype");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
