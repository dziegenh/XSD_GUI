package de.uos.se.xsd2gui.models.constraints;


/**
 * created: 18.02.2016
 * A constraint modelling a numeric constraing. {@linkplain Double} is used as base for checking
 * values.
 *
 * @author Falk Wilke
 */
public class NumericXSDConstraint
        extends NoPureWhitespaceStringConstraint
{
    //double number regex
    public static final String DOUBLE_REGEX = "(-?\\d+\\.?\\d*)|(-?\\d*\\.?\\d+)";

    @Override
    public boolean isViolatedBy(String value)
    {
        return super.isViolatedBy(value) || ! value.trim().matches(DOUBLE_REGEX);
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (! isViolatedBy(value))
            return "";
        if (super.isViolatedBy(value))
            return super.getViolationMessage(value);
        else
            return "value '" + value + "' is not a number";
    }
}
