package de.uos.se.xsd2gui.models.constraints;

/**
 * created: 18.02.2016
 *
 * @author Falk Wilke
 */
public interface IXSDConstraint
{
    boolean isViolatedBy(String value);

    String getViolationMessage(String value);
}
