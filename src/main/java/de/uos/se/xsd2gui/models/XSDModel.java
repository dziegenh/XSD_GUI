package de.uos.se.xsd2gui.models;

import de.uos.se.xsd2gui.models.constraints.IXSDConstraint;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

/**
 * created: 09.02.2016
 * A Class representing an XML-Model generated from an xsd. Every model can have (like in xml)
 * submodels of various kinds
 * It provides base functionality, subclasses will want to use. The last added submodels can be
 * polled which supplements deleting of models without knowing which where created exactly
 *
 * @author Falk Wilke
 */
public abstract class XSDModel
{
    //the name constant
    public static final String NAME = "name";
    public static final String LINE_SEP = System.getProperty("line.separator");
    //the node it corresponds to
    private final Element _xsdNode;
    //the submodels
    private final LinkedList<XSDModel> _subModels;
    //if this is required or not
    private final boolean _required;
    //the comparator
    private final Comparator<XSDModel> _comparator;
    //the name of the element
    private final String _elementName;
    //the value contained inside this element
    private final StringProperty _value;
    //the last added  xsdmodels
    private final List<XSDModel> _lastAdded;
    //the constraints placed on this model
    private final List<IXSDConstraint> _contraints;

    /**
     * Same as {@linkplain #XSDModel(Element, Comparator)}, uses (x1, x2) -> 0 as comparator
     *
     * @param xsdNode
     *         the node to be responsible for
     */
    public XSDModel(Element xsdNode)
    {
        this(xsdNode, (x1, x2) -> 0);
    }

    /**
     * Same as
     * {@linkplain #XSDModel(Element, List, Comparator)}, uses {@linkplain Collections#emptyList()}
     *
     * @param xsdNode
     *         the node to be responsible for
     */
    public XSDModel(Element xsdNode, Comparator<XSDModel> comparator)
    {
        this(xsdNode, Collections.emptyList(), comparator);
    }


    /**
     * The Constructor creating a new xsdmodel from scratch
     *
     * @param xsdNode
     *         the node to be responsible for
     * @param subModels
     *         the submodels to be responsible for
     * @param comparator
     *         the comparator to use for sorting
     */
    public XSDModel(Element xsdNode, List<? extends XSDModel> subModels, Comparator<XSDModel>
            comparator)
    {
        //clone node to avoid sideeffects
        this._xsdNode = (Element) xsdNode.cloneNode(true);
        //get name
        this._elementName = this._xsdNode.getAttribute(NAME);
        if (this._elementName == null)
            throw new IllegalArgumentException(
                    "provided element node does not have an attribute name ");
        if (subModels == null)
            throw new NullPointerException("provided submodels are null");
        if (comparator == null)
            throw new NullPointerException("comparator is null");
        //create submodels list by copying
        this._subModels = new LinkedList<>(subModels);
        //create string property
        this._value = new SimpleStringProperty("");
        //set required
        this._required = this._xsdNode.getAttribute("use").equals("required");
        //create last added
        this._lastAdded = new LinkedList<>();
        this._comparator = comparator;
        this._contraints = new LinkedList<>();
    }

    /**
     * If this model has a required attribute set which equals "required"
     *
     * @return If this model has a required attribute set which equals "required"
     */
    public boolean isRequired()
    {
        return _required;
    }

    public synchronized List<XSDModel> getSubModels()
    {
        return Collections.unmodifiableList(this._subModels);
    }

    /**
     * Parses this model to a xml element and appends it to the given parent element. For a
     * xml-root-element it may be appened to the document instead
     *
     * @param doc
     *         the document (used for creating new elements)
     * @param parent
     *         the parent element to append elements to
     */
    public abstract void parseToXML(Document doc, Element parent);

    public StringProperty valueProperty()
    {
        return _value;
    }

    public String getName()
    {
        return _elementName;
    }

    @Override
    public synchronized String toString()
    {
        return "XSDModel{" +
               "_xsdNode=" + _xsdNode +
               ", _subModels=" + _subModels +
               ", _required=" + _required +
               ", _elementName='" + _elementName + '\'' +
               ", _value=" + _value +
               ", _lastAdded=" + _lastAdded +
               '}';
    }

    public synchronized void addSubModel(XSDModel xsdm)
    {
        this._subModels.add(xsdm);
        this._subModels.sort(this._comparator);
        this._lastAdded.add(xsdm);
    }

    public synchronized void removeSubModel(XSDModel xsdm)
    {
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
    public synchronized List<XSDModel> pollLastAddedModels()
    {
        List<XSDModel> tmp = new LinkedList<>(this._lastAdded);
        this._lastAdded.clear();
        return tmp;
    }

    public boolean addConstraint(IXSDConstraint constr)
    {
        return this._contraints.add(constr);
    }

    public boolean removeContraint(IXSDConstraint constr)
    {
        return this._contraints.remove(constr);
    }

    public Element getXSDNode()
    {
        return (Element) _xsdNode.cloneNode(true);
    }

    public int size()
    {
        return this._subModels.size();
    }

    /**
     * checks all constraints for this model and all of its subodels. If the optional is present
     * a constraint was violated and the error message is the string contained here
     *
     * @return an optional holding an error message (if an error is found) or an empty optional
     * if everything is all right
     */
    public final Optional<String> checkConstraints()
    {
        StringBuilder builder = new StringBuilder();
        boolean violated = false;
        for (IXSDConstraint constraint : _contraints)
        {
            String value = this._value.getValue();
            if (constraint.isViolatedBy(value))
            {
                violated = true;
                builder.append(constraint.getViolationMessage(value)).append(" for field ")
                       .append(this.getName()).append(LINE_SEP);
            }
        }
        if (! _subModels.isEmpty() && this.hasName())
            builder.append(this.getName()).append(":").append(LINE_SEP);
        for (XSDModel submodel : _subModels)
        {
            Optional<String> s = submodel.checkConstraints();
            if (s.isPresent())
            {
                builder.append(s.get());
                violated = true;
            }
        }
        if (_subModels.isEmpty())
            builder.append("---------------------").append(LINE_SEP);
        if (violated)
            return Optional.of(builder.toString());
        else
            return Optional.empty();
    }

    private boolean hasName()
    {
        return ! this.getName().isEmpty();
    }

}
