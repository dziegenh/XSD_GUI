package de.uos.se.xsd2gui.models.constraints;

import java.util.regex.Pattern;

/**
 * Created by sem on 25.02.2016.
 * A constraint modelling matching of values against a given {@linkplain Pattern}
 */
public class RegexConstraint
        extends NoNullValueConstraint
{
    //the pattern to match against
    private final Pattern _matchingPattern;

    /**
     * The constructor
     *
     * @param matchingPattern
     *         the pattern against which this {@linkplain IXSDValueConstraint} shall match every value
     *
     * @throws NullPointerException
     *         if the given pattern was null
     */
    public RegexConstraint(Pattern matchingPattern) throws NullPointerException
    {
        if (matchingPattern == null)
            throw new NullPointerException("given pattern was null");
        _matchingPattern = matchingPattern;
    }

    @Override
    public boolean isViolatedBy(String value)
    {
        return super.isViolatedBy(value) || _matchingPattern.matcher(value).matches();
    }

    @Override
    public String getViolationMessage(String value)
    {
        if (! isViolatedBy(value))
            return "";
        if (super.isViolatedBy(value))
            return super.getViolationMessage(value);
        return "value '" + value + "' does not match regular expression " + _matchingPattern.pattern();
    }
}
