package de.uos.se.xsd2gui.generators;

import de.uos.se.xsd2gui.factories.INodeGenerator;
import de.uos.se.xsd2gui.models.AttributeModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.xsdparser.AbstractWidgetFactory;
import de.uos.se.xsd2gui.xsdparser.IWidgetGenerator;
import javafx.scene.layout.Pane;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates GUI components for attribute tags with basic XSMLSchema types (e.g.
 * "<attribute type='xs:string' />".
 *
 * @author dziegenhagen
 */
public class BasicAttributeParser
        implements IWidgetGenerator
{
    @Override
    public javafx.scene.Node createWidget(AbstractWidgetFactory factory, Pane parentWidget, Node
            xsdNode, XSDModel parentModel)
    {

        //check for correct types and attributes
        if (! (xsdNode.getNodeType() == Node.ELEMENT_NODE))
        {
            return null;
        }

        final Element elementNode = (Element) xsdNode;
        String localName = elementNode.getLocalName();
        if (! localName.equals("attribute"))
        {
            return null;
        }

        final String type = elementNode.getAttribute("type");

        if (null == type)
        {
            return null;
        }
        XSDModel model = new AttributeModel(elementNode);
        //important that the submodel is added before asking the factoryx for values since the
        // location of the model is used for generating "paths"
        parentModel.addSubModel(model);
        INodeGenerator baseElementFactory = factory.getNodeGenerator();
        javafx.scene.Node binded = baseElementFactory
                .getAndBindControl(factory.getValueGenerator(), model);

        if (binded == null)
        {
            Logger.getLogger(this.getClass().getName())
                  .log(Level.WARNING, "no input widget created for {0}", model);
            parentModel.removeSubmodel(model);
            return null;
        }

        return binded;

    }
}
