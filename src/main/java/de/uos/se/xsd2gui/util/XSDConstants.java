package de.uos.se.xsd2gui.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * created: 09.03.2016
 * A class holding several constants useful for dealing with XSDs.
 * It was introduced to avoid having all those fixed value strings like "xs:int" scattered across
 * the code. Also this avoids some common mistakes (like writing 'xs.int' for example instead of
 * 'xs:int').
 *
 * @author Falk Wilke
 */
public class XSDConstants
{
    /**
     * The primitive types associated with an xsd type attribute like for example xs:int.
     * Basically all implementations of {@linkplain de.uos.se.xsd2gui.xsdparser.IWidgetGenerator}
     * should at least be capable of dealing with the types contained in here if they are aware
     * of primitive types at all.
     * Contains:
     * {@linkplain #XS_INTEGER},{@linkplain #XS_INT},{@linkplain #XS_UNSIGNED_INT},{@linkplain #XS_STRING}
     */
    public static final Set<String> PRIMITIVE_TYPES;
    //the fixed attribute name
    public static final String FIXED = "fixed";
    //the type attribute name
    public static final String TYPE = "type";
    //a constant for the name of the 'simple type' elements
    public static final String SIMPLE_TYPE = "simpleType";
    //the attribute name for the "name" of the elements denoted by a certain <xs:?>
    public static final String NAME = "name";
    //the name of the sequence xsd-element
    public static final String SEQUENCE = "sequence";
    //the name of the <xs:attribute/> element
    public static final String ATTRIBUTE = "attribute";
    //the name of the <xs:element/> element
    public static final String ELEMENT = "element";
    //the name of the value attributes
    public static final String VALUE = "value";
    //the <xs:int/> type name
    public static final String XS_INT = "xs:int";
    //the <xs:string/> type name
    public static final String XS_STRING = "xs:string";
    //the <xs:unsignedInt/> type name
    public static final String XS_UNSIGNED_INT = "xs:unsignedInt";
    //the <xs:integer/> type name
    public static final String XS_INTEGER = "xs:integer";
    //the name of the <xs:minInclusive/> element
    public static final String XS_MIN_INCLUSIVE = "xs:minInclusive";
    //the name of the <xs:maxInclusive/> element
    public static final String XS_MAX_INCLUSIVE = "xs:maxInclusive";
    //the name of the <xs:minExclusive/> element
    public static final String XS_MIN_EXCLUSIVE = "xs:minExclusive";
    //the name of the <xs:maxExclusive/> element
    public static final String XS_MAX_EXCLUSIVE = "xs:maxExclusive";


    static
    {
        //setup primits
        Set<String> primits = new HashSet<>();
        primits.add(XS_INT);
        primits.add(XS_STRING);
        primits.add(XS_UNSIGNED_INT);
        primits.add(XS_INTEGER);
        PRIMITIVE_TYPES = Collections.unmodifiableSet(primits);
    }

    private XSDConstants()
    {

    }
}
