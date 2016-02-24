package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 18.02.2016
 * A constraint on the {@linkplain de.uos.se.xsd2gui.models.XSDModel#_value} attribute
 *
 * @author Falk Wilke
 */
public interface IXSDConstraint
{
    /**
     * Decides whether the given value violates the contraint modelled by this
     * {@linkplain IXSDConstraint}
     *
     * @param value
     *         the value to evaluate
     *
     * @return if the given value violates the modelled constraint
     */
    boolean isViolatedBy(String value);

    /**
     * returns a Message in human readable form what exactly was the cause that
     * {@linkplain #isViolatedBy(String)} returned for the given value.
     * If that is not the case an empty String is returned.
     *
     * @param value
     *
     * @return
     */
    String getViolationMessage(String value);
}
