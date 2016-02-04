package de.uos.se.xsd2gui.generators;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.uos.se.xsd2gui.xsdparser.WidgetFactory;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.uos.se.xsd2gui.xsdparser.WidgetGenerator;

/**
 * Creates the GUI component for elements which have a common type (e.g.
 * "<element type='ct:EndianType' />").
 *
 * @author dziegenhagen
 */
public class CustomTypesParser implements WidgetGenerator {

    /**
     * The namespace prefix of the matching type (e.g. "ct:").
     */
    private final String typeNamespacePrefix;

    /**
     * The XSD file that contains the type definition.
     */
    private final String xsdFilename;
    
    public CustomTypesParser(String typeNamespacePrefix, String xsdFilename) {
        this.typeNamespacePrefix = typeNamespacePrefix;
        this.xsdFilename = xsdFilename;
    }
    
    @Override
    public javafx.scene.Node createWidget(WidgetFactory controller, Pane parentWidget, Node xsdNode) {
        if (!(xsdNode.getNodeType() == Node.ELEMENT_NODE)) {
            return null;
        }
        
        final Element elementNode = (Element) xsdNode;
        final String localName = elementNode.getLocalName();
        if (!localName.equals("element") && !localName.equals("attribute")) {
            return null;
        }
        
        final String type = elementNode.getAttribute("type");
        if (null != type && !type.startsWith(typeNamespacePrefix)) {
            return null;
        }
        
        final String localType = type.substring(typeNamespacePrefix.length());
        
        try {
            // load and setup the XSD document 
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            factory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(new FileInputStream(xsdFilename));

            // setup the XPath object
            XPathFactory xp = XPathFactory.newInstance();
            XPath newXPath = xp.newXPath();
            newXPath.setNamespaceContext(controller.getDefaultNamespaceContext());

            // Find the node which defines the current element type
            NodeList matchingTypeNodes;
            matchingTypeNodes = (NodeList) newXPath.evaluate("/xs:schema/node()[@name='" + localType + "']", doc, XPathConstants.NODESET);

            // TODO check length of the matching node list (should be 1!!)
            // create the GUI widget for the current element type
            Label textFieldLabel = new Label(elementNode.getAttribute("name"));
            HBox hBox = new HBox(10, textFieldLabel);
            controller.parseXsdNode(hBox, matchingTypeNodes.item(0));
            return hBox;
            
        } catch (Exception ex) {
            Logger.getLogger(CustomTypesParser.class.getName()).log(Level.SEVERE, "{0}", ex);
        }
        
        return null;
    }
    
}