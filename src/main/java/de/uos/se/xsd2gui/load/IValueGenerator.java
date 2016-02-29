package de.uos.se.xsd2gui.load;

import de.uos.se.xsd2gui.models.XSDModel;
import org.w3c.dom.Element;

/**
 * created: 24.02.2016
 *
 * @author Falk Wilke
 */
public interface IValueGenerator
{

    String STANDARD_DEFAULT_VALUE = "";

    /**
     * Same as calling
     * {@linkplain #getValueFor(XSDModel, String)} with {@linkplain #STANDARD_DEFAULT_VALUE} as
     * second parameter
     */
    default String getValueFor(XSDModel model)
    {
        return getValueFor(model, STANDARD_DEFAULT_VALUE);
    }

    /**
     * This method creates a value for the specified {@linkplain XSDModel}. The parameter is used
     * when this generator is not able to create a decent value since sometimes that depends on
     * the context of the calling method.
     * This makes it more flexible to handle.
     *
     * @param xsdNode
     *         the model to generate a value for
     * @param defaultValue
     *         the default value to use if a default value would be returned
     *
     * @return a value for the given {@linkplain Element} of an XSD-Document
     */
    String getValueFor(XSDModel xsdNode, String defaultValue);

    int getMinimumNumberOfElements(XSDModel model, Element element);
}
