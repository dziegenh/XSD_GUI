package de.uos.se.xsd2gui.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

   public Node parseToXML(Document owner) {
      Element root = owner.createElement(this.getName());
      for (XSDModel xsdm : getSubModels()) {
         root.appendChild(xsdm.parseToXML(owner));
      }
      return root;
   }
}
