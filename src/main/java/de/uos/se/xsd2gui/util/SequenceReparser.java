package de.uos.se.xsd2gui.util;

import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.xsdparser.WidgetFactory;
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
 *
 * @author Falk Wilke
 */
public class SequenceReparser {
   private static final String NAME = "name";
   private static final String MIN_OCCURS = "minOccurs";
   private static final String MAX_OCCURS = "maxOccurs";
   private final Map<String, Element> _elements;
   private final XSDModel _model;
   private final Map<String, Integer> _currentOccurences;

   public SequenceReparser(NodeList elements, XSDModel model) {
      this._elements = new HashMap<>();
      this._currentOccurences = new HashMap<>();
      for (int i = 0; i < elements.getLength(); i++) {
         if (elements.item(i).getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
            throw new IllegalArgumentException("a nom-element node was found: " + elements.item(i));
         Element elem = (Element) elements.item(i);
         String name = elem.getAttribute(NAME);
         if (name == null)
            throw new IllegalArgumentException("node does not have a name attribute: " + elem);
         if (!elem.hasAttribute(MIN_OCCURS) || !elem.hasAttribute(MAX_OCCURS))
              Logger.getLogger(SequenceReparser.class.getName()).log(Level.WARNING, "element {0} does not have both maxOccurs and minOccurs attribute set while being inside a sequence, possible bug", elem);
//            Logger.getLogger(this.getClass().log(Level.WARNING, "element {0} does not have both maxOccurs and minOccurs attribute set while being inside a sequence, possible bug", elem);
         this._elements.put(name, elem);
         this._currentOccurences.put(name, 0);
      }
      this._model = model;
   }

   public synchronized void add(Pane widget, String name, WidgetFactory factory) {
      if (!_elements.containsKey(name))
         return;
      Element elem = this._elements.get(name);
      int maxOccurs = getOccurs(elem, MAX_OCCURS, Integer.MAX_VALUE);
      int currentOcc = this._currentOccurences.get(name);
      if (currentOcc < maxOccurs) {
         Pane elementPane = new HBox(20);
         factory.parseXsdNode(elementPane, elem, this._model);
         List<XSDModel> models = this._model.getLastAddedModels();
         System.out.println(models);
         Button delete = new Button("-");
         delete.setOnAction(ev -> delete(widget, elementPane, name, models));
         elementPane.getChildren().add(delete);
         widget.getChildren().add(elementPane);
         this._currentOccurences.put(name, this._currentOccurences.get(name) + 1);
      }
   }

   public synchronized void reparseToMinimumOcc(Pane widget, WidgetFactory factory) {
      for (Element elem : this._elements.values()) {
         String name = elem.getAttribute(NAME);
         int minOccurs = getOccurs(elem, MIN_OCCURS, 0);
         int currentOccs = this._currentOccurences.get(name);
         for (int i = 0; i < minOccurs - currentOccs; i++) {
            this.add(widget, name, factory);
         }

      }
   }

   private synchronized int getOccurs(Element elem, String attName, int defaultValue) {
      String minOccursString = elem.getAttribute(attName);
      return minOccursString.matches("\\d+") ? Integer.parseInt(minOccursString) : defaultValue;
   }

   public synchronized void delete(Pane widget, Node element, String name, List<XSDModel> models) {
      if (!_currentOccurences.containsKey(name))
         return;
      int currentOcc = this._currentOccurences.get(name);
      Element item = this._elements.get(name);
      int minOccurs = getOccurs(item, MIN_OCCURS, 0);
      if (currentOcc > minOccurs) {
         widget.getChildren().remove(element);
         this._currentOccurences.put(name, this._currentOccurences.get(name) - 1);
         this._model.removeSubModels(models);
      }
   }

   public synchronized Set<String> elementNames() {
      return Collections.unmodifiableSet(this._elements.keySet());
   }

}
