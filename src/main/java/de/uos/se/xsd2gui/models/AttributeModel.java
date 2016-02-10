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
   private final String _error_string;


   public AttributeModel(Element xsdNode, Pattern validationPattern, boolean required) {
      super(xsdNode);
      this._validationPattern = validationPattern;
      this._error_string = "value does not match: " + _validationPattern.pattern();

   }

   @Override
   public void parseToXML(Document doc, Element parent) {
      if (this.isRequired())
         parent.setAttribute(this.getName(), this.getValue());
      else if (!this.getValue().trim().isEmpty())
         parent.setAttribute(this.getName(), this.getValue());

   }

   @Override
   protected boolean validate(String value) {
      if (value.isEmpty() && !this.isRequired())
         return true;
      return _validationPattern.matcher(value).matches();
   }

   @Override
   protected String getValueErrorMessage(String value) {
      return _error_string;
   }
}
