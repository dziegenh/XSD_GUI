package de.uos.se.xsd2gui.value_generators;

import de.uos.se.xsd2gui.models.XSDModel;
import org.w3c.dom.Element;

/**
 * created: 24.02.2016
 * A default implementaion of a value generator simply returning the provided default value or
 * the values present within the given {@linkplain XSDModel}
 * @author Falk Wilke
 */
public class DefaultValueGenerator
        implements IValueGenerator
{
    //the name of the default value attribute
    public static final String DEFAULT = "default";
    public static final String FIXED = "fixed";

    @Override
    public String getValueFor(XSDModel model, String defaultValue)
    {
        Element xsdNode = model.getXSDNode();
        if (xsdNode.hasAttribute(FIXED))
            return xsdNode.getAttribute(FIXED);
        String defaultAttributeValue = xsdNode.getAttribute(DEFAULT);
        return defaultAttributeValue.trim().isEmpty() ? defaultValue : defaultAttributeValue;
    }

    @Override
    public int getMinimumNumberOfElements(XSDModel model, Element element)
    {
        return 0;
    }
}
