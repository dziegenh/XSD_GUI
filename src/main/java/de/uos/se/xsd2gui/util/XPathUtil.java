package de.uos.se.xsd2gui.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by sem on 04.02.2016.
 * A utility class providing some helpers for evaluating xpath expressions
 */
public class XPathUtil
{
    /**
     * Essentially the same as {@linkplain #evaluateXPath(NamespaceContext, Node, String)}, but is using {@linkplain DefaultNamespaceContext}
     *
     * @param rootNode
     *         the node from where the expression shall be evaluated
     * @param expression
     *         the xpath expression
     *
     * @return a nodelist containing all matches or null if the expression was malformed
     */
    public static NodeList evaluateXPath(org.w3c.dom.Node rootNode, String expression)
    {
        return evaluateXPath(new DefaultNamespaceContext(), rootNode, expression);
    }

    /**
     * This method evaluates the given xpath expression on the given {@linkplain org.w3c.dom.Node}using the provided {@linkplain NamespaceContext}.
     * Can return null if the expression is malformed
     *
     * @param namespaceContext
     *         the context to use
     * @param rootNode
     *         the node from where the expression shall be evaluated
     * @param expression
     *         the xpath expression
     *
     * @return a nodelist containing all matches or null if the expression was malformed
     */
    public static NodeList evaluateXPath(NamespaceContext namespaceContext, org.w3c.dom.Node rootNode, String expression)
    {
        // setup the XPath object
        XPathFactory xp = XPathFactory.newInstance();
        XPath newXPath = xp.newXPath();
        newXPath.setNamespaceContext(namespaceContext);

        // Find the node which defines the current element type
        try
        {
            return (NodeList) newXPath.evaluate(expression, rootNode, XPathConstants.NODESET);
        } catch (XPathExpressionException e)
        {
            return null;
        }
    }
}
