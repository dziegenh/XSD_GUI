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
import java.util.*;
import java.util.stream.Collectors;

/**
 * created: 24.02.2016
 * A
 * {@linkplain IValueGenerator} to load values from a file. It heavily depends on {@linkplain XSDPathUtil} for matching XSD and XML.
 *
 * @author Falk Wilke
 */
public class LoadValueGenerator
        extends DefaultValueGenerator
{
    public static final String EMPTY = "";
    //the values to give
    private final Map<String, List<String>> _values;
    //the amount of elements (or their paths precisely)
    private final Map<String, Integer> _amountOfElements;
    //the possible attributes of every element
    private Map<String, Set<String>> _possibleAttributes;

    /**
     * The constructor
     *
     * @param xmlFile
     *         the file where the data is located
     *
     * @throws IllegalArgumentException
     *         if the given document could not be parsed
     */
    public LoadValueGenerator(File xmlFile) throws IllegalArgumentException
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
            Document parsedXML = documentBuilder.parse(xmlFile);

            initPossibleAttributes(parsedXML.getDocumentElement());
            //init attribute values
            initAtt(parsedXML.getDocumentElement());
            //init element count
            initElemCount(parsedXML.getDocumentElement());


        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * This Method initializes the possible attributes which can be found for every element. It
     * is mostly the same as {@linkplain #initAtt(Element)}, but the latter needs the information
     * generated here beforehand, since they cannot be generated locally.
     *
     * @param root
     *         he element where parsing shall start
     */
    private void initPossibleAttributes(Element root)
    {
        NodeList nodeList = XPathUtil
                .evaluateXPath(root, "current()/descendant::node()[not(self::text())]");
        Map<String, Set<String>> possibleAttributes = new HashMap<>();
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Element currentElement = (Element) nodeList.item(i);
            //get all attributes for the current element
            NodeList nodeListAttributes = XPathUtil
                    .evaluateXPath(currentElement, "current()/attribute::node()");
            String elementPath = XSDPathUtil.parseFromXMLNode(currentElement);
            possibleAttributes.putIfAbsent(elementPath, new HashSet<>());
            Set<String> attributes = possibleAttributes.get(elementPath);
            for (int j = 0; j < nodeListAttributes.getLength(); j++)
            {
                Node currentAttribute = nodeListAttributes.item(j);
                //turn attribute into path and store value
                String path = XSDPathUtil.parseFromXMLNode(currentAttribute);
                attributes.add(path);
            }
        }
        this._possibleAttributes = possibleAttributes;
    }

    /**
     * This method initializes the values which can possibly be retrieved from this
     * {@linkplain IValueGenerator}.
     * Is only using attribute values (could possibly be changed but since attributes can always
     * replace inner text this is omitted)
     * The values are mapped to the result of{@linkplain XSDPathUtil#parseFromXMLNode(Node)} for
     * every attribute
     *
     * @param root
     *         the element where parsing shall start
     */
    private void initAtt(Element root)
    {
        //get all elements which are not text nodes (descendant axis, node() test)
        NodeList nodeList = XPathUtil
                .evaluateXPath(root, "current()/descendant::node()[not(self::text())]");
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Element currentElement = (Element) nodeList.item(i);
            //get all attributes for the current element
            NodeList nodeListAttributes = XPathUtil
                    .evaluateXPath(currentElement, "current()/attribute::node()");
            String elementPath = XSDPathUtil.parseFromXMLNode(currentElement);
            Set<String> attributes = new HashSet<>();
            for (int j = 0; j < nodeListAttributes.getLength(); j++)
            {
                Node currentAttribute = nodeListAttributes.item(j);
                //turn attribute into path and store value
                String path = XSDPathUtil.parseFromXMLNode(currentAttribute);
                if (! this._values.containsKey(path))
                    this._values.put(path, new LinkedList<>());
                //the order of appearance is important!
                this._values.get(path).add(currentAttribute.getNodeValue());
                attributes.add(path);
            }
            //repair values if necessary
            List<String> notPresentAtts = this._possibleAttributes.get(elementPath).stream()
                                                                  .filter(s -> ! attributes
                                                                          .contains(s))
                                                                  .collect(Collectors.toList());
            for (String notPresentAtt : notPresentAtts)
            {
                this._values.putIfAbsent(notPresentAtt, new LinkedList<>());
                this._values.get(notPresentAtt).add(null);
            }
        }
    }

    /**
     * This method counts every element (or to be precise the path representation) by using
     * {@linkplain XSDPathUtil#parseFromXMLNode(Node)}
     *
     * @param root
     *         the element where to start
     */
    private void initElemCount(Element root)
    {
        //get all elements which are not text nodes (descendant axis, node() test)
        NodeList nodeList = XPathUtil
                .evaluateXPath(root, "current()/descendant::node()[not(self::text())]");
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
        //get path and check if it is known: if not or no values can be retrieved anymore return
        // default
        String path = XSDPathUtil.parseFromXSDModel(model);
        if (! this._values.containsKey(path))
            return super.getValueFor(model, defaultValue);
        List<String> valuesForElement = this._values.get(path);
        if (valuesForElement.isEmpty())
            return super.getValueFor(model, defaultValue);

        String result = valuesForElement.remove(0);
        if (result == null)
            return super.getValueFor(model, defaultValue);
        return result;
    }

    @Override
    public int getMinimumNumberOfElements(XSDModel model, Element element)
    {
        String path = XSDPathUtil.parseFromXSDModel(model, element);
        return this._amountOfElements.getOrDefault(path, 0);
    }
}
