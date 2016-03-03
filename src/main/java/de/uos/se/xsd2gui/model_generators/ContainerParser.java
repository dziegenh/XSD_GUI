package de.uos.se.xsd2gui.model_generators;

import de.uos.se.xsd2gui.models.ElementModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.node_generators.INodeGenerator;
import de.uos.se.xsd2gui.xsdparser.AbstractWidgetFactory;
import de.uos.se.xsd2gui.xsdparser.IWidgetGenerator;
import javafx.scene.layout.Pane;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates titled GUI component for named container tags without any type (e.g.
 * "<element name='ContainerTitle'>...</complexType>").
 *
 * @author dziegenhagen
 */
public class ContainerParser
        implements IWidgetGenerator
{

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

        if (! localName.equals("element"))
        {
            return null;
        }
        String name = elementNode.getAttribute("name");
        String type = elementNode.getAttribute("type");
        if (name.isEmpty() || ! type.isEmpty())
        {
            return null;
        }

        //create the model
        XSDModel model = new ElementModel(elementNode);
        // Create the content pane for the child nodes
        INodeGenerator baseElementFactory = factory.getNodeGenerator();
        parentModel.addSubModel(model);
        Pane contentNodesPane = baseElementFactory.getSimpleContainerFor(model);
        // create and add child GUI components to the container
        NodeList childNodes = elementNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            factory.parseXsdNode(contentNodesPane, childNodes.item(i), model);
        }

        // Use the value of the "name" attribute as the container title.
        return baseElementFactory.getTitledContainerFor(model, contentNodesPane);
    }

}
