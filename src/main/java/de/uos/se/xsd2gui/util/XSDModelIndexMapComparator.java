package de.uos.se.xsd2gui.util;

import de.uos.se.xsd2gui.models.XSDModel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sem on 18.02.2016.
 * This map compares xsdmodels using their names and a map containing the indices. This is
 * intended as a workaround for <xs:sequence></xs:sequence> since there the order of the
 * contained elements is important and fixed.
 */
public class XSDModelIndexMapComparator
        implements Comparator<XSDModel>
{
    private final Map<String, Integer> indexMap;

    public XSDModelIndexMapComparator(Map<String, Integer> indexMap)
    {
        this.indexMap = new HashMap<>(indexMap);
    }

    @Override
    public int compare(XSDModel o1, XSDModel o2)
    {
        String name1 = o1.getName();
        String name2 = o2.getName();
        if (indexMap.containsKey(name1) && indexMap.containsKey(name2))
        {
            int i1 = indexMap.get(name1);
            int i2 = indexMap.get(name2);
            return Integer.compare(i1, i2);
        }
        throw new IllegalArgumentException("one compared value is not present");
    }
}
