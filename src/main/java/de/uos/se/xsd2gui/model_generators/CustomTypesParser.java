package de.uos.se.xsd2gui.model_generators;

import de.uos.se.xsd2gui.models.AttributeModel;
import de.uos.se.xsd2gui.models.ElementModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.models.constraints.FixedValueConstraint;
import de.uos.se.xsd2gui.util.XPathUtil;
import de.uos.se.xsd2gui.xsdparser.AbstractWidgetFactory;
import de.uos.se.xsd2gui.xsdparser.IWidgetGenerator;
import javafx.scene.layout.Pane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
    public javafx.scene.Node createWidget(AbstractWidgetFactory factory, Pane parentWidget, Node xsdNode, XSDModel parentModel)
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
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setNamespaceAware(true);
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document doc = documentBuilder.parse(new FileInputStream(xsdFilename));

            // Find the node which defines the current element type
            NodeList matchingTypeNodes;
            matchingTypeNodes = XPathUtil.evaluateXPath(doc, "/xs:schema/node()[@name='" + localType + "']");
            if (1 == matchingTypeNodes.getLength())
            {

                parentModel.addSubModel(model);
                Pane container = factory.getNodeGenerator().getSimpleContainerFor(model);
                factory.parseXsdNode(container, matchingTypeNodes.item(0), model);

                return container;

            } else
            {
                /*Logger.getLogger(CustomTypesParser.class.getName())
                      .log(Level.INFO, "The XSD Node for the " +
                                          "custom type {0} could " +
                                          "not be found!", localType);*/
                return null;

            }

        } catch (Exception ex)
        {
            Logger.getLogger(CustomTypesParser.class.getName()).log(Level.SEVERE, "{0}", ex);
        }

        return null;
    }

}
