package de.uos.se.xsd2gui.models.constraints;


/**
 * created: 18.02.2016
 *
 * @author Falk Wilke
 */
public class NumericXSDConstraint
        implements IXSDConstraint
{
    public static final String NUMBER_REGEX = "\\d*.?\\d+";

    @Override
    public boolean isViolatedBy(String value)
    {
        return value == null || ! value.trim().matches(NUMBER_REGEX);
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (! isViolatedBy(value))
            return "";
        if (value == null)
            return "null value";
        else
            return "value '" + value + "' is not a number";
    }
}
