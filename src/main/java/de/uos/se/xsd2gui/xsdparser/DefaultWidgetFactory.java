package de.uos.se.xsd2gui.xsdparser;

import de.uos.se.xsd2gui.models.RootModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.node_generators.INodeGenerator;
import de.uos.se.xsd2gui.util.DefaultNamespaceContext;
import de.uos.se.xsd2gui.util.XPathUtil;
import de.uos.se.xsd2gui.value_generators.DefaultValueGenerator;
import de.uos.se.xsd2gui.value_generators.IValueGenerator;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dziegenhagen
 */
public class DefaultWidgetFactory
        extends AbstractWidgetFactory
{

    /**
     * Same as calling <i>new DefaultWidgetFactory(new DefaultNamespaceContext())</i>
     */
    public DefaultWidgetFactory()
    {
        this(new DefaultNamespaceContext());
    }

    /**
     * The constructor, making this object use the provided {@linkplain NamespaceContext}
     *
     * @param namespaceContext
     *         the context to use
     */
    public DefaultWidgetFactory(NamespaceContext namespaceContext)
    {
        super(namespaceContext, new DefaultValueGenerator());
    }

    public DefaultWidgetFactory(IValueGenerator valueFactory, INodeGenerator elementFactory)
    {
        super(new DefaultNamespaceContext(), valueFactory, elementFactory);
    }

    public DefaultWidgetFactory(IValueGenerator valueFactory)
    {
        super(new DefaultNamespaceContext(), valueFactory);
    }

    public DefaultWidgetFactory(INodeGenerator elementFactory)
    {
        super(new DefaultNamespaceContext(), new DefaultValueGenerator(), elementFactory);
    }

    /**
     * Starts the XSD parsing using the document node.
     *
     * @param doc
     *         the document from where the xml/xsd data shall be parsed
     * @param rootWidget
     *         the root widget where every generated {@linkplain Node} shall appear
     */
    @Override
    public RootModel parseXsd(Document doc, Pane rootWidget, String nameSpaceSchemaLocation)
    {
        Element documentRoot = doc.getDocumentElement();
        NodeList list = XPathUtil
                .evaluateXPath(documentRoot, "current()/xs:element/node()[not(self::text())]");
        if (list.getLength() == 0)
        {
            Logger.getLogger(this.getClass().getName())
                  .log(Level.SEVERE, "no elements present besides root element, aborting.....");
            return null;
        }
        org.w3c.dom.Element firstElement = (Element) XPathUtil
                .evaluateXPath(documentRoot, "current()/xs:element").item(0);
        RootModel rootModel = new RootModel(firstElement, nameSpaceSchemaLocation);
        for (int i = 0; i < list.getLength(); i++)
        {
            parseXsdNode(rootWidget, list.item(0), rootModel);
        }
        return rootModel;
    }

    /**
     * Tries to parse the given xsdNode using the available widget generators.
     * If nothing was generated, the step is repeated for each child node.
     *
     * @param rootWidget
     *         rootWidget the root widget where every generated {@linkplain Node} shall
     *         appear
     * @param xsdNode
     *         the {@link org.w3c.dom.Node} from where parsing shall start
     */
    @Override
    public void parseXsdNode(Pane rootWidget, org.w3c.dom.Node xsdNode, XSDModel rootModel)
    {

        //abort for text nodes
        if (xsdNode.getNodeType() == org.w3c.dom.Node.TEXT_NODE)
            return;
        boolean guiNodeCreated = false;
        for (IWidgetGenerator generator : getGenerators())
        {
            Optional<Node> nodeWidget = generator
                    .createWidget(this, rootWidget, xsdNode, rootModel);
            if (nodeWidget.isPresent())
            {
                rootWidget.getChildren().add(nodeWidget.get());

                if (guiNodeCreated)
                {
                    Logger.getLogger(DefaultWidgetFactory.class.getName())
                          .log(Level.INFO, "More then one GUI node created for {0}", xsdNode);
                }

                guiNodeCreated = true;
            }
        }

        if (! guiNodeCreated)
        {

            Logger.getLogger(DefaultWidgetFactory.class.getName())
                  .log(Level.INFO, "No GUI node created for {0}", xsdNode);

            if (xsdNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
            {
                Element nodeEl = (Element) xsdNode;
                NodeList nodeElChildren = nodeEl.getChildNodes();

                for (int i = 0; i < nodeElChildren.getLength(); i++)
                {
                    parseXsdNode(rootWidget, nodeElChildren.item(i), rootModel);
                }
            }
        }

    }

}
