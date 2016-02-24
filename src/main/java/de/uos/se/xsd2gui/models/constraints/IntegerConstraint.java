package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 24.02.2016
 * A constraing modelling that only integer values are ok. Of course it extends
 * {@linkplain NumericXSDConstraint}
 *
 * @author Falk Wilke
 */
public class IntegerConstraint
        extends NumericXSDConstraint
{
    //a integer number regex
    public static final String INTEGER_REGEX = "\\d+";

    @Override
    public boolean isViolatedBy(String value)
    {
        return super.isViolatedBy(value) || ! value.matches(INTEGER_REGEX);
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (! isViolatedBy(value))
            return "";
        if (super.isViolatedBy(value))
            return super.getViolationMessage(value);
        return "value " + value + " is not an integer";
    }
}
