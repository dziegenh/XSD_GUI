package de.uos.se.xsd2gui.model_generators;

import de.uos.se.xsd2gui.models.AttributeModel;
import de.uos.se.xsd2gui.models.XSDModel;
import de.uos.se.xsd2gui.models.constraints.FixedValueConstraint;
import de.uos.se.xsd2gui.models.constraints.IntegerConstraint;
import de.uos.se.xsd2gui.models.constraints.NoPureWhitespaceStringConstraint;
import de.uos.se.xsd2gui.models.constraints.UIntConstraint;
import de.uos.se.xsd2gui.node_generators.INodeGenerator;
import de.uos.se.xsd2gui.util.XSDConstants;
import de.uos.se.xsd2gui.xsdparser.AbstractWidgetFactory;
import de.uos.se.xsd2gui.xsdparser.IWidgetGenerator;
import javafx.scene.layout.Pane;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates GUI components for attribute tags with basic XSMLSchema types (e.g.
 * "<attribute type='xs:string' />".
 *
 * @author dziegenhagen
 */
public class BasicAttributeParser
        implements IWidgetGenerator
{

    @Override
    public Optional<javafx.scene.Node> createWidget(AbstractWidgetFactory factory, Pane
            parentWidget, Node xsdNode, XSDModel parentModel)
    {

        //check for correct types and attributes
        if (! (xsdNode.getNodeType() == Node.ELEMENT_NODE))
        {
            return Optional.empty();
        }

        final Element elementNode = (Element) xsdNode;
        String localName = elementNode.getLocalName();
        if (! localName.equals(XSDConstants.ATTRIBUTE))
        {
            return Optional.empty();
        }

        final String type = elementNode.getAttribute(XSDConstants.TYPE);

        if (null == type || type.isEmpty() || ! XSDConstants.PRIMITIVE_TYPES.contains(type))
        {
            return Optional.empty();
        }
        XSDModel model = new AttributeModel(elementNode);
        //important that the submodel is added before asking the factoryx for values since the
        // location of the model is used for generating "paths"
        parentModel.addSubModel(model);

        /**
         * divide gui node creation and constraint adding. According to the value of the
         * {@linkplain XSDConstants#TYPE} attribute various constrains are added.
         */
        switch (type)
        {
            case "xs:unsignedInt":
                //add uintconstraint
                model.addConstraint(new UIntConstraint());

            case "xs:int":
                //add integerconstraint
                model.addConstraint(new IntegerConstraint());
                break;

            case "xs:string":
                break;
        }
        //bind controls
        INodeGenerator baseElementFactory = factory.getNodeGenerator();
        javafx.scene.Node binded = baseElementFactory
                .getAndBindControl(factory.getValueGenerator(), model, type);
        String fixed = elementNode.getAttribute(XSDConstants.FIXED);
        //do not allow whitespace on required attributes
        if (model.isRequired())
            model.addConstraint(new NoPureWhitespaceStringConstraint());
        //only allow fixed values on attribute which have a {@linkplain XSDConstants#FIXED} value
        if (model.isFixed())
            model.addConstraint(new FixedValueConstraint(fixed));

        if (binded == null)
        {
            Logger.getLogger(this.getClass().getName())
                  .log(Level.WARNING, "no input widget created for {0}", model);
            parentModel.removeSubmodel(model);
            return Optional.empty();
        }

        return Optional.of(binded);

    }
}
