package de.uos.se.xsd2gui.util;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

/**
 * created: 04.02.2016
 * The {@linkplain NamespaceContext} to be used as a project default
 *
 * @author Falk Wilke
 */
public class DefaultNamespaceContext
        implements NamespaceContext
{
    public String getNamespaceURI(String prefix)
    {
        if (prefix == null)
        {
            throw new NullPointerException("Null prefix");
        } else if ("xs".equals(prefix))
        {
            return "http://www.w3.org/2001/XMLSchema";
        }
        return XMLConstants.NULL_NS_URI;
    }


    // not used
    public String getPrefix(String uri)
    {
        return null;
    }

    // not used
    public Iterator getPrefixes(String uri)
    {
        return null;
    }
}
