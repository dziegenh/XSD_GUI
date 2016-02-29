package de.uos.se.xsd2gui.xsdparser;

import de.uos.se.xsd2gui.base.DefaultBaseElementFactory;
import de.uos.se.xsd2gui.base.IBaseElementFactory;
import de.uos.se.xsd2gui.load.DefaultValueFactory;
import de.uos.se.xsd2gui.load.IValueFactory;
import de.uos.se.xsd2gui.models.RootModel;
import de.uos.se.xsd2gui.models.XSDModel;
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
    //the value factory to use
    private IValueFactory _valueFactory;
    //the factory for base element like panes or controls
    private IBaseElementFactory _baseFactory;

    /**
     * Same as calling <i>new DefaultWidgetFactory(new DefaultNamespaceContext(), new
     * DefaultValueFactory())</i>
     */
    protected AbstractWidgetFactory()
    {
        this(new DefaultNamespaceContext(), new DefaultValueFactory());
    }

    /**
     * The constructor, making this object use the provided {@linkplain NamespaceContext}
     *
     * @param namespaceContext
     *         the context to use
     */
    protected AbstractWidgetFactory(NamespaceContext namespaceContext, IValueFactory valueFactory)
    {
        this(namespaceContext, valueFactory, new DefaultBaseElementFactory());
    }

    /**
     * The constructor, making this object use the provided {@linkplain NamespaceContext}
     *
     * @param namespaceContext
     *         the context to use
     */
    protected AbstractWidgetFactory(NamespaceContext namespaceContext, IValueFactory
            valueFactory, IBaseElementFactory elementFactory)
    {
        this._namespaceContext = namespaceContext;
        this._generators = new LinkedList<>();
        this._valueFactory = valueFactory;
        this._baseFactory = elementFactory;
    }

    /**
     * Same as calling <i>new DefaultWidgetFactory(namespaceContext,new DefaultValueFactory())</i>
     */
    protected AbstractWidgetFactory(NamespaceContext namespaceContext)
    {
        this(namespaceContext, new DefaultValueFactory());
    }

    public IBaseElementFactory getBaseElementFactory()
    {
        return _baseFactory;
    }

    public void setBaseFactory(IBaseElementFactory _baseFactory)
    {
        this._baseFactory = _baseFactory;
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

    public IValueFactory getValueFactory()
    {
        return _valueFactory;
    }

    public void setValueFactory(IValueFactory valueFactory)
    {
        this._valueFactory = valueFactory;
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
