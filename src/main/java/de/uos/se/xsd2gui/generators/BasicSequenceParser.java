package de.uos.se.xsd2gui.generators;

import de.uos.se.xsd2gui.util.XPathUtil;
import de.uos.se.xsd2gui.xsdparser.WidgetGenerator;
import de.uos.se.xsd2gui.xsdparser.WidgetFactory;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.logging.Logger;

/**
 * Created by sem on 04.02.2016.
 */
public class BasicSequenceParser implements WidgetGenerator {

   public static final String ELEMENT_NAME = "sequence";
   public static final Logger LOGGER = Logger.getLogger(BasicSequenceParser.class.getName());

   @Override
   public Node createWidget(WidgetFactory controller, Pane parentWidget, org.w3c.dom.Node xsdNode) {
      if (!(xsdNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)) {
         return null;
      }

      final Element elementNode = (Element) xsdNode;
      if (!elementNode.getLocalName().equals(ELEMENT_NAME)) {
         return null;
      }

      try {
         NodeList matchingTypeNodes = XPathUtil.evaluateXPath(controller.getDefaultNamespaceContext(), xsdNode);
         Pane contentNodesPane = new VBox();
         for (int i = 0; i < matchingTypeNodes.getLength(); i++) {
            final Element item = (Element) matchingTypeNodes.item(i);
            final int minOccurs = Integer.parseInt(item.getAttribute("minOccurs"));
            final String maxOccursString = item.getAttribute("maxOccurs");
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
