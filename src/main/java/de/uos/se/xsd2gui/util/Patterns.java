package de.uos.se.xsd2gui.util;

import java.util.regex.Pattern;

/**
 * created: 10.02.2016
 *
 * @author Falk Wilke
 */
public interface Patterns {
   Pattern XS_INT_PATTERN_REQUIRED = Pattern.compile("\\d+");
   Pattern XS_INT_PATTERN_NOT_REQUIRED = Pattern.compile("\\d*");
   Pattern XS_STRING_PATTERN_REQUIRED = Pattern.compile(".+");
   Pattern XS_STRING_PATTERN_NOT_REQUIRED = Pattern.compile(".+");
}
