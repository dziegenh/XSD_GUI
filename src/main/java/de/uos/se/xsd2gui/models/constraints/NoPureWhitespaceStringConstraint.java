package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 18.02.2016
 * A constraint not permitting any pure whitespace values ({@linkplain String#trim()} returns an
 * empty string)
 *
 * @author Falk Wilke
 */
public class NoPureWhitespaceStringConstraint
        extends NoNullValueConstraint
{
    @Override
    public boolean isViolatedBy(String value)
    {
        return super.isViolatedBy(value) || value.trim().isEmpty();
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (! isViolatedBy(value))
            return "";
        if (super.isViolatedBy(value))
            return super.getViolationMessage(value);
        return "value '" + value + "' is only whitespace or empty";
    }
}
