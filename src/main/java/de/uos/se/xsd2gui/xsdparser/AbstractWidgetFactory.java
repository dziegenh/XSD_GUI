package de.uos.se.xsd2gui.xsdparser;

import de.uos.se.xsd2gui.load_generators.DefaultValueGenerator;
import de.uos.se.xsd2gui.load_generators.IValueGenerator;
import de.uos.se.xsd2gui.models.RootModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.node_generators.DefaultNodeGenerator;
import de.uos.se.xsd2gui.node_generators.INodeGenerator;
import de.uos.se.xsd2gui.util.DefaultNamespaceContext;
import javafx.scene.layout.Pane;
import org.w3c.dom.Document;

import javax.xml.namespace.NamespaceContext;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * created: 24.02.2016
 *
 * @author Falk Wilke
 */
public abstract class AbstractWidgetFactory
{
    /**
     * A namespace context which the widget generators can access.
     */
    protected final NamespaceContext _namespaceContext;
    /**
     * The widget generators which parse specific XSD elements.
     */
    private final List<IWidgetGenerator> _generators;
    //the value generator to use
    private IValueGenerator _valueGenerator;
    //the generator for basic nodes like panes or controls
    private INodeGenerator _nodeGenerator;

    /**
     * Same as calling <i>new DefaultWidgetFactory(new DefaultNamespaceContext(), new
     * DefaultValueGenerator())</i>
     */
    protected AbstractWidgetFactory()
    {
        this(new DefaultNamespaceContext(), new DefaultValueGenerator());
    }

    /**
     * The constructor, making this object use the provided {@linkplain NamespaceContext}
     *
     * @param namespaceContext
     *         the context to use
     */
    protected AbstractWidgetFactory(NamespaceContext namespaceContext, IValueGenerator valueFactory)
    {
        this(namespaceContext, valueFactory, new DefaultNodeGenerator());
    }

    /**
     * The constructor, making this object use the provided {@linkplain NamespaceContext}
     *
     * @param namespaceContext
     *         the context to use
     */
    protected AbstractWidgetFactory(NamespaceContext namespaceContext, IValueGenerator
            valueFactory, INodeGenerator nodeGenerator)
    {
        this._namespaceContext = namespaceContext;
        this._generators = new LinkedList<>();
        this._valueGenerator = valueFactory;
        this._nodeGenerator = nodeGenerator;
    }

    /**
     * Same as calling <i>new DefaultWidgetFactory(namespaceContext,new DefaultValueGenerator())</i>
     */
    protected AbstractWidgetFactory(NamespaceContext namespaceContext)
    {
        this(namespaceContext, new DefaultValueGenerator());
    }

    public INodeGenerator getNodeGenerator()
    {
        return _nodeGenerator;
    }

    public void setNodeGenerator(INodeGenerator _baseFactory)
    {
        this._nodeGenerator = _baseFactory;
    }

    /**
     * Adds a widget generator.
     *
     * @param generator
     *         the generator to add
     */
    public void addWidgetGenerator(IWidgetGenerator generator)
    {
        this._generators.add(generator);
    }

    public abstract RootModel parseXsd(Document doc, Pane rootWidget, String
            nameSpaceSchemaLocation);

    public abstract void parseXsdNode(Pane rootWidget, org.w3c.dom.Node xsdNode, XSDModel
            rootModel);

    /**
     * If set, Generators can access a shared default namespace.
     *
     * @return a {@linkplain NamespaceContext} which can be shared (so that all generators can
     * use the same context
     */
    public NamespaceContext getNamespaceContext()
    {
        return this._namespaceContext;
    }

    public List<IWidgetGenerator> getGenerators()
    {
        return Collections.unmodifiableList(_generators);
    }

    public IValueGenerator getValueGenerator()
    {
        return _valueGenerator;
    }

    public void setValueGenerator(IValueGenerator valueFactory)
    {
        this._valueGenerator = valueFactory;
    }

    /**
     * Removes a widget generator.
     *
     * @param widgetGenerator
     *         the parser to remove
     */
    public void removeWidgetGenerator(IWidgetGenerator widgetGenerator)
    {
        this._generators.remove(widgetGenerator);
    }

}
