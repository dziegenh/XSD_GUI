package de.uos.se.xsd2gui.generators;

import de.uos.se.xsd2gui.models.SequenceModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.util.XPathUtil;
import de.uos.se.xsd2gui.xsdparser.WidgetFactory;
import de.uos.se.xsd2gui.xsdparser.WidgetGenerator;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by sem on 04.02.2016.
 */
public class BasicSequenceParser implements WidgetGenerator {

   public static final String ELEMENT_NAME = "sequence";
   public static final Logger LOGGER = Logger.getLogger(BasicSequenceParser.class.getName());

   @Override
   public Node createWidget(WidgetFactory controller, Pane parentWidget, org.w3c.dom.Node xsdNode, XSDModel parentModel) {
      if (!(xsdNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)) {
         return null;
      }

      final Element elementNode = (Element) xsdNode;
      if (!elementNode.getLocalName().equals(ELEMENT_NAME)) {
         return null;
      }
      XSDModel model = new SequenceModel(elementNode);
      parentModel.addSubModel(model);

      NodeList matchingTypeNodes = XPathUtil.evaluateXPath(controller.getNamespaceContext(), xsdNode, "./xs:element[@minOccurs and @maxOccurs]");
      Pane contentNodesPane = new HBox(20);
      final Pane nestedContent = new VBox();
      final SequenceReparser reparser = new SequenceReparser(matchingTypeNodes, model);
      Pane addContent = new VBox();
      List<Button> addButtons = new LinkedList<>();
      reparser.elementNames().forEach(name -> addButtons.add(new Button("+" + name)));
      addButtons.forEach(button -> button.setOnAction(ev -> reparser.add(nestedContent, button.getText().substring(1), controller)));
      addContent.getChildren().addAll(addButtons);


      contentNodesPane.getChildren().add(nestedContent);
      contentNodesPane.getChildren().add(addContent);
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
            Pane elementPane = new HBox(20);
            Button delete = new Button("-");
            delete.setOnAction(ev -> nestedContent.getChildren().remove(elementPane));
            elementPane.getChildren().add(delete);
            controller.parseXsdNode(elementPane, item, model);
            nestedContent.getChildren().add(elementPane);
         }
      }
      return contentNodesPane;
   }

   public class SequenceReparser {
      private final Map<String, Element> _elements;
      private final XSDModel _model;

      private SequenceReparser(NodeList elements, XSDModel model) {
         this._elements = new HashMap<>();
         for (int i = 0; i < elements.getLength(); i++) {
            if (elements.item(i).getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
               throw new IllegalArgumentException("a nom-element node was found: " + elements.item(i));
            Element elem = (Element) elements.item(i);
            String name = elem.getAttribute("name");
            if (name == null)
               throw new IllegalArgumentException("node does not have a name attribute: " + elem);
            this._elements.put(name, elem);
         }
         this._model = model;
      }

      public void add(Pane widget, String name, WidgetFactory factory) {
         Pane elementPane = new HBox(20);
         Button delete = new Button("-");
         delete.setOnAction(ev -> widget.getChildren().remove(elementPane));
         elementPane.getChildren().add(delete);
         factory.parseXsdNode(elementPane, this._elements.get(name), this._model);
         widget.getChildren().add(elementPane);
      }

      public Set<String> elementNames() {
         return Collections.unmodifiableSet(this._elements.keySet());
      }

   }

}
