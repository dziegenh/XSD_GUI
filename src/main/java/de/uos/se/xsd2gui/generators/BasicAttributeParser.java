package de.uos.se.xsd2gui.generators;

import de.uos.se.xsd2gui.models.AttributeModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.util.Patterns;
import de.uos.se.xsd2gui.xsdparser.WidgetFactory;
import de.uos.se.xsd2gui.xsdparser.WidgetGenerator;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Creates GUI components for attribute tags with basic XSMLSchema types (e.g.
 * "<attribute type='xs:string' />".
 *
 * @author dziegenhagen
 */
public class BasicAttributeParser implements WidgetGenerator {

   @Override
   public javafx.scene.Node createWidget(WidgetFactory controller, Pane parentWidget, Node xsdNode, XSDModel parentModel) {

      if (!(xsdNode.getNodeType() == Node.ELEMENT_NODE)) {
         return null;
      }

      final Element elementNode = (Element) xsdNode;
      if (!elementNode.getLocalName().equals("attribute")) {
         return null;
      }

      final String type = elementNode.getAttribute("type");

      if (null == type) {
         return null;
      }
      XSDModel model;
      // TODO create the desired GUI element (Textfield, integer input etc.)
      // TODO use the attribute constraints ("required", "default" etc.)
      Control inputWidget = null;
      String use = elementNode.getAttribute("use");
      switch (elementNode.getAttribute("type")) {
         case "xs:int":
            model = new AttributeModel(elementNode);

            IntegerSpinnerValueFactory factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
            Spinner<Integer> spinner = new Spinner<>(factory);
            spinner.setEditable(false);
            model.valueProperty().setValue("0");
            spinner.editorProperty().getValue().textProperty().bindBidirectional(model.valueProperty());
            inputWidget = spinner;
            break;

         case "xs:string":
            model = new AttributeModel(elementNode);
            TextField textField = new TextField();
            TextFormatter<String> formatter = getFormatter(use, Patterns.XS_STRING_PATTERN, Patterns.XS_STRING_PATTERN);
            textField.textProperty().bindBidirectional(model.valueProperty());
            textField.setTextFormatter(formatter);
            model.valueProperty().setValue("");
            inputWidget = textField;
            break;
         default:
            model = null;
            break;
      }

      if (null != inputWidget) {
         parentModel.addSubModel(model);
         Label textFieldLabel = new Label(elementNode.getAttribute("name"));
         Label typeLabel = new Label(" (" + elementNode.getAttribute("type") + ")");
         return new HBox(10, textFieldLabel, inputWidget, typeLabel);
      }

      return null;

   }

   private TextFormatter<String> getFormatter(String use, String requiredPattern, String notRequiredPattern) {
      switch (use) {
         case "required":
            return new TextFormatter<>(change -> change.getControlNewText().matches(requiredPattern) ? change : null);
         default:
            return new TextFormatter<>(change -> change.getControlNewText().matches(notRequiredPattern) ? change : null);
      }
   }

}
