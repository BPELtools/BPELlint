package isabel.model.wsdl;

import isabel.model.ContainerAwareReferable;
import isabel.model.NavigationException;
import isabel.model.NodeHelper;
import isabel.model.PrefixHelper;
import isabel.model.ProcessContainer;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import static isabel.model.Standards.CONTEXT;

public class PropertyAliasElement extends ContainerAwareReferable {

    private final NodeHelper propertyAlias;

	public PropertyAliasElement(Node propertyAlias, ProcessContainer processContainer) {
        super(propertyAlias, processContainer);
        this.propertyAlias = new NodeHelper(propertyAlias, "propertyAlias");
    }

    public PropertyElement getProperty()
            throws NavigationException {
        Document wsdlDom = toXOM().getDocument();
        Nodes properties = wsdlDom.query("//vprop:property", CONTEXT);

        for (Node propertyNode : properties) {
            String propertyName = new NodeHelper(propertyNode).getAttribute("name");

            if (propertyName.equals(PrefixHelper.removePrefix(getPropertyAttribute()))) {
                return new PropertyElement(propertyNode, getProcessContainer());
            }
        }
        throw new NavigationException("Referenced <property> does not exist.");
    }

    private String getPropertyAttribute() {
        return propertyAlias.getAttribute("propertyName");
    }

    public boolean hasMessageType() {
        return propertyAlias.hasAttribute("messageType");
    }

    public boolean hasPart() {
		return propertyAlias.hasAttribute("part");
	}

    public boolean hasType() {
		return propertyAlias.hasAttribute("type");
	}

    public boolean hasElement() {
		return propertyAlias.hasAttribute("element");
	}

    public boolean hasSameType(PropertyAliasElement otherPropertyAlias) {
        return propertyAlias.hasSameAttribute(otherPropertyAlias, "type");
    }

    public boolean hasSameMessageType(PropertyAliasElement otherPropertyAlias) {
        return propertyAlias.hasSameAttribute(otherPropertyAlias, "messageType");
    }

    public boolean hasSameElement(PropertyAliasElement otherPropertyAlias) {
        return propertyAlias.hasSameAttribute(otherPropertyAlias, "element");
    }

    public boolean hasSamePropertyName(PropertyAliasElement otherPropertyAlias) {
        return propertyAlias.hasSameAttribute(otherPropertyAlias, "propertyName");
    }

    public String getPropertyName() {
        return propertyAlias.getAttribute("propertyName");
    }

	public String getElementAttribute() {
		return propertyAlias.getAttribute("element");
	}

	public String getTypeAttribute() {
		return propertyAlias.getAttribute("type");
	}

	public String getMessageTypeAttribute() {
		return propertyAlias.getAttribute("messageType");
	}

	public String getPropertyNameAttribute() {
		return propertyAlias.getAttribute("propertyName");
	}
}
