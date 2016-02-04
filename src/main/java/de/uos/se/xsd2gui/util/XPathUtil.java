package de.uos.se.xsd2gui.util;

import de.uos.se.xsd2gui.xsdparser.WidgetGeneratorController;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by sem on 04.02.2016.
 */
public class XPathUtil {

   public static NodeList evaluateXPath(NamespaceContext namespaceContext, org.w3c.dom.Node rootNode) throws XPathExpressionException {
      // setup the XPath object
      XPathFactory xp = XPathFactory.newInstance();
      XPath newXPath = xp.newXPath();
      newXPath.setNamespaceContext(namespaceContext);

      // Find the node which defines the current element type
      return (NodeList) newXPath.evaluate("./xs:element[@minOccurs and @maxOccurs]", rootNode, XPathConstants.NODESET);
   }
}
