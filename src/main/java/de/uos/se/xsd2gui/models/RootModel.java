package de.uos.se.xsd2gui.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * created: 09.02.2016
 * A model representing the root of an xml document. As such, the generated xml from its
 * submodels is directly appened to the document
 *
 * @author Falk Wilke
 */
public class RootModel
        extends XSDModel
{
    private final String _schemaLocation;

    public RootModel(Element xsdNode, String schemaLocation)
    {
        super(xsdNode);
        this._schemaLocation = schemaLocation;
    }

    @Override
    public void parseToXML(Document doc, Element parent)
    {
        //this has to be handled differently from a "normal" element since there is no parent!
        Element root = doc.createElement(this.getName());
        root.setAttribute("xsi:noNamespaceSchemaLocation", this._schemaLocation);
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        doc.appendChild(root);
        //as usual parse submodels
        for (XSDModel xsdm : getSubModels())
        {
            xsdm.parseToXML(doc, doc.getDocumentElement());
        }
    }
}
