package de.uos.se.xsd2gui.factories;

import de.uos.se.xsd2gui.load.IValueGenerator;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.models.constraints.FixedValueConstraint;
import de.uos.se.xsd2gui.models.constraints.IntegerConstraint;
import de.uos.se.xsd2gui.models.constraints.NoPureWhitespaceStringConstraint;
import de.uos.se.xsd2gui.models.constraints.UIntConstraint;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.w3c.dom.Element;

import java.util.List;

/**
 * created: 29.02.2016
 * A class providing  a default implementation of {@linkplain INodeGenerator}
 * @author Falk Wilke
 */
public class DefaultNodeGenerator
        implements INodeGenerator
{
    //the fixed attribute name
    public static final String FIXED = "fixed";
    //the tape attribute name
    public static final String TYPE = "type";
    public static final String ELEMENT = "element";
    private static final int DEFAULT_SPACING = 10;

    @Override
    public Node getAndBindControl(IValueGenerator factory, XSDModel model)
    {
        if (! model.hasParent())
            throw new IllegalArgumentException("given model does not have a parent set");
        Element elementNode = model.getXSDNode();
        String fixed = elementNode.getAttribute(FIXED);
        Control inputWidget = null;
        //System.out.println(XSDPathUtil.parseFromXMLNode(elementNode));
        switch (elementNode.getAttribute(TYPE))
        {
            case "xs:unsignedInt":
                model.addConstraint(new UIntConstraint());


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

                SpinnerValueFactory.IntegerSpinnerValueFactory spinnerFactory
                        = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE,
                                                                             Integer.MAX_VALUE,
                                                                             initialValue);
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
        }
        if (null != inputWidget)
        {
            inputWidget.setDisable(model.isFixed());
            Label textFieldLabel = new Label(elementNode.getAttribute("name"));
            Label typeLabel = new Label(" (" + elementNode.getAttribute("type") + ")");
            return new HBox(10, textFieldLabel, inputWidget, typeLabel);
        }
        return null;
    }

    @Override
    public Pane getSimpleContainerFor(XSDModel xsdModel, int spacing)
    {
        if (xsdModel.hasName())
        {
            if (xsdModel.getXSDNode().getLocalName().equals(ELEMENT))
                return new HBox(spacing);
            else
                return new HBox(spacing, new Label(xsdModel.getName()));
        } else
            return new HBox(spacing);
    }

    @Override
    public Pane getSimpleContainerFor(XSDModel xsdModel)
    {
        return getSimpleContainerFor(xsdModel, DEFAULT_SPACING);
    }

    @Override
    public Pane getMultiPurposeContainer(org.w3c.dom.Node xsdNode)
    {
        return new VBox();
    }

    @Override
    public ButtonBase getControlForHandler(EventHandler<ActionEvent> handler, String label)
    {
        Button button = new Button(label);
        button.setOnAction(handler);
        return button;
    }

    @Override
    public ComboBoxBase<String> getAndBindRestrictedControl(IValueGenerator factory, XSDModel
            parentModel, List<String> enumValues)
    {
        ComboBox<String> comboBox = new ComboBox<>();
        if (! parentModel.isRequired())
            comboBox.getItems().add("");
        for (String enumValue : enumValues)
        {
            comboBox.getItems().add(enumValue);
        }
        comboBox.valueProperty().bindBidirectional(parentModel.valueProperty());
        String firstItem = comboBox.getItems().get(0);
        comboBox.setDisable(parentModel.isFixed());
        parentModel.valueProperty().setValue(factory.getValueFor(parentModel, firstItem));
        return comboBox;
    }

    @Override
    public Labeled getTitledContainerFor(XSDModel xsdModel, Node content)
    {
        return new TitledPane(xsdModel.getName(), content);
    }
}
