package de.uos.se.xsd2gui.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * created: 09.02.2016
 * A Class representing an XML-Model generated from an xsd. Every model can have (like in xml) submodels of various kinds
 * It provides base functionality, subclasses will want to use
 * @author Falk Wilke
 */
public abstract class XSDModel {
    //the name constant
   public static final String NAME = "name";
    //the node it corresponds to
   private final Element _xsdNode;
    //the submodels
    private final LinkedList<XSDModel> _subModels;
    //if this is required or not
   private final boolean _required;
    //the name of the element
   private String _elementName;
    //the value contained inside this element
   private StringProperty _value;
    //the last added  xsdmodels
   private List<XSDModel> _lastAdded;

    /**
     * Same as {@linkplain #XSDModel(Element, List)}, uses {@linkplain Collections#emptyList()}
     *
     * @param xsdNode
     *         the node to be responsible for
     */
    public XSDModel(Element xsdNode)
    {
        this(xsdNode, Collections.emptyList());

    }

    /**
     * The Constructor creating a new xsdmodel from scratch
     *
     * @param xsdNode
     *         the node to be responsible for
     * @param subModels
     *         the submodels to be responsible for
     */
   public XSDModel(Element xsdNode, List<? extends XSDModel> subModels) {
       //clone node to avoid sideeffects
       this._xsdNode = (Element) xsdNode.cloneNode(true);
       //get name
      this._elementName = this._xsdNode.getAttribute(NAME);
      if (this._elementName == null)
         throw new IllegalArgumentException("provided element node does not have an attribute name ");
      if (subModels == null)
         throw new NullPointerException("provided submodels are null");
       //create submodels list by copying
       this._subModels = new LinkedList<>(subModels);
       //create string property
       this._value = new SimpleStringProperty("");
       //set required
      this._required = this._xsdNode.getAttribute("use").equals("required");
       //create last added
       this._lastAdded = new LinkedList<>();
   }

    /**
     * If this model has a required attribute set which equals "required"
     * @return If this model has a required attribute set which equals "required"
     */
   public boolean isRequired() {
      return _required;
   }

   public synchronized List<XSDModel> getSubModels() {
      return Collections.unmodifiableList(this._subModels);
   }

    /**
     * parses this model to a xml element ad appends it to the given parent element
     * @param doc the document (used for creating new elements)
     * @param parent the parent element to append elements to
     */
   public abstract void parseToXML(Document doc, Element parent);

   public StringProperty valueProperty() {
      return _value;
   }

   public String getName() {
      return _elementName;
   }

   @Override
   public synchronized String toString() {
      return "XSDModel{" +
            "_xsdNode=" + _xsdNode +
            ", _subModels=" + _subModels +
            ", _required=" + _required +
            ", _elementName='" + _elementName + '\'' +
            ", _value=" + _value +
            ", _lastAdded=" + _lastAdded +
            '}';
   }

    public synchronized void addSubModel(XSDModel xsdm) {
      this._subModels.push(xsdm);
      this._lastAdded.add(xsdm);
   }

    public synchronized void removeSubModel(XSDModel xsdm) {
      this.removeSubModels(Collections.singleton(xsdm));
   }

   public synchronized void removeSubModels(Collection<XSDModel> xsdm)
   {
       this._subModels.removeAll(xsdm);
   }

    /**
     * gets the last added submodels an removes them from the internal list
     *
     * @return the last added submodels
     */
    public synchronized List<XSDModel> pollLastAddedModels() {
      List<XSDModel> tmp = new LinkedList<>(this._lastAdded);
      this._lastAdded.clear();
        return tmp;
    }

    public Element getXSDNode() {
      return (Element)_xsdNode.cloneNode(true);
   }

   public int size() {
      return this._subModels.size();
   }

}
