package de.uos.se.xsd2gui.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.regex.Pattern;

/**
 * created: 09.02.2016
 *
 * @author Falk Wilke
 */
public class AttributeModel extends XSDModel {
   private final Pattern _validationPattern;
   private final String error_string;

   public AttributeModel(Element xsdNode, Pattern validationPattern) {
      super(xsdNode);
      this._validationPattern = validationPattern;
      error_string = "value does not match: " + _validationPattern.pattern();
   }

   @Override
   public void parseToXML(Document doc, Element parent) {
      parent.setAttribute(this.getName(), this.getValue());

   }

   @Override
   protected boolean validate(String value) {
      return _validationPattern.matcher(value).matches();
   }

   @Override
   protected String getValueErrorMessage(String value) {
      return error_string;
   }
}
