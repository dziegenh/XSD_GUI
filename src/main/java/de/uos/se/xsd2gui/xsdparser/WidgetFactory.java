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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dziegenhagen
 */
public class WidgetFactory {

    /**
     * A namespace context which the widget generators can access.
     */
    private final NamespaceContext namespaceContext;
    /**
     * The widget generators which parse specific XSD elements.
     */
    private List<WidgetGenerator> generators = new LinkedList<>();

    /**
     * The constructor, making this object use the provided {@linkplain NamespaceContext}
     * @param namespaceContext the context to use
     */
    public WidgetFactory(NamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    /**
     * Same as calling <i>new WidgetFactory(new DefaultNamespaceContext())</i>
     */
    public WidgetFactory() {
        this(new DefaultNamespaceContext());
    }

    /**

     * Adds a widget generator.
     *
     * @param generator
     */
    public void addWidgetGenerator(WidgetGenerator generator) {
        this.generators.add(generator);
    }

    /**
     * Starts the XSD parsing using the document node.
     *
     * @param doc
     * @param rootWidget
     */
    public XSDModel parseXsd(Document doc, Pane rootWidget,String nameSpaceSchemaLocation) {
        Element documentRoot = doc.getDocumentElement();
        org.w3c.dom.Node intermediateNode = XPathUtil.evaluateXPath(documentRoot, "current()/xs:element/node()[not(self::text())]").item(0);
        RootModel rootModel = new RootModel((Element) intermediateNode.getParentNode(),nameSpaceSchemaLocation);
        parseXsdNode(rootWidget, intermediateNode, rootModel);
        return rootModel;
    }

    /**
     * Tries to parse the given xsdNode using the available widget generators.
     * If nothing was generated, the step is repeated for each child node.
     *
     * @param rootWidget
     * @param xsdNode
     */
    public void parseXsdNode(Pane rootWidget, org.w3c.dom.Node xsdNode, XSDModel rootModel) {

        boolean guiNodeCreated = false;
        for (WidgetGenerator generator : generators) {
            Node nodeWidget = generator.createWidget(this, rootWidget, xsdNode, rootModel);
            if (null != nodeWidget) {
                rootWidget.getChildren().add(nodeWidget);
                
                if (guiNodeCreated) {
                    Logger.getLogger(WidgetFactory.class.getName()).log(Level.INFO, "More then one GUI node created for {0}", xsdNode);
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

    /**
     * If set, Generators can access a shared default namespace.
     *
     * @return
     */
    public NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }

}
