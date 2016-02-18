package de.uos.se.xsd2gui.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Comparator;

/**
 * created: 09.02.2016
 * A model representing a <xsd:sequence></xsd:sequence>. As such all submodels are handled in row
 *
 * @author Falk Wilke
 */
public class SequenceModel
        extends XSDModel
{
    public SequenceModel(Element xsdNode, Comparator<XSDModel> comparator)
    {
        super(xsdNode, comparator);
    }

    @Override
    public void parseToXML(Document doc, Element parent)
    {
        for (XSDModel xsdModel : this.getSubModels())
        {
            xsdModel.parseToXML(doc, parent);
        }
    }
}
