package de.uos.se.xsd2gui.generators;

import de.uos.se.xsd2gui.models.AttributeModel;
import de.uos.se.xsd2gui.models.ElementModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.models.constraints.FixedValueConstraint;
import de.uos.se.xsd2gui.xsdparser.AbstractWidgetFactory;
import de.uos.se.xsd2gui.xsdparser.IWidgetGenerator;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates the GUI component for elements which have a common type (e.g.
 * "<element type='ct:EndianType' />").
 *
 * @author dziegenhagen
 */
public class CustomTypesParser
        implements IWidgetGenerator
{

    public static final String FIXED = "fixed";
    /**
     * The namespace prefix of the matching type (e.g. "ct:").
     */
    private final String typeNamespacePrefix;

    /**
     * The XSD file that contains the type definition.
     */
    private final String xsdFilename;

    public CustomTypesParser(String typeNamespacePrefix, String xsdFilename)
    {
        this.typeNamespacePrefix = typeNamespacePrefix;
        this.xsdFilename = xsdFilename;
    }

    @Override
    public javafx.scene.Node createWidget(AbstractWidgetFactory factory, Pane parentWidget, Node
            xsdNode, XSDModel parentModel)
    {
        if (! (xsdNode.getNodeType() == Node.ELEMENT_NODE))
        {
            return null;
        }

        final Element elementNode = (Element) xsdNode;
        final String localName = elementNode.getLocalName();
        if (! localName.equals("element") && ! localName.equals("attribute"))
        {
            return null;
        }

        final String type = elementNode.getAttribute("type");
        if (null == type || ! type.startsWith(typeNamespacePrefix))
        {
            return null;
        }
        XSDModel model;
        String fixed = elementNode.getAttribute(FIXED);
        if (localName.equals("element"))
            model = new ElementModel(elementNode);
        else
            model = new AttributeModel(elementNode);
        if (model.isFixed())
            model.addConstraint(new FixedValueConstraint(fixed));
        final String localType = type.substring(typeNamespacePrefix.length());

        try
        {
            // load and setup the XSD document
            DocumentBuilderFactory documentBuilderFctory = DocumentBuilderFactory.newInstance();
            documentBuilderFctory.setIgnoringComments(true);
            documentBuilderFctory.setIgnoringElementContentWhitespace(true);
            documentBuilderFctory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFctory.newDocumentBuilder();
            Document doc = documentBuilder.parse(new FileInputStream(xsdFilename));

            // setup the XPath object
            XPathFactory xp = XPathFactory.newInstance();
            XPath newXPath = xp.newXPath();
            newXPath.setNamespaceContext(factory.getNamespaceContext());

            // Find the node which defines the current element type
            NodeList matchingTypeNodes;
            matchingTypeNodes = (NodeList) newXPath
                    .evaluate("/xs:schema/node()[@name='" + localType + "']", doc,
                              XPathConstants.NODESET);
            if (1 == matchingTypeNodes.getLength())
            {

                Label textFieldLabel = new Label(elementNode.getAttribute("name"));
                HBox hBox = new HBox(10, textFieldLabel);
                parentModel.addSubModel(model);
                factory.parseXsdNode(hBox, matchingTypeNodes.item(0), model);
                model.valueProperty()
                     .setValue(factory.getValueFor(model, model.valueProperty().getValue()));
                return hBox;

            } else
            {
                /*Logger.getLogger(CustomTypesParser.class.getName())
                      .log(Level.INFO, "The XSD Node for the " +
                                          "custom type {0} could " +
                                          "not be found!", localType);*/
                return null;

            }

        }
        catch (Exception ex)
        {
            Logger.getLogger(CustomTypesParser.class.getName()).log(Level.SEVERE, "{0}", ex);
        }

        return null;
    }

}
