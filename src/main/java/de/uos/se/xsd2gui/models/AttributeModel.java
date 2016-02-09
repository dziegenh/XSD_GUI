package de.uos.se.xsd2gui.models;

import org.w3c.dom.Element;

/**
 * created: 09.02.2016
 *
 * @author Falk Wilke
 */
public class AttributeModel extends XSDModel {
   public AttributeModel(Element xsdNode) {
      super(xsdNode);
   }

   @Override
   public void parseToXML(Element parent) {
      parent.setAttribute(this.getName(), this.getValue());
   }
}
