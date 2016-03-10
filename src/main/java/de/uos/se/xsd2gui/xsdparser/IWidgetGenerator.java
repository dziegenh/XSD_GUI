package de.uos.se.xsd2gui.xsdparser;

import de.uos.se.xsd2gui.models.XSDModel;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.Optional;

/**
 * A Widget generator capable of creating widgets.
 * Caution: because of the resurcsive structure
 * {@linkplain #createWidget(AbstractWidgetFactory, Pane, org.w3c.dom.Node, XSDModel)} may appear
 * more than once in a single stacktrace.
 *
 * @author dziegenhagen
 */
public interface IWidgetGenerator
{

    /**
     * Analyses the xsdNode and created a corresponding widget if the node
     * matches. Otherwise an empty {@linkplain Optional} is returned.
     *
     * @param factory
     *         the "parent" factory which can be used to generate subwidgets or acces the
     *         {@linkplain javax.xml.namespace.NamespaceContext} provided
     * @param parentWidget
     *         the parent widget where the created components should be added
     * @param xsdNode
     *         the xsd node to create a widget for
     *
     * @return a {@linkplain Node} containing the parsed widget if present
     */
    Optional<Node> createWidget(AbstractWidgetFactory factory, Pane parentWidget, org.w3c.dom
            .Node xsdNode, XSDModel parentModel);

}
