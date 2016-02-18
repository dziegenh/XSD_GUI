package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 18.02.2016
 *
 * @author Falk Wilke
 */
public class NoEmptyStringConstraint
        implements IXSDConstraint
{
    @Override
    public boolean isViolatedBy(String value)
    {
        return value.trim().isEmpty();
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (! isViolatedBy(value))
            return "";
        return "value '" + value + "' is only whitespace or empty";
    }
}
