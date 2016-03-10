package de.uos.se.xsd2gui.model_generators;

import de.uos.se.xsd2gui.app.XsdParserApp;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.models.constraints.IntegerRangeConstraint;
import de.uos.se.xsd2gui.node_generators.INodeGenerator;
import de.uos.se.xsd2gui.util.XSDConstants;
import de.uos.se.xsd2gui.xsdparser.AbstractWidgetFactory;
import de.uos.se.xsd2gui.xsdparser.IWidgetGenerator;
import javafx.scene.layout.Pane;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates GUI components for simpleType tags. It only applies to those <xs:simpleType/> who are
 * restricted by <xs:restriction base='xs:integer'/> using up to 2 of (which do make sense of
 * course)
 * <xs:minInclusive/>,<xs:maxInclusive/>,<xs:minExclusive/>,<xs:maxExclusive/>
 *
 * @author dziegenhagen
 */
public class SimpleTypeIntegerRestrictionParser
        implements IWidgetGenerator
{

    @Override
    public Optional<javafx.scene.Node> createWidget(AbstractWidgetFactory factory, Pane
            parentWidget, Node xsdNode, XSDModel parentModel)
    {
        //abort if wrong type
        if (xsdNode.getNodeType() != Node.ELEMENT_NODE ||
            ! xsdNode.getLocalName().equals(XSDConstants.SIMPLE_TYPE))
        {
            return Optional.empty();
        }
        XPathFactory xp = XPathFactory.newInstance();
        XPath newXPath = xp.newXPath();
        newXPath.setNamespaceContext(factory.getNamespaceContext());
        NodeList values;
        try
        {
            //check for integer restrictions only, there should be more than 1 and less than 3
            values = (NodeList) newXPath
                    .evaluate("xs:restriction[@base='xs:integer']/child::node()[not(self::text())]",
                              xsdNode, XPathConstants.NODESET);

            //abort if wrong amount
            if (values.getLength() < 1 || values.getLength() > 2)
            {
                return Optional.empty();
            }
            //min max init to max/min integer range
            int max = Integer.MAX_VALUE;
            int min = Integer.MIN_VALUE;
            boolean minInclusive = false;
            boolean maxInclusive = false;
            //iterate and check for attributes
            for (int i = 0; i < values.getLength(); i++)
            {
                Element item = (Element) values.item(i);
                switch (item.getNodeName())
                {
                    case "xs:minInclusive":
                        minInclusive = true;
                        min = Integer.parseInt(item.getAttribute(XSDConstants.VALUE));
                        break;
                    case "xs:maxInclusive":
                        maxInclusive = true;
                        max = Integer.parseInt(item.getAttribute(XSDConstants.VALUE));
                        break;
                    case "xs:minExclusive":

                        minInclusive = false;
                        min = Integer.parseInt(item.getAttribute(XSDConstants.VALUE));
                        break;
                    case "xs:maxExclusive":
                        maxInclusive = false;
                        max = Integer.parseInt(item.getAttribute(XSDConstants.VALUE));
                        break;
                }
            }
            //check min/max
            if (min > max)
            {
                int tmp = max;
                //have at least a gap of 3 between those values to avoid checking inclusive flags
                max = min + 2;
                min = tmp;
                Logger.getLogger(this.getClass().getName())
                      .warning("min max values are in wrong range min=" + max + " max=" + min);
            }
            //add range constraint
            IntegerRangeConstraint constr = new IntegerRangeConstraint(min, max, minInclusive,
                                                                       maxInclusive);
            parentModel.addConstraint(constr);
            INodeGenerator bef = factory.getNodeGenerator();
            return Optional.of(bef.getAndBindControl(factory.getValueGenerator(), parentModel,
                                                     XSDConstants.XS_INT));


        }
        catch (XPathExpressionException ex)
        {
            Logger.getLogger(XsdParserApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Optional.empty();
    }


}
