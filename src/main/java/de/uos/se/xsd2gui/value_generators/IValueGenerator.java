package de.uos.se.xsd2gui.value_generators;

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

    /**
     * This method tells the caller how many elements of the specified type have to be present at
     * least.
     * This is used for elements which correspond to a <xs:sequence></xs:sequence>. Obviously the
     * amount of elements is only of important if the origin of the data returned by
     * {@linkplain #getValueFor(XSDModel, String)} is external. The {@linkplain XSDModel} is not
     * used directly for retrieving a count. It was decided to retrieve that information
     * <b>before</b> adding a model to is parent. Obviously it is not possible to evaluate the
     * exact count from a {@linkplain XSDModel} which is not properly in place within a hierarchy
     * of models.
     *
     * @param model
     *         the {@linkplain XSDModel} to evaluate the minimum amount of contained elements
     *         specified by the second parameter
     * @param element
     *         the
     *         {@linkplain Element} which whose minimum amount inside the given {@linkplain XSDModel} shall be retrieved.
     *
     * @return the minimum amount of the given
     * {@linkplain Element} which should be present inside the given {@linkplain XSDModel}
     */
    int getMinimumNumberOfElements(XSDModel model, Element element);
}
