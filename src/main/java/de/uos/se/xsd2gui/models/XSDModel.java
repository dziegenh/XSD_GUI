package de.uos.se.xsd2gui.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * created: 09.02.2016
 *
 * @author Falk Wilke
 */
public abstract class XSDModel {
   public static final String NAME = "name";
   private final Element _xsdNode;
   private final List<XSDModel> _subModels;
   private final String _name;
   private String _value;

   public XSDModel(Element xsdNode, List<? extends XSDModel> subModels) {
      this._xsdNode = (Element) xsdNode.cloneNode(true);
      this._name = this._xsdNode.getAttribute(NAME);
      if (this._name == null)
         throw new IllegalArgumentException("provided element node does not have an attribute name");
      if (subModels == null)
         throw new NullPointerException("provided submodels are null");
      this._subModels = new LinkedList<>(subModels);
      this._value = "";
   }

   public XSDModel(Element xsdNode) {
      this(xsdNode, Collections.emptyList());

   }

   public List<XSDModel> getSubModels() {
      return Collections.unmodifiableList(this._subModels);
   }

   public abstract Node parseToXML(Document owner);

   public String getValue() {
      return this._value;
   }

   //   public Node parseToXML()
//   {
//      DocumentBuilderFactory factory =
//            DocumentBuilderFactory.newInstance();
//      try {
//         DocumentBuilder builder = factory.newDocumentBuilder();
//         return this.parseToXML(builder.newDocument());
//      } catch (ParserConfigurationException e) {
//         e.printStackTrace();
//      }
//      return null;
//   }
   public void setValue(String value) {
      this._value = value;
   }

   public String getName() {
      return _name;
   }

   public void addSubModel(XSDModel xsdm) {
      this._subModels.add(xsdm);
   }

   public void removeSubModel(XSDModel xsdm) {
      this._subModels.remove(xsdm);
   }

   public Element getXSDNode() {
      return _xsdNode;
   }
}
