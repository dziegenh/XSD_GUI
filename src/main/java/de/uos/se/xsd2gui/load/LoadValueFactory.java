package de.uos.se.xsd2gui.load;

import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.util.XPathUtil;
import de.uos.se.xsd2gui.util.XSDPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * created: 24.02.2016
 * A {@linkplain IValueFactory} to load values from a file. It heavily depends on {@linkplain XSDPathUtil} for matching XSD and XML.
 *
 * @author Falk Wilke
 */
public class LoadValueFactory
        extends DefaultValueFactory
{
    //the values to give
    private final Map<String, List<String>> _values;
    //the amount of elements (or their paths precisely)
    private final Map<String, Integer> _amountOfElements;

    /**
     * The constructor
     *
     * @param xmlFile
     *         the file where the data is located
     *
     * @throws IllegalArgumentException
     *         if the given document could not be parsed
     */
    public LoadValueFactory(File xmlFile) throws IllegalArgumentException
    {
        this._values = new HashMap<>();
        this._amountOfElements = new HashMap<>();
        //same old xml parsing stuff.....
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(true);

        try
        {
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document parse = documentBuilder.parse(xmlFile);
            //init attribute values
            initAtt(parse.getDocumentElement());
            //init element count
            initElemCount(parse.getDocumentElement());

        } catch (ParserConfigurationException | SAXException | IOException e)
        {
            throw new IllegalArgumentException(e);
        }

    }

    /**
     * This method initializes the values which can possibly be retrieved from this {@linkplain IValueFactory}.
     * Is only using attribute values (could possibly be changed but since attributes can always replace inner text this is omitted)
     * The values are mapped to the result of{@linkplain XSDPathUtil#parseFromXMLNode(Node)} for every attribute
     *
     * @param root
     *         the element where parsing shall start
     */
    private void initAtt(Element root)
    {
        //get all elements which are not text nodes (descendant axis, node() test)
        NodeList nodeList = XPathUtil.evaluateXPath(root, "current()/descendant::node()[not(self::text())]");
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Element currentElement = (Element) nodeList.item(i);
            //get all attributes for the current element
            NodeList nodeListAttributes = XPathUtil.evaluateXPath(currentElement, "current()/attribute::node()");
            for (int j = 0; j < nodeListAttributes.getLength(); j++)
            {
                Node currentAttribute = nodeListAttributes.item(j);
                //turn attribute into path and store value
                String path = XSDPathUtil.parseFromXMLNode(currentAttribute);
                if (! this._values.containsKey(path))
                    this._values.put(path, new LinkedList<>());
                //the order of appearance is important!
                this._values.get(path).add(currentAttribute.getNodeValue());
            }
        }
    }

    /**
     * This method counts every element (or to be precise the path representation) by using {@linkplain XSDPathUtil#parseFromXMLNode(Node)}
     *
     * @param root
     *         the element where to start
     */
    private void initElemCount(Element root)
    {
        //get all elements which are not text nodes (descendant axis, node() test)
        NodeList nodeList = XPathUtil.evaluateXPath(root, "current()/descendant::node()[not(self::text())]");
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node currentNode = nodeList.item(i);
            String path = XSDPathUtil.parseFromXMLNode(currentNode);
            //simply count
            this._amountOfElements.put(path, this._amountOfElements.getOrDefault(path, 0) + 1);
        }
    }

    @Override
    public String getValueFor(XSDModel model, String defaultValue)
    {
        //get path and check if it is known: if not or no values can be retrieved anymore return default
        String path = XSDPathUtil.parseFromXSDModel(model);
        if (! this._values.containsKey(path))
            return super.getValueFor(model, defaultValue);
        List<String> valuesForElement = this._values.get(path);
        if (valuesForElement.isEmpty())
            return super.getValueFor(model, defaultValue);
        return valuesForElement.remove(0);
    }

    @Override
    public int getMinimumNumberOfElements(XSDModel model, Element element)
    {
        String path = XSDPathUtil.parseFromXSDModel(model, element);
        return this._amountOfElements.getOrDefault(path, 0);
    }
}
