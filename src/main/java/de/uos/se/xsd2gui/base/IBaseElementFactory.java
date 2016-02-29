package de.uos.se.xsd2gui.base;

import de.uos.se.xsd2gui.load.IValueFactory;
import de.uos.se.xsd2gui.models.XSDModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * created: 29.02.2016
 * An Interface representing a basic element creating factory which creates
 * {@linkplain Control} {@linkplain javafx.scene.layout.Pane}
 * for given {@linkplain XSDModel}s.
 *
 * @author Falk Wilke
 */
public interface IBaseElementFactory
{
    /**
     * Returns a Node element bound to {@linkplain XSDModel#valueProperty()}. This can be any
     * type of node, important is that it responds to value changes by setting the corresponding
     * model's value.
     * It is preferred to return a
     * {@linkplain javafx.scene.layout.Pane} like {@linkplain javafx.scene.layout.HBox}.
     * The given {@linkplain XSDModel} must! have a parent, otherwise an exception is thrown.
     * This is necessary since the whole 'hierarchy' needs to be evaluated to generate proper
     * components and values for them.
     *
     * @param factory
     *         the factory to rely on for value generation
     * @param model
     *         the model to generate a control for
     *
     * @return a {@linkplain Control} which is bound to the {@linkplain XSDModel#valueProperty()}
     *
     * @throws IllegalArgumentException
     *         if
     *         {@linkplain XSDModel#hasParent()} returns <i>false</i> for the given {@linkplain XSDModel}
     */
    Node getAndBindControl(IValueFactory factory, XSDModel model) throws IllegalArgumentException;

    /**
     * This method constructs a {@linkplain Pane} which is suited for housing a gui element
     * representing the given model.
     * It is important to note that a container *for* the {@linkplain Node} representing the
     * given model is created. For the creatin of that representation take a look at
     * {@linkplain #getAndBindControl(IValueFactory, XSDModel)}
     *
     * @param xsdModel
     *         the model to create a housing for
     * @param spacing
     *         the spacing to use
     *
     * @return a {@linkplain Pane} where the given {@linkplain XSDModel} can be housed within
     */
    Pane getSimpleContainerFor(XSDModel xsdModel, int spacing);

    /**
     * @see {@linkplain #getSimpleContainerFor(XSDModel, int)}
     * Does the same, but does not take a spacing param.
     */
    Pane getSimpleContainerFor(XSDModel xsdModel);

    /**
     * This method creates a new
     * {@linkplain Pane} possibly using the information stored within the given {@linkplain Node}.
     * The reason this method exists is that sometimes general purpose containers are needed.
     * They should only be used to add controls (like buttons triggering certain actions) and
     * nothing directly involved with the xsd itself. It is strongly advised to use
     * {@linkplain #getAndBindControl(IValueFactory, XSDModel)} and {@linkplain #getSimpleContainerFor(XSDModel, int)}
     * to generate directly involved containers. Bypassing this does circumvent flexibility.
     *
     * @param xsdNode
     *         the {@linkplain Node} to use for multi-purpose container generation
     *
     * @return a Pane which can be used for adding
     */
    Pane getMultiPurposeContainer(org.w3c.dom.Node xsdNode);

    /**
     * This method creates a
     * {@linkplain javafx.scene.control.ButtonBase} bound to the given {@linkplain EventHandler} with its {@linkplain javafx.scene.control.ButtonBase#setOnAction(EventHandler)}.
     * The given text is used for labeling the button.
     *
     * @param handler
     *         the handler to bind to the created button
     * @param label
     *         the label for the created button
     *
     * @return a {@linkplain ButtonBase} bound to the given {@linkplain EventHandler}
     */
    ButtonBase getControlForHandler(EventHandler<ActionEvent> handler, String label);

    /**
     * Does essentially the same as {@linkplain #getAndBindControl(IValueFactory, XSDModel)}
     * except that input is restricted to the given values.
     * This is kind of a convenience method so that
     * {@linkplain #getAndBindControl(IValueFactory, XSDModel)} does not need to have excessive
     * knowledge about parsing xsds.
     *
     * @param factory
     *         the factory to rely on for value generation
     * @param model
     *         the model to generate a control for
     * @param enumValues
     *         the values input shall be restricted to
     *
     * @return see {@linkplain #getAndBindControl(IValueFactory, XSDModel)}, only input is
     * limited to the given values
     */
    Node getAndBindRestrictedControl(IValueFactory factory, XSDModel model, List<String>
            enumValues);

    /**
     * Wraps the given
     * {@linkplain Node} into a {@linkplain Labeled} using the information stored within the given {@linkplain XSDModel}.
     * This is necessary since sometimes it is not clear on a global level where for example
     * "elements" end. So this is decided by a
     * {@linkplain de.uos.se.xsd2gui.xsdparser.IWidgetGenerator} and this method is called.
     * The prupose of this class is to make {@linkplain Node} generation more flexible and
     * independent from the control flow of the calling
     * {@linkplain de.uos.se.xsd2gui.xsdparser.IWidgetGenerator}. But sometimes layout and logic
     * are connected too tightly, this is why this method was created to provide a limited amount
     * of independence. IN this case  generation can still be influenced by the caller.
     *
     * @param xsdModel
     *         the mode to use for information
     * @param content
     *         the {@linkplain Node} to wrap up
     *
     * @return A labeled component corresponding to the given model wrapping the given node
     */
    Labeled getTitledContainerFor(XSDModel xsdModel, Node content);
}
