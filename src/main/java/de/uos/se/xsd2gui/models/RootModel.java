package de.uos.se.xsd2gui.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * created: 09.02.2016
 *
 * @author Falk Wilke
 */
public class RootModel extends XSDModel {
   public RootModel(Element xsdNode) {
      super(xsdNode);
   }

   @Override
   public Node parseToXML(Document owner) {
      return null;
   }
}
