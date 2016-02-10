package de.uos.se.xsd2gui.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * created: 09.02.2016
 *
 * @author Falk Wilke
 */
public class RootModel extends XSDModel {
   private final String _schemaLocation;

   public RootModel(Element xsdNode, String schemaLocation) {
      super(xsdNode);
      this._schemaLocation = schemaLocation;
   }

   @Override
   public void parseToXML(Document doc, Element parent) {
      Element root = doc.createElement(this.getName());
      root.setAttribute("xsi:noNamespaceSchemaLocation",this._schemaLocation);
      root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
      doc.appendChild(root);
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
