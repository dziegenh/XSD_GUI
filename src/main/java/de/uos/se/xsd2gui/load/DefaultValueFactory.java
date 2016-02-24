package de.uos.se.xsd2gui.load;

import de.uos.se.xsd2gui.models.XSDModel;
import org.w3c.dom.Element;

/**
 * created: 24.02.2016
 *
 * @author Falk Wilke
 */
public class DefaultValueFactory
        implements IValueFactory
{
    //the name of the default value attribute
    public static final String DEFAULT = "default";

    @Override
    public String getValueFor(XSDModel model, String defaultValue)
    {
        Element xsdNode = model.getXSDNode();
        if (xsdNode.hasAttribute("fixed"))
            return xsdNode.getAttribute("fixed");
        String defaultAttributeValue = xsdNode.getAttribute(DEFAULT);
        return defaultAttributeValue.trim().isEmpty() ? defaultValue : defaultAttributeValue;
    }

    @Override
    public int getMinimumNumberOfElements(XSDModel model, Element element)
    {
        return 0;
    }
}
