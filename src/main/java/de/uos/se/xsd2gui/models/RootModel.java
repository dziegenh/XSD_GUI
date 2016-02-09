package de.uos.se.xsd2gui.models;

import org.w3c.dom.Element;

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
   public void parseToXML(Element root) {
      for (XSDModel xsdm : getSubModels()) {
         xsdm.parseToXML(root);
      }
   }

}
