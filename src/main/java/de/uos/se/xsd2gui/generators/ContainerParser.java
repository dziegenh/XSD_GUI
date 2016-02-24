package de.uos.se.xsd2gui.generators;

import de.uos.se.xsd2gui.models.ElementModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.xsdparser.AbstractWidgetFactory;
import de.uos.se.xsd2gui.xsdparser.IWidgetGenerator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
    public javafx.scene.Node createWidget(AbstractWidgetFactory controller, Pane parentWidget, Node
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
        // Create the content pane for the child nodes
        Pane contentNodesPane = new VBox(10);
        XSDModel model = new ElementModel(elementNode);
        parentModel.addSubModel(model);
        // create and add child GUI components to the container
        NodeList childNodes = elementNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            controller.parseXsdNode(contentNodesPane, childNodes.item(i), model);
        }

        // Use the value of the "name" attribute as the container title.
        return new TitledPane(name, contentNodesPane);
    }

}
