package de.uos.se.xsd2gui.model_generators;

import de.uos.se.xsd2gui.models.SequenceModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.node_generators.INodeGenerator;
import de.uos.se.xsd2gui.util.SequenceReparser;
import de.uos.se.xsd2gui.util.XPathUtil;
import de.uos.se.xsd2gui.util.XSDModelIndexMapComparator;
import de.uos.se.xsd2gui.xsdparser.AbstractWidgetFactory;
import de.uos.se.xsd2gui.xsdparser.IWidgetGenerator;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by sem on 04.02.2016.
 * Creates GUI components for attribute tags with basic XSMLSchema types (e.g.
 * "<sequence></sequence>".
 */
public class BasicSequenceParser
        implements IWidgetGenerator
{

    //the name of the element
    public static final String ELEMENT_NAME = "sequence";
    //the corresponding logger
    public static final Logger LOGGER = Logger.getLogger(BasicSequenceParser.class.getName());
    //the attribute name for the "name" of the elements contained in the parsed sequence
    private static final String NAME = "name";

    @Override
    public Optional<Node> createWidget(AbstractWidgetFactory factory, Pane parentWidget, org.w3c
            .dom.Node
            xsdNode, XSDModel parentModel)
    {
        //abortif wrong type
        if (! (xsdNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE))
        {
            return Optional.empty();
        }

        final Element elementNode = (Element) xsdNode;
        //abort if wrong name
        if (! elementNode.getLocalName().equals(ELEMENT_NAME))
        {
            return Optional.empty();
        }

        //only see elements with min and maxoccurs
        NodeList matchingTypeNodes = XPathUtil.evaluateXPath(factory.getNamespaceContext(), xsdNode,
                                                             "./xs:element[@minOccurs and " +
                                                             "@maxOccurs]");
        //the map the internal comparator shall use since sequence is ordered!
        final Map<String, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < matchingTypeNodes.getLength(); i++)
        {
            indexMap.put(((Element) matchingTypeNodes.item(i)).getAttribute(NAME), i);
        }
        //create new sequence model and add to parentmodel
        XSDModel model = new SequenceModel(elementNode, new XSDModelIndexMapComparator(indexMap));
        parentModel.addSubModel(model);

        INodeGenerator baseElementFactory = factory.getNodeGenerator();
        Pane contentNodesPane = baseElementFactory.getSimpleContainerFor(model);
        Pane nestedContent = baseElementFactory.getMultiPurposeContainer(xsdNode);
        //prepare reparsing
        SequenceReparser reparser = new SequenceReparser(matchingTypeNodes, model);
        Pane addContent = baseElementFactory.getMultiPurposeContainer(xsdNode);

        //add buttons for adding new elements
        List<? extends Node> collect = reparser.elementNames().stream().map(s -> baseElementFactory
                .getControlForHandler(ev -> reparser.add(nestedContent, s, factory), s + "+"))
                                               .collect(Collectors.toList());
        addContent.getChildren().addAll(collect);

        //add to parent widget
        contentNodesPane.getChildren().add(nestedContent);
        contentNodesPane.getChildren().add(addContent);
        //use reparser to parse to minimum occurrences
        reparser.reparseToMinimumOcc(nestedContent, factory);
        return Optional.of(contentNodesPane);
    }

}
