package isabel.model.bpel.mex;

import isabel.model.*;
import isabel.model.bpel.PartnerLinkElement;
import isabel.model.wsdl.OperationElement;
import isabel.model.wsdl.PortTypeElement;
import nu.xom.Node;
import nu.xom.Nodes;

import static isabel.model.Standards.CONTEXT;

public class MessageActivityImpl implements MessageActivity {

    private final NodeHelper nodeHelper;
    private final ProcessContainer processContainer;

    public MessageActivityImpl(NodeHelper nodeHelper, ProcessContainer processContainer) {
        this.nodeHelper = nodeHelper;
        this.processContainer = processContainer;
    }

    @Override
    public Type getType() {
        if ("receive".equals(nodeHelper.getLocalName())) {
            return Type.RECEIVE;
        } else if ("reply".equals(nodeHelper.getLocalName())) {
            return Type.REPLY;
        } else if ("invoke".equals(nodeHelper.getLocalName())) {
            return Type.INVOKE;
        } else if ("onMessage".equals(nodeHelper.getLocalName())) {
            return Type.ON_MESSAGE;
        } else if ("onEvent".equals(nodeHelper.getLocalName())) {
            return Type.ON_EVENT;
        }

        throw new IllegalStateException("Given node " + nodeHelper.getLocalName() + " is not a message activity");
    }

    @Override
    public PartnerLinkElement getPartnerLink() throws NavigationException {
        return nodeHelper.getPartnerLink(getPartnerLinkAttribute());
    }

    @Override
    public PortTypeElement getPortType() throws NavigationException {
        return getPartnerLink().partnerLinkToPortType(processContainer, this);
    }

    @Override
    public OperationElement getOperation() throws NavigationException {
        return getPortType().getOperationByName(getOperationAttribute());
    }

    @Override
    public String getPartnerLinkAttribute() {
        return nodeHelper.getAttribute("partnerLink");
    }

    @Override
    public String getOperationAttribute() {
        return nodeHelper.getAttribute("operation");
    }

    @Override
    public String getPortTypeAttribute() {
        return nodeHelper.getAttribute("portType");
    }

    @Override
    public boolean isReceiving() {
        return Type.ON_EVENT.equals(getType()) || Type.RECEIVE.equals(getType()) || Type.ON_MESSAGE.equals(getType());
    }

    @Override
    public Node toXOM() {
        return nodeHelper.asElement();
    }

    public PortTypeElement getPortType(String portTypeQName, String portTypeNamespaceURI)
            throws NavigationException {
        String portTypeName = PrefixHelper.removePrefix(portTypeQName);
        for (XmlFile wsdlEntry : processContainer.getWsdls()) {
            String targetNamespace = wsdlEntry.getTargetNamespace();
            if (targetNamespace.equals(portTypeNamespaceURI)) {
                Nodes portTypes = wsdlEntry.getDocument().query(
                        "//wsdl:portType[@name='" + portTypeName + "']",
                        CONTEXT);
                if (portTypes.hasAny()) {
                    return new PortTypeElement(portTypes.get(0));
                }
            }
        }

        throw new NavigationException("portType not defined");
    }

}