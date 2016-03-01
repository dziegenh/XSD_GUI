package de.uos.se.xsd2gui.node_generators;

import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.models.constraints.FixedValueConstraint;
import de.uos.se.xsd2gui.models.constraints.IntegerConstraint;
import de.uos.se.xsd2gui.models.constraints.NoPureWhitespaceStringConstraint;
import de.uos.se.xsd2gui.models.constraints.UIntConstraint;
import de.uos.se.xsd2gui.value_generators.IValueGenerator;
import javafx.beans.value.ObservableValue;
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
 *
 * @author Falk Wilke
 */
public class DefaultNodeGenerator
        implements INodeGenerator
{
    //the fixed attribute name
    public static final String FIXED = "fixed";
    //the type attribute name
    public static final String TYPE = "type";
    //the default spacing
    private static final int DEFAULT_SPACING = 10;

    @Override
    public Node getAndBindControl(IValueGenerator factory, XSDModel model)
    {
        if (! model.hasParent())
            throw new IllegalArgumentException("given model does not have a parent set");
        Element elementNode = model.getXSDNode();
        String fixed = elementNode.getAttribute(FIXED);
        Control inputWidget = null;
        //differ by type
        switch (elementNode.getAttribute(TYPE))
        {
            case "xs:unsignedInt":
                model.addConstraint(new UIntConstraint());


            case "xs:int":
                //int can be processed by using a Spinner
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
        //only if an element could be created:
        if (null != inputWidget)
        {
            //disable for fixed values
            inputWidget.setDisable(model.isFixed());
            //bind input validation tooltips
            bindTooltips(inputWidget, model);
            //create label for name and type
            Label textFieldLabel = new Label(elementNode.getAttribute("name"));
            Label typeLabel = new Label(" (" + elementNode.getAttribute("type") + ")");
            //collect within hbox
            return new HBox(10, textFieldLabel, inputWidget, typeLabel);
        }
        return null;
    }

    /**
     * This method binds tooltips for the
     * {@linkplain XSDModel#violatedProperty()} and the {@linkplain XSDModel#violationTextProperty()} to the given {@linkplain Control}
     *
     * @param inputWidget
     *         the {@linkplain Control} to bind to
     * @param model
     *         the {@linkplain XSDModel} to bind to
     */
    private void bindTooltips(Control inputWidget, XSDModel model)
    {
        model.violatedProperty().addListener(
                (observable, oldValue, newValue) -> makeTooltip(inputWidget, model, observable,
                                                                oldValue, newValue));
        if (model.violatedProperty().get())
        {
            setError(inputWidget, model);
        }
    }

    /**
     * This method processes a change event to
     * {@linkplain XSDModel#violatedProperty()}. It styles the given {@linkplain Control} if the given {@linkplain XSDModel} is violated. It also adds a tooltip for {@linkplain XSDModel#violationTextProperty()}
     *
     * @param inputWidget
     *         the widget which shall be styled
     * @param model
     *         the {@linkplain XSDModel} to bind tooltips to
     * @param observable
     *         see {@linkplain javafx.beans.value.ChangeListener}
     * @param oldValue
     *         see {@linkplain javafx.beans.value.ChangeListener}
     * @param newValue
     *         see {@linkplain javafx.beans.value.ChangeListener}
     */
    private void makeTooltip(Control inputWidget, XSDModel model, ObservableValue<? extends
            Boolean> observable, Boolean oldValue, Boolean newValue)

    {
        if (newValue)
        {
            setError(inputWidget, model);
        } else
        {
            removeError(inputWidget);
        }
    }

    private void setError(Control con, XSDModel model)
    {
        Tooltip tt = new Tooltip();
        tt.textProperty().bind(model.violationTextProperty());
        con.setTooltip(tt);
        con.setStyle("-fx-text-box-border: red ;" + "     -fx-focus-color: red ;");
    }


    private void removeError(Control con)
    {
        con.setStyle("");
        con.setTooltip(null);
    }

    @Override
    public Pane getSimpleContainerFor(XSDModel xsdModel, int spacing)
    {
        if (! xsdModel.hasParent())
            throw new IllegalArgumentException("given model does not have a parent set");
        if (xsdModel.hasName())
        {
            if (! xsdModel.getXSDNode().hasAttribute(TYPE))
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
