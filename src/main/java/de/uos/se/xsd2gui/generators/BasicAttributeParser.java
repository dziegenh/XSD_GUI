package de.uos.se.xsd2gui.generators;

import de.uos.se.xsd2gui.models.AttributeModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.models.constraints.FixedValueConstraint;
import de.uos.se.xsd2gui.models.constraints.IntegerConstraint;
import de.uos.se.xsd2gui.models.constraints.NoPureWhitespaceStringConstraint;
import de.uos.se.xsd2gui.xsdparser.AbstractWidgetFactory;
import de.uos.se.xsd2gui.xsdparser.IWidgetGenerator;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextField;
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
public class BasicAttributeParser
        implements IWidgetGenerator
{

    //the fixed attribute name
    public static final String FIXED = "fixed";
    //the tape attribute name
    public static final String TYPE = "type";

    @Override
    public javafx.scene.Node createWidget(AbstractWidgetFactory factory, Pane parentWidget, Node
            xsdNode, XSDModel parentModel)
    {

        //check for correct types and attributes
        if (! (xsdNode.getNodeType() == Node.ELEMENT_NODE))
        {
            return null;
        }

        final Element elementNode = (Element) xsdNode;
        if (! elementNode.getLocalName().equals("attribute"))
        {
            return null;
        }

        final String type = elementNode.getAttribute("type");

        if (null == type)
        {
            return null;
        }
        XSDModel model = new AttributeModel(elementNode);
        //important that the submodel is added before asking the factoryx for values since the
        // location of the model is used for generating "paths"
        parentModel.addSubModel(model);
        // TODO create the desired GUI element (Textfield, integer input etc.)
        // TODO use the attribute constraints ("required", "default" etc.)
        Control inputWidget = null;
        String fixed = elementNode.getAttribute(FIXED);
        //System.out.println(XSDPathUtil.parseFromXMLNode(elementNode));
        switch (elementNode.getAttribute(TYPE))
        {
            case "xs:unsignedInt":
                // TODO create constraints for unsigned int type


            case "xs:int":
                int initialValue = Integer.parseInt(factory.getValueFor(model, "0"));
                model.addConstraint(new IntegerConstraint());
                if (model.isRequired())
                    model.addConstraint(new NoPureWhitespaceStringConstraint());
                if (model.isFixed())
                {
                    model.addConstraint(new FixedValueConstraint(fixed));
                    initialValue = Integer.parseInt(fixed);
                }

                IntegerSpinnerValueFactory spinnerFactory = new IntegerSpinnerValueFactory(
                        Integer.MIN_VALUE, Integer.MAX_VALUE, initialValue);
                Spinner<Integer> spinner = new Spinner<>(spinnerFactory);
                spinner.setEditable(false);
                model.valueProperty().setValue(spinnerFactory.getValue().toString());
                spinner.editorProperty().getValue().textProperty()
                        .bindBidirectional(model.valueProperty());
                inputWidget = spinner;
                break;

            case "xs:string":
                String defaultStringValue = factory.getValueFor(model, "");
                if (model.isRequired())
                    model.addConstraint(new NoPureWhitespaceStringConstraint());
                if (! fixed.trim().isEmpty())
                {
                    model.addConstraint(new FixedValueConstraint(fixed));
                    defaultStringValue = fixed;
                }
                TextField textField = new TextField();
                textField.textProperty().bindBidirectional(model.valueProperty());
                model.valueProperty().setValue(defaultStringValue);
                inputWidget = textField;
                break;
            default:
                model = null;
                break;
        }

        if (null != inputWidget)
        {
            parentModel.addSubModel(model);
            Label textFieldLabel = new Label(elementNode.getAttribute("name"));
            Label typeLabel = new Label(" (" + elementNode.getAttribute("type") + ")");
            return new HBox(10, textFieldLabel, inputWidget, typeLabel);
        }

        return null;

    }
}
