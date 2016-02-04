package de.uos.se.xsd2gui.generators;

import de.uos.se.xsd2gui.xsdparser.WidgetGenerator;
import de.uos.se.xsd2gui.xsdparser.WidgetGeneratorController;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.logging.Logger;

/**
 * Created by sem on 04.02.2016.
 */
public class BasicSequenceParser implements WidgetGenerator {

   public static final String ELEMENT_NAME = "sequence";
   public static final Logger LOGGER = Logger.getLogger(BasicSequenceParser.class.getName());

   @Override
   public Node createWidget(WidgetGeneratorController controller, Pane parentWidget, org.w3c.dom.Node xsdNode) {
      if (!(xsdNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)) {
         return null;
      }

      final Element elementNode = (Element) xsdNode;
      if (!elementNode.getLocalName().equals(ELEMENT_NAME)) {
         return null;
      }
      // setup the XPath object
      XPathFactory xp = XPathFactory.newInstance();
      XPath newXPath = xp.newXPath();
      newXPath.setNamespaceContext(controller.getDefaultNamespaceContext());

      // Find the node which defines the current element type
      try {
         NodeList matchingTypeNodes = (NodeList) newXPath.evaluate("./xs:element[@minOccurs and @maxOccurs]", xsdNode, XPathConstants.NODESET);
         Pane contentNodesPane = new VBox();
         for (int i = 0; i < matchingTypeNodes.getLength(); i++) {
            Element item = (Element) matchingTypeNodes.item(i);
            int minOccurs = Integer.parseInt(item.getAttribute("minOccurs"));
            String maxOccursString = item.getAttribute("maxOccurs");
            int maxOccurs = maxOccursString.matches("\\d+") ? Integer.parseInt(maxOccursString) : Integer.MAX_VALUE;
            if (minOccurs > maxOccurs) {
               LOGGER.warning("minOccurs > maxOccurs for element " + item);
               continue;
            }
            for (int j = 0; j < minOccurs; j++) {
               controller.parseXsdNode(contentNodesPane, item);
            }
         }
         return contentNodesPane;
      } catch (XPathExpressionException e) {
         e.printStackTrace();
      }
      return null;
   }
}
