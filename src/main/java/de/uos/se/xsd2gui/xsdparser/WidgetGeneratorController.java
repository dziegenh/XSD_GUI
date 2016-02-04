package de.uos.se.xsd2gui.xsdparser;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author dziegenhagen
 */
public class WidgetGeneratorController {

    /**
     * The widget generators which parse specific XSD elements.
     */
    List<WidgetGenerator> generators = new LinkedList<>();

    /**
     * A namespace context which the widget generators can access.
     */
    private NamespaceContext defaultNamespaceContext = null;

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
    public void parseXsd(Document doc, Pane rootWidget) {
        Element documentRoot = doc.getDocumentElement();
        parseXsdNode(rootWidget, documentRoot);
    }

    /**
     * Tries to parse the given xsdNode using the available widget generators.
     * If nothing was generated, the step is repeated for each child node.
     *
     * @param rootWidget
     * @param xsdNode
     */
    public void parseXsdNode(Pane rootWidget, org.w3c.dom.Node xsdNode) {

        boolean guiNodeCreated = false;
        for (WidgetGenerator generator : generators) {
            Node nodeWidget = generator.createWidget(this, rootWidget, xsdNode);
            if (null != nodeWidget) {
                rootWidget.getChildren().add(nodeWidget);
                
                if (guiNodeCreated) {
                    Logger.getLogger(WidgetGeneratorController.class.getName()).log(Level.INFO, "More then one GUI node created for {0}", xsdNode);
                }

                guiNodeCreated = true;
            }
        }

        if (!guiNodeCreated) {

           // Logger.getLogger(WidgetGeneratorController.class.getName()).log(Level.INFO, "No GUI node created for {0}", xsdNode);

            if (xsdNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element nodeEl = (Element) xsdNode;
                NodeList nodeElChildren = nodeEl.getChildNodes();

                for (int i = 0; i < nodeElChildren.getLength(); i++) {
                    parseXsdNode(rootWidget, nodeElChildren.item(i));
                }
            }
        }

    }

    /**
     * If set, Generators can access a shared default namespace.
     *
     * @return
     */
    public NamespaceContext getDefaultNamespaceContext() {
        return this.defaultNamespaceContext;
    }

    /**
     * Sets the shared default namespace.
     *
     * @param defaultNamespaceContext
     */
    public void setDefaultNamespaceContext(NamespaceContext defaultNamespaceContext) {
        this.defaultNamespaceContext = defaultNamespaceContext;
    }

}
