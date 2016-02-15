package de.uos.se.xsd2gui.generators;

import de.uos.se.xsd2gui.models.SequenceModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.util.SequenceReparser;
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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by sem on 04.02.2016.
 */
public class BasicSequenceParser implements WidgetGenerator {

   public static final String ELEMENT_NAME = "sequence";
   public static final Logger LOGGER = Logger.getLogger(BasicSequenceParser.class.getName());

   @Override
   public Node createWidget(WidgetFactory factory, Pane parentWidget, org.w3c.dom.Node xsdNode, XSDModel parentModel) {
      if (!(xsdNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)) {
         return null;
      }

      final Element elementNode = (Element) xsdNode;
      if (!elementNode.getLocalName().equals(ELEMENT_NAME)) {
         return null;
      }
      XSDModel model = new SequenceModel(elementNode);
      parentModel.addSubModel(model);

      NodeList matchingTypeNodes = XPathUtil.evaluateXPath(factory.getNamespaceContext(), xsdNode, "./xs:element[@minOccurs and @maxOccurs]");
      Pane contentNodesPane = new HBox(20);
      Pane nestedContent = new VBox();
      SequenceReparser reparser = new SequenceReparser(matchingTypeNodes, model);
      Pane addContent = new VBox();
      List<Button> addButtons = new LinkedList<>();
      reparser.elementNames().forEach(name -> addButtons.add(new Button("+" + name)));
      addButtons.forEach(button -> button.setOnAction(ev -> reparser.add(nestedContent, button.getText().substring(1), factory)));
      addContent.getChildren().addAll(addButtons);


      contentNodesPane.getChildren().add(nestedContent);
      contentNodesPane.getChildren().add(addContent);
      reparser.reparseToMinimumOcc(nestedContent, factory);
      return contentNodesPane;
   }

}
