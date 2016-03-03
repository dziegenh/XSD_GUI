package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 24.02.2016
 * A constraint modelling a constraint that a value must not be null
 *
 * @author Falk Wilke
 */
public class NoNullValueConstraint
        implements IXSDValueConstraint
{
    @Override
    public boolean isViolatedBy(String value)
    {
        return value == null;
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (! isViolatedBy(value))
            return "";
        return "value is null";
    }
}
