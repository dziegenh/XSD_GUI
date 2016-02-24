package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 18.02.2016
 * A class representing a numeric range constraint
 * @author Falk Wilke
 */
public class NumericRangeConstraint
        extends NumericXSDConstraint
{
    //the upper bound
    private final double up;
    //the lower bound
    private final double down;

    /**
     * The constructor
     *
     * @param up
     *         the upper bound to check
     * @param down
     *         the lower bound to check
     */
    public NumericRangeConstraint(double up, double down)
    {
        this.up = up;
        this.down = down;
    }

    @Override
    public boolean isViolatedBy(String value)
    {
        if (super.isViolatedBy(value))
            return true;
        try
        {
            double parsed = Double.parseDouble(value);
            return down <= parsed &&
                   up >= parsed;
        }
        catch (NumberFormatException ex)
        {
            //exceeds max double!
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
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(value);
        }
        catch (NumberFormatException ex)
        {
            return "value '" + value + "' exceeds max double";
        }
        return "";
    }
}
