package de.uos.se.xsd2gui.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * created: 09.02.2016
 * A Model representing an <xs:attribute name=".."></xs:attribute>
 *
 * @author Falk Wilke
 */
public class AttributeModel
        extends XSDModel
{

    public AttributeModel(Element xsdNode)
    {
        super(xsdNode);
    }

    @Override
    public void parseToXML(Document doc, Element parent)
    {
        //simply add attribute to parent
        String value = this.valueProperty().getValue();
        if (this.isRequired())
            parent.setAttribute(this.getName(), value);
        else if (! value.trim().isEmpty())
            parent.setAttribute(this.getName(), value);

    }

}
