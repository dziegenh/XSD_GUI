package de.uos.se.xsd2gui.generators;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.uos.se.xsd2gui.xsdparser.WidgetFactory;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.uos.se.xsd2gui.xsdparser.WidgetGenerator;
import de.uos.se.xsd2gui.app.XsdParserApp;

/**
 * Creates GUI components for simpleType tags.
 * 
 * @author dziegenhagen
 */
public class SimpleTypeParser implements WidgetGenerator {

    @Override
    public javafx.scene.Node createWidget(WidgetFactory controller, Pane parentWidget, Node xsdNode) {

        // TODO das kann ggf. auch als XPath ausgedrueckt werden...
        if (xsdNode.getNodeType() != Node.ELEMENT_NODE || !((Element) xsdNode).getLocalName().equals("simpleType")) {
            return null;
        }

        XPathFactory xp = XPathFactory.newInstance();
        XPath newXPath = xp.newXPath();
        newXPath.setNamespaceContext(controller.getDefaultNamespaceContext());

        NodeList enumValues;
        try {
            enumValues = (NodeList) newXPath.evaluate("xs:restriction[@base='xs:string']/xs:enumeration/@value", xsdNode, XPathConstants.NODESET);
            if (enumValues.getLength() < 1) {
                return null;
            }

            ComboBox<String> comboBox = new ComboBox<String>();
            for (int i = 0; i < enumValues.getLength(); i++) {
                Node item = enumValues.item(i);
                comboBox.getItems().add(item.getNodeValue());
            }

            return comboBox;
            
        } catch (XPathExpressionException ex) {
            Logger.getLogger(XsdParserApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
