package de.uos.se.xsd2gui.generators;

import de.uos.se.xsd2gui.models.AttributeModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.models.constraints.FixedValueConstraint;
import de.uos.se.xsd2gui.models.constraints.NoEmptyStringConstraint;
import de.uos.se.xsd2gui.models.constraints.NumericXSDConstraint;
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
public class BasicAttributeParser
        implements WidgetGenerator
{

    //the fixed attribute name
    public static final String FIXED = "fixed";
    //the tape attribute name
    public static final String TYPE = "type";
    //the name of the default value attribute
    public static final String DEFAULT = "default";

    @Override
    public javafx.scene.Node createWidget(WidgetFactory controller, Pane parentWidget, Node
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
        XSDModel model;
        // TODO create the desired GUI element (Textfield, integer input etc.)
        // TODO use the attribute constraints ("required", "default" etc.)
        Control inputWidget = null;
        String fixed = elementNode.getAttribute(FIXED);
        switch (elementNode.getAttribute(TYPE))
        {
            case "xs:int":
                model = new AttributeModel(elementNode);
                IntegerSpinnerValueFactory factory;
                if (model.isRequired())
                    model.addConstraint(new NumericXSDConstraint());
                if (! fixed.trim().isEmpty())
                {
                    model.addConstraint(new FixedValueConstraint(fixed));
                    int fixedIntValue = Integer.parseInt(fixed);
                    factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(fixedIntValue,
                                                                                 fixedIntValue,
                                                                                 fixedIntValue);
                } else
                {
                    int initialValue = Integer
                            .parseInt(getDefaultValueFromElement(elementNode, "0"));
                    factory = new IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE,
                                                             initialValue);
                }
                Spinner<Integer> spinner = new Spinner<>(factory);
                spinner.setEditable(false);
                model.valueProperty().setValue(factory.getValue().toString());
                spinner.editorProperty().getValue().textProperty()
                       .bindBidirectional(model.valueProperty());
                inputWidget = spinner;
                break;

            case "xs:string":
                model = new AttributeModel(elementNode);
                String defaultStringValue = getDefaultValueFromElement(elementNode, "");
                if (model.isRequired())
                    model.addConstraint(new NoEmptyStringConstraint());
                if (! fixed.trim().isEmpty())
                {
                    model.addConstraint(new FixedValueConstraint(fixed));
                    defaultStringValue = fixed;
                }
                model = new AttributeModel(elementNode);
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

    private String getDefaultValueFromElement(Element elementNode, String notPresentValue)
    {
        String defaultValue = elementNode.getAttribute(DEFAULT);
        return defaultValue.trim().isEmpty() ? notPresentValue : defaultValue;
    }


}
