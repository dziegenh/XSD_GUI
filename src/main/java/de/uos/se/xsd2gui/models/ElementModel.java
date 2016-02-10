package de.uos.se.xsd2gui.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * created: 09.02.2016
 *
 * @author Falk Wilke
 */
public class ElementModel extends XSDModel {
   public ElementModel(Element xsdNode, List<? extends XSDModel> subModels) {
      super(xsdNode, subModels);
   }

   public ElementModel(Element xsdNode) {
      super(xsdNode);
   }

   @Override

   public void parseToXML(Document doc, Element parent) {
      Element root = doc.createElement(this.getName());
      parent.appendChild(root);
      for (XSDModel xsdm : getSubModels()) {
         xsdm.parseToXML(doc, root);
      }
   }

   @Override
   protected boolean validate(String value) {
      return false;
   }

   @Override
   protected String getValueErrorMessage(String value) {
      return null;
   }
}
