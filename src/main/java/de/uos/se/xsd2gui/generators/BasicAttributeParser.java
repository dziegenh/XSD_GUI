package de.uos.se.xsd2gui.generators;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import de.uos.se.xsd2gui.xsdparser.WidgetGenerator;
import de.uos.se.xsd2gui.xsdparser.WidgetGeneratorController;

/**
 * Creates GUI components for attribute tags with basic XSMLSchema types (e.g.
 * "<attribute type='xs:string' />".
 *
 * @author dziegenhagen
 */
public class BasicAttributeParser implements WidgetGenerator {

    @Override
    public javafx.scene.Node createWidget(WidgetGeneratorController controller, Pane parentWidget, Node xsdNode) {

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

        // TODO create the desired GUI element (Textfield, integer input etc.)
        // TODO use the attribute constraints ("required", "default" etc.)
        Control inputWidget = null;
        switch (elementNode.getAttribute("type")) {
            case "xs:int":
                IntegerSpinnerValueFactory factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE);
                Spinner spinner = new Spinner(factory);
                spinner.setEditable(true);
                inputWidget = spinner;
                break;

            case "xs:string":
                inputWidget = new TextField();
                break;
        }
        
        if (null != inputWidget) {
            Label textFieldLabel = new Label(elementNode.getAttribute("name"));
            Label typeLabel = new Label(" (" + elementNode.getAttribute("type") + ")");
            return new HBox(10, textFieldLabel, inputWidget, typeLabel);
        }

        return null;

    }
}
