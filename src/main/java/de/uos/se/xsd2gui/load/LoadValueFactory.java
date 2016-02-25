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
 *
 * @author Falk Wilke
 */
public class LoadValueFactory
        implements IValueFactory
{
    private final Map<String, List<String>> _values;
    private final Map<String, Integer> _amountOfElements;

    public LoadValueFactory(File xmlFile)
    {
        this._values = new HashMap<>();
        this._amountOfElements = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(true);

        try
        {
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document parse = documentBuilder.parse(xmlFile);
            initAtt(parse.getDocumentElement());
            initElemCount(parse.getDocumentElement());

        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    private void initAtt(Element root)
    {
        NodeList nodeList = XPathUtil
                .evaluateXPath(root, "current()/descendant::node()[not(self::text())]");
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Element currentElement = (Element) nodeList.item(i);
            NodeList nodeListAttributes = XPathUtil
                    .evaluateXPath(currentElement, "current()/attribute::node()");
            for (int j = 0; j < nodeListAttributes.getLength(); j++)
            {
                Node currentAttribute = nodeListAttributes.item(j);
                String path = XSDPathUtil.parseFromXMLNode(currentAttribute);
                if (! this._values.containsKey(path))
                    this._values.put(path, new LinkedList<>());
                this._values.get(path).add(currentAttribute.getNodeValue());
            }
        }
    }

    private void initElemCount(Element root)
    {
        NodeList nodeList = XPathUtil
                .evaluateXPath(root, "current()/descendant::node()[not(self::text())]");
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node currentNode = nodeList.item(i);
            String path = XSDPathUtil.parseFromXMLNode(currentNode);
            this._amountOfElements.put(path, this._amountOfElements.getOrDefault(path, 0) + 1);
        }
    }

    @Override
    public String getValueFor(XSDModel model, String defaultValue)
    {
        String path = XSDPathUtil.parseFromXSDModel(model);
        if (! this._values.containsKey(path))
            return defaultValue;
        List<String> valuesForElement = this._values.get(path);
        if (valuesForElement.isEmpty())
            return defaultValue;
        return valuesForElement.remove(0);
    }

    @Override
    public int getMinimumNumberOfElements(XSDModel model, Element element)
    {
        String path = XSDPathUtil.parseFromXSDModel(model, element);
        return this._amountOfElements.getOrDefault(path, 0);
    }
}
