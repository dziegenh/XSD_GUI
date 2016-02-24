package de.uos.se.xsd2gui.util;

import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.xsdparser.AbstractWidgetFactory;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * created: 10.02.2016
 * A class handling reparsing of sequence elements. In its current state this class does not
 * handle sequence ordering
 *
 * @author Falk Wilke
 */
public class SequenceReparser
{
    //the name attributes name
    private static final String NAME = "name";
    //the minOccurs attributes name
    private static final String MIN_OCCURS = "minOccurs";
    //the maxOccurs attributes name
    private static final String MAX_OCCURS = "maxOccurs";
    //a map holding all elements this reparser handles mapped to their name
    private final Map<String, Element> _elements;
    //the xsdmodel
    private final XSDModel _model;
    //a map holding the current occurrences count of the handled elements
    private final Map<String, Integer> _currentOccurences;

    /**
     * The Constructor. Attempts to extract all elements from the given {@linkplain NodeList} and
     * map them to their name attribute
     * Attempts to deepclone all encountered elements.
     *
     * @param elements
     *         the elements this reparser should handle in its methods
     * @param model
     *         the xsd model to manipulate
     *
     * @throws IllegalArgumentException
     *         If there are elements present within the given
     *         {@linkplain NodeList} where {@linkplain org.w3c.dom.Node#getNodeType()}
     *         oes not return the value of
     *         {@linkplain org.w3c.dom.Node#ELEMENT_NODE} or no attribute having the same name as the value of {@linkplain #NAME} is present
     */
    public SequenceReparser(NodeList elements, XSDModel model) throws IllegalArgumentException
    {
        this._elements = new HashMap<>();
        this._currentOccurences = new HashMap<>();
        //iterate through nodelist
        for (int i = 0; i < elements.getLength(); i++)
        {
            //check nodetype and name attribute
            if (elements.item(i).getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
                throw new IllegalArgumentException(
                        "a nom-element node was found: " + elements.item(i));
            Element elem = (Element) elements.item(i).cloneNode(true);
            if (! elem.hasAttribute(NAME))
                throw new IllegalArgumentException("node does not have a name attribute: " + elem);
            if (! elem.hasAttribute(MIN_OCCURS) || ! elem.hasAttribute(MAX_OCCURS))
                Logger.getLogger(SequenceReparser.class.getName()).log(Level.WARNING,
                                                                       "element {0} does not have" +
                                                                       " both maxOccurs and " +
                                                                       "minOccurs attribute set " +
                                                                       "while being inside a " +
                                                                       "sequence, possible bug",
                                                                       elem);
            String name = elem.getAttribute(NAME);
            this._elements.put(name, elem);
            this._currentOccurences.put(name, 0);
        }
        this._model = model;
    }

    /**
     * This method will call {@linkplain #add(Pane, String, AbstractWidgetFactory)} for every
     * element
     * which is handled by this instance until there are minOccurs many elements present
     *
     * @param widget
     *         the widget to reparse for
     * @param factory
     *         the factory to use for parsing unknown elements
     */
    public synchronized void reparseToMinimumOcc(Pane widget, AbstractWidgetFactory factory)
    {
        //iterate since all elements have to be reparsed
        for (Element elem : this._elements.values())
        {
            String name = elem.getAttribute(NAME);
            int minOccurs = Math.max(getMinOcc(elem, MIN_OCCURS, 0),
                                     factory.getMinimumAmountOfElements(this._model, elem));
            int currentOccs = this._currentOccurences.get(name);
            for (int i = 0; i < minOccurs - currentOccs; i++)
            {
                this.add(widget, name, factory);
            }

        }
    }

    /**
     * This method simply gets the amount of occurs stored at the given attribute. If it is not
     * present (or has an illegal value) the provided default value is returned
     *
     * @param elem
     *         the elem to evaluate for
     * @param attName
     *         the name of the attribute to evaluate
     * @param defaultValue
     *         the default value
     *
     * @return The amount of occurs stored at the given attribute. If it is not present (or has
     * an illegal value) the provided default value is returned
     */
    private synchronized int getMinOcc(Element elem, String attName, int defaultValue)
    {
        String minOccursString = elem.getAttribute(attName);
        return minOccursString.matches("\\d+") ? Integer.parseInt(minOccursString) : defaultValue;
    }

    /**
     * This method adds another instance (view and model) of the named element to the provided
     * widget and the internal model
     * In fact the provided factory will be asked to parse the named element anew
     *
     * @param widget
     *         the widget to parse for
     * @param name
     *         the name of the element to add
     * @param factory
     *         the factory zto use for parsing elements
     */
    public synchronized void add(Pane widget, String name, AbstractWidgetFactory factory)
    {
        //cannot parse unknown elements
        if (! _elements.containsKey(name))
            return;
        Element elem = this._elements.get(name);
        int maxOccurs = getMinOcc(elem, MAX_OCCURS, Integer.MAX_VALUE);
        int currentOcc = this._currentOccurences.get(name);
        //check occurrences do not exceed limit
        if (currentOcc < maxOccurs)
        {
            /**
             * Does essentially the same
             * {@linkplain de.uos.se.xsd2gui.generators.BasicSequenceParser#createWidget(AbstractWidgetFactory, Pane, org.w3c.dom.Node, XSDModel)}
             * should do.
             */
            Pane elementPane = new HBox(20);
            //parse elements
            factory.parseXsdNode(elementPane, elem, this._model);
            //poll lkast added models from stored model
            List<XSDModel> models = this._model.pollLastAddedModels();
            //create delete button and add listener
            Button delete = new Button("-");
            delete.setOnAction(ev -> delete(widget, elementPane, name, models));
            elementPane.getChildren().add(delete);
            widget.getChildren().add(elementPane);
            this._currentOccurences.put(name, this._currentOccurences.get(name) + 1);
        }
    }

    /**
     * This method deletes the provided
     * {@linkplain XSDModel}s from the internal model and the provided {@linkplain Node} from the given {@linkplain Pane}.
     * Essentially this method is to be used by listeners which do not want to hold a reference
     * to the node.
     * This is simply for convenience and could easily be changed if splitting of this
     * functionality is desired.
     *
     * @param widget
     *         the widet to delete from
     * @param element
     *         the element to delete from the {@linkplain Pane}
     * @param name
     *         the name of the element to delete
     * @param models
     *         the models to delete from the internal model
     */
    public synchronized void delete(Pane widget, Node element, String name, List<XSDModel> models)
    {
        //cannot delete unknown elements
        if (! _currentOccurences.containsKey(name))
            return;
        int currentOcc = this._currentOccurences.get(name);
        Element item = this._elements.get(name);
        int minOccurs = getMinOcc(item, MIN_OCCURS, 0);
        //check occurrences do not underflow
        if (currentOcc > minOccurs)
        {
            widget.getChildren().remove(element);
            this._currentOccurences.put(name, this._currentOccurences.get(name) - 1);
            this._model.removeSubModels(models);
        }
    }

    public synchronized Set<String> elementNames()
    {
        return Collections.unmodifiableSet(this._elements.keySet());
    }

}
