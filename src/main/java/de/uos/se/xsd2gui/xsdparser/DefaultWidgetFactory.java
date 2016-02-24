package de.uos.se.xsd2gui.xsdparser;

import de.uos.se.xsd2gui.models.RootModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.util.DefaultNamespaceContext;
import de.uos.se.xsd2gui.util.XPathUtil;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
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
     * @param namespaceContext the context to use
     */
    public DefaultWidgetFactory(NamespaceContext namespaceContext)
    {
        super(namespaceContext, new DefaultValueFactory());
    }

    public DefaultWidgetFactory(IValueFactory valueFactory)
    {
        super(new DefaultNamespaceContext(), valueFactory);
    }

    /**
     * Starts the XSD parsing using the document node.
     *
     * @param doc the document from where the xml/xsd data shall be parsed
     * @param rootWidget the root widget where every generated {@linkplain Node} shall appear
     */
    @Override
    public RootModel parseXsd(Document doc, Pane rootWidget, String nameSpaceSchemaLocation) {
        Element documentRoot = doc.getDocumentElement();
        NodeList list = XPathUtil.evaluateXPath(documentRoot, "current()/xs:element/node()[not(self::text())]");
        org.w3c.dom.Element firstElement = (Element) XPathUtil.evaluateXPath(documentRoot, "current()/xs:element")
                                                              .item(0);
        RootModel rootModel = new RootModel(firstElement, nameSpaceSchemaLocation);
        for (int i = 0; i < list.getLength(); i++)
        {
            parseXsdNode(rootWidget, list.item(0), rootModel);
        }
        return rootModel;
    }

    /**
     * Removes a widget generator.
     *
     * @param localCustomTypeParser
     */
    public void removeWidgetGenerator(WidgetGenerator localCustomTypeParser)
    {
        this.generators.remove(localCustomTypeParser);
    }

    /**
     * Tries to parse the given xsdNode using the available widget generators.
     * If nothing was generated, the step is repeated for each child node.
     *
     * @param rootWidget rootWidget the root widget where every generated {@linkplain Node} shall
     *                   appear
     * @param xsdNode the {@link org.w3c.dom.Node} from where parsing shall start
     */
    @Override
    public void parseXsdNode(Pane rootWidget, org.w3c.dom.Node xsdNode, XSDModel rootModel) {

        boolean guiNodeCreated = false;
        for (IWidgetGenerator generator : getGenerators())
        {
            Node nodeWidget = generator.createWidget(this, rootWidget, xsdNode, rootModel);
            if (null != nodeWidget) {
                rootWidget.getChildren().add(nodeWidget);
                
                if (guiNodeCreated) {
                    Logger.getLogger(DefaultWidgetFactory.class.getName())
                          .log(Level.INFO, "More then one GUI node created for {0}", xsdNode);
                }

                guiNodeCreated = true;
            }
        }

        if (!guiNodeCreated) {

            Logger.getLogger(WidgetFactory.class.getName()).log(Level.INFO, "No GUI node created for {0}", xsdNode);

            if (xsdNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element nodeEl = (Element) xsdNode;
                NodeList nodeElChildren = nodeEl.getChildNodes();

                for (int i = 0; i < nodeElChildren.getLength(); i++) {
                    parseXsdNode(rootWidget, nodeElChildren.item(i), rootModel);
                }
            }
        }

    }

}
