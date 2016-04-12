package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 09.03.2016
 *
 * @author Falk Wilke
 */
public class IntegerRangeConstraint
        extends IntegerConstraint
{
    private final int _max;
    private final int _min;
    private final boolean _maxInclusive;
    private final boolean _minInclusive;

    public IntegerRangeConstraint(int min, int max, boolean minInclusive, boolean maxInclusive)
    {
        _max = max;
        _min = min;
        _maxInclusive = maxInclusive;
        _minInclusive = minInclusive;
    }

    @Override
    public boolean isViolatedBy(String value)
    {
        if (super.isViolatedBy(value))
            return true;
        try
        {
            int integerValue = Integer.parseInt(value);
            boolean minRangeCheck = _minInclusive ? _min <= integerValue : _min < integerValue;
            boolean maxRangeCheck = _maxInclusive ? integerValue <= _max : integerValue < _max;
            return ! minRangeCheck || ! maxRangeCheck;
        }
        catch (Exception ex)
        {
            //can only mean that integer range was exceeded
            return true;
        }
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (! isViolatedBy(value))
            return "";
        if (super.isViolatedBy(value))
            return super.getViolationMessage(value);
        String braceMax = _maxInclusive ? "]" : ")";
        String braceMin = _minInclusive ? "[" : "(";
        return "value '" + value + "' is not within range " + braceMin + _min + "," + _max +
               braceMax;
    }
}
