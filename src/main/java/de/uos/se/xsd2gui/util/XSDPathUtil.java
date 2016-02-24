package de.uos.se.xsd2gui.util;

import de.uos.se.xsd2gui.models.XSDModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * created: 24.02.2016
 *
 * @author Falk Wilke
 */
public class XSDPathUtil
{
    public static final String PATH_SEPARATOR = " ";
    public static final String NAME = "name";
    public static final String REPLACEMENT = ",";

    private XSDPathUtil()
    {

    }

    public static String parseFromXSDModel(XSDModel model, String prefixElementName)
    {
        return prefixElementName.trim().concat(PATH_SEPARATOR).concat(parseFromXSDModel(model));
    }

    public static String parseFromXSDModel(XSDModel model)
    {
        if (model == null)
            return "";
        XSDModel current = model;
        StringBuilder sb = new StringBuilder();
        while (current != null)
        {

            Element currentElement = current.getXSDNode();
            if (currentElement.hasAttribute(NAME))
                sb.append(currentElement.getAttribute(NAME).replaceAll(PATH_SEPARATOR, REPLACEMENT))
                  .append(PATH_SEPARATOR);
            current = current.getParentModel();
        }
        return sb.substring(0, sb.lastIndexOf(PATH_SEPARATOR));
    }

    public static String parseFromXMLNode(Node xmlNode)
    {
        if (xmlNode == null)
            return "";
        Node current = xmlNode;
        StringBuilder sb = new StringBuilder();
        while (current != null && current.getLocalName() != null)
        {
            sb.append(current.getLocalName().replaceAll(PATH_SEPARATOR, REPLACEMENT))
              .append(PATH_SEPARATOR);
            current = getContainingNode(current);
        }
        return sb.substring(0, sb.lastIndexOf(PATH_SEPARATOR));
    }

    private static Node getContainingNode(Node current)
    {
        switch (current.getNodeType())
        {
            case Node.ELEMENT_NODE:
                return current.getParentNode();
            case Node.ATTRIBUTE_NODE:
                return ((Attr) current).getOwnerElement();
            default:
                return current.getOwnerDocument();
        }
    }

}
