package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 18.02.2016
 *
 * @author Falk Wilke
 */
public class FixedValueConstraint
        implements IXSDConstraint
{
    private final String _value;

    public FixedValueConstraint(String value)
    {
        _value = value.trim();
    }

    @Override
    public boolean isViolatedBy(String value)
    {
        return ! value.equals(this._value);
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (! isViolatedBy(value))
            return "";
        return "value '" + value + "'!='" + this._value + "'";
    }
}
