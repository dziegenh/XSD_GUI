package de.uos.se.xsd2gui.models;

import de.uos.se.xsd2gui.models.constraints.IXSDConstraint;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private final List<IXSDConstraint> _constraints;
    //the text of a violation of constraints
    private final StringProperty _violationText;
    //the boolean property holding if constraints have been violated
    private final BooleanProperty _violated;

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
        this._constraints = new LinkedList<>();
        this._violationText = new SimpleStringProperty("");
        this._value.addListener((observable, oldValue, newValue) -> checkConstraints());
        this._violated = new SimpleBooleanProperty(false);
    }

    /**
     * checks all constraints for this model and all of its submodels.
     * the {@linkplain}
     */
    private void checkConstraints()
    {
        StringBuilder builder = new StringBuilder();
        boolean violated = false;
        for (IXSDConstraint constraint : _constraints)
        {
            String value = this._value.getValue();
            if (constraint.isViolatedBy(value))
            {
                violated = true;
                builder.append(constraint.getViolationMessage(value)).append(" for field ")
                       .append(this.getName()).append(LINE_SEP);
            }
        }
        if (violated)
        {
            this._violationText.setValue(builder.toString());
            System.err.println(builder.toString());
        } else
        {
            System.out.println("OK");
            this._violationText.setValue("");
        }
        this._violated.setValue(violated);
    }

    public String getName()
    {
        return _elementName;
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
        return this._constraints.add(constr);
    }

    public boolean removeConstraint(IXSDConstraint constr)
    {
        return this._constraints.remove(constr);
    }

    public Element getXSDNode()
    {
        return (Element) _xsdNode.cloneNode(true);
    }

    public int size()
    {
        return this._subModels.size();
    }

    public final boolean checkViolationDeep()
    {
        if (this._violated.get())
            return true;
        for (XSDModel subModel : this._subModels)
        {
            if (subModel._violated.get())
                return true;
        }
        return false;
    }

    public BooleanProperty violatedProperty()
    {
        return _violated;
    }

    public StringProperty violationTextProperty()
    {
        return _violationText;
    }

    private boolean hasName()
    {
        return ! this.getName().isEmpty();
    }

}
