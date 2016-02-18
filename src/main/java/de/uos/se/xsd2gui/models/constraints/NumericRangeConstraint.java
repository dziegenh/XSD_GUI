package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 18.02.2016
 *
 * @author Falk Wilke
 */
public class NumericRangeConstraint
        extends NumericXSDConstraint
{
    private final double up;
    private final double down;

    public NumericRangeConstraint(double up, double down)
    {
        this.up = up;
        this.down = down;
    }

    @Override
    public boolean isViolatedBy(String value)
    {
        try
        {
            return super.isViolatedBy(value) && down <= Double.parseDouble(value) &&
                   up >= Double.parseDouble(value);
        }
        catch (NumberFormatException ex)
        {
            return true;
        }
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (super.isViolatedBy(value))
            return super.getViolationMessage(value);
        try
        {
            Double.parseDouble(value);
        }
        catch (NumberFormatException ex)
        {
            return "value '" + value + "' exceeds max double";
        }
        return "";
    }
}
