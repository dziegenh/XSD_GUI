package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 18.02.2016
 * A constraint modelling a fixed value (no other values are permitted). It extends the
 * {@linkplain NoPureWhitespaceStringConstraint} since a pure whitespace string as a fixed value
 * constraint does not make any
 * sense.
 *
 * @author Falk Wilke
 */
public class FixedValueConstraint
        extends NoPureWhitespaceStringConstraint
{
    //the fixed value which other values shall be checked against
    private final String _fixedValue;

    /**
     * THe constructor.
     *
     * @param fixedValue
     *         The value which every other value shall be evaluated against. Will be trimmed
     *         beforehand by using {@linkplain String#trim()}
     *
     * @throws IllegalArgumentException
     *         If the string is a pure whitespace string ({@linkplain String#trim()} returns an
     *         empty string)
     */
    public FixedValueConstraint(String fixedValue) throws IllegalArgumentException
    {
        _fixedValue = fixedValue.trim();
        if (_fixedValue.isEmpty())
            throw new IllegalArgumentException(
                    "no pure whitespace/empty string permitted for fixed value constraint");
    }

    @Override
    public boolean isViolatedBy(String value)
    {
        return super.isViolatedBy(value) || ! value.equals(this._fixedValue);
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (! isViolatedBy(value))
            return "";
        if (super.isViolatedBy(value))
            return super.getViolationMessage(value);
        return "value '" + value + "'!='" + this._fixedValue + "'";
    }
}
