package de.uos.se.xsd2gui.util;

import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.value_generators.IValueGenerator;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * created: 24.02.2016
 * A class translating between xsd and xml by generating "paths" for them individually
 *
 * @author Falk Wilke
 */
public class XSDPathUtil
{
    //the default separator between elements
    public static final String PATH_SEPARATOR = " ";
    //the replacement for PATH_SEPARATORs
    public static final String REPLACEMENT = ",";

    /**
     * Private constructor since this is utility class
     */
    private XSDPathUtil()
    {

    }

    /**
     * This method parses a path from the given {@linkplain XSDModel} and prepends the given {@linkplain Element}s name attribute. Essentially the same as calling
     * {@linkplain #parseFromXSDModel(XSDModel)} and prepending it with the name attribute of the given element, {@linkplain #getCorrespondingType(Node)} and {@linkplain #PATH_SEPARATOR}.
     * All joined in that exact order. This Method is used by
     * {@linkplain IValueGenerator#getMinimumNumberOfElements(XSDModel, Element)} essentially since there the {@linkplain XSDModel} alone is not sufficient for generating a valid path.
     * This happend because a {@linkplain SequenceReparser} needs to know that information before adding models.
     *
     * @param model
     *         The {@linkplain XSDModel} to generate a path for
     * @param element
     *         the Element to use as a prefix
     *
     * @return a string representing the given xsd-models value field in terms of a (later or beforehand) generated XML-file
     */
    public static String parseFromXSDModel(XSDModel model, Element element)
    {
        String prefixElementName = element.getAttribute(XSDConstants.NAME) +
                                   getCorrespondingType(element);
        return prefixElementName.trim().concat(PATH_SEPARATOR).concat(parseFromXSDModel(model));
    }

    /**
     * Gets the type of the given {@linkplain Element}. Normally this could be done by simply calling {@linkplain Element#getNodeType()}, but in this case an exception to that rule is required.
     * An <xs:attribute/> does correspond to an attribute node within an xml document while being an {@linkplain Node#ELEMENT_NODE}.
     * This has to be corrected if matching results are desired.
     * This method is designed for avoiding name clashes between attributes and elements (we do not want attributes and elements with the same name to cancel each other out)
     *
     * @param element
     *         the {@linkplain Element} to evaluate for
     *
     * @return the result of {@linkplain Element#getNodeType()} or {@linkplain Node#ATTRIBUTE_NODE} if  {@linkplain Node#getNodeName()} equals 'xs:attribute'
     */
    private static short getCorrespondingType(Node element)
    {
        if (element.getNodeName().equals("xs:attribute"))
            return Node.ATTRIBUTE_NODE;
        else
            return element.getNodeType();
    }

    /**
     * This method generates a path by moving to top from the given {@linkplain XSDModel} and joining all name attributes with {@linkplain #PATH_SEPARATOR} in between.
     *
     * @param model
     *         the model to evaluate
     *
     * @return a path representing the value field within an XML-Document that corresponds to the given {@linkplain XSDModel}
     */
    public static String parseFromXSDModel(XSDModel model)
    {
        //null is empty
        if (model == null)
            return "";
        XSDModel current = model;
        StringBuilder sb = new StringBuilder();
        //move top, only root model does not have a parent (null)
        while (current != null)
        {
            //get the current xsdnode
            Element currentElement = current.getXSDNode();
            //only append if name is present
            if (currentElement.hasAttribute(XSDConstants.NAME))
                //append by using path separator and replacing illegal signs
                sb.append(currentElement.getAttribute(XSDConstants.NAME)
                                        .replaceAll(PATH_SEPARATOR, REPLACEMENT))
                  .append(getCorrespondingType(currentElement)).append(PATH_SEPARATOR);
            //move upwards
            current = current.getParentModel();
        }
        //remove last separator
        return sb.substring(0, sb.lastIndexOf(PATH_SEPARATOR));
    }

    /**
     * This method parses the given xml node into a path representation matching its position within a corresponding {@linkplain XSDModel}.
     * This works for all kind of elements (since all of them can have a value), but it is strongly advised to use attributes for storing data alone.
     *
     * @param xmlNode
     *         the xml node to evaluate
     *
     * @return a path representation for this nodes "value" if it is to be found within an {@linkplain XSDModel}
     */
    public static String parseFromXMLNode(Node xmlNode)
    {
        //null is empty
        if (xmlNode == null)
            return "";
        Node current = xmlNode;
        StringBuilder sb = new StringBuilder();
        //loop until the local name or the current element is null.
        while (current != null && current.getLocalName() != null)
        {
            //append by using path separator and replacing illegal signs
            sb.append(current.getLocalName().replaceAll(PATH_SEPARATOR, REPLACEMENT))
              .append(getCorrespondingType(current)).append(PATH_SEPARATOR);
            current = getContainingNode(current);
        }
        //remove last separator
        return sb.substring(0, sb.lastIndexOf(PATH_SEPARATOR));
    }

    /**
     * Convenience method. Since {@linkplain Node#ATTRIBUTE_NODE}s need a different call to gain their "parent" this method was introduced.
     * E.g. for {@linkplain Attr} a call to {@linkplain Attr#getOwnerElement()} is needed whereas for {@linkplain Element} {@linkplain Node#getParentNode()} is needed.
     *
     * @param current
     *         the {@linkplain Node} to get the containing {@linkplain Node} for
     *
     * @return the {@linkplain Node} containing the given {@linkplain Node}
     */
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
