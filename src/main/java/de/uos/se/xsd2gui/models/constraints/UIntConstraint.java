package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 29.02.2016
 *
 * @author Falk Wilke
 */
public class UIntConstraint
        extends IntegerConstraint
{
    @Override
    public boolean isViolatedBy(String value)
    {
        return super.isViolatedBy(value) || value.contains("-");
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (! isViolatedBy(value))
            return "";
        if (super.isViolatedBy(value))
            return super.getViolationMessage(value);
        return "value '" + value + "' is not an unsigned integer";
    }
}
