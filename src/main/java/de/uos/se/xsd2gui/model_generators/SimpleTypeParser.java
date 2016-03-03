package de.uos.se.xsd2gui.model_generators;

import de.uos.se.xsd2gui.app.XsdParserApp;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.node_generators.INodeGenerator;
import de.uos.se.xsd2gui.xsdparser.AbstractWidgetFactory;
import de.uos.se.xsd2gui.xsdparser.IWidgetGenerator;
import javafx.scene.layout.Pane;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates GUI components for simpleType tags.
 *
 * @author dziegenhagen
 */
public class SimpleTypeParser
        implements IWidgetGenerator
{

    //a constant for the fixed attribute
    public static final String FIXED = "fixed";
    //a constant for the name of the elements this attribute should apply to
    public static final String SIMPLE_TYPE = "simpleType";

    @Override
    public Optional<javafx.scene.Node> createWidget(AbstractWidgetFactory factory, Pane
            parentWidget, Node xsdNode, XSDModel parentModel)
    {

        if (xsdNode.getNodeType() != Node.ELEMENT_NODE || ! xsdNode.getLocalName().equals(SIMPLE_TYPE))
        {
            return Optional.empty();
        }
        XPathFactory xp = XPathFactory.newInstance();
        XPath newXPath = xp.newXPath();
        newXPath.setNamespaceContext(factory.getNamespaceContext());
        NodeList enumValues;
        try
        {
            enumValues = (NodeList) newXPath
                    .evaluate("xs:restriction[@base='xs:string']/xs:enumeration/@value", xsdNode,
                              XPathConstants.NODESET);
            if (enumValues.getLength() < 1)
            {
                return Optional.empty();
            }
            //nodelists are highly unflexivle so "casting" is required
            List<String> enumValuesAsStrings = new LinkedList<>();
            for (int i = 0; i < enumValues.getLength(); i++)
            {
                enumValuesAsStrings.add(enumValues.item(i).getNodeValue());
            }
            INodeGenerator bef = factory.getNodeGenerator();
            return Optional
                    .of(bef.getAndBindRestrictedControl(factory.getValueGenerator(), parentModel,
                                                        enumValuesAsStrings));

        } catch (XPathExpressionException ex)
        {
            Logger.getLogger(XsdParserApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Optional.empty();
    }


}
