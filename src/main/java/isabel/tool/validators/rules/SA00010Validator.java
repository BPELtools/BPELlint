package isabel.tool.validators.rules;

import static isabel.model.Standards.CONTEXT;
import isabel.model.NodeHelper;
import isabel.model.PrefixHelper;
import isabel.model.ProcessContainer;
import isabel.model.XmlFile;
import isabel.tool.impl.ValidationCollector;

import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

public class SA00010Validator extends Validator {

	public SA00010Validator(ProcessContainer files,
			ValidationCollector violationCollector) {
		super(files, violationCollector);
	}

	@Override
	public void validate() {
		for (Node node : allBpelNodes()) {
			if (!typeDefinitionExists(node)) {
				addViolation(node);
			}
		}
	}

	private Nodes allBpelNodes() {
		return processContainer.getBpel().getDocument()
				.query("//bpel:*", CONTEXT);
	}

	private boolean typeDefinitionExists(Node node) {
		String elementName = NodeHelper.toElement(node).getQualifiedName();

		switch (elementName) {
		case "partnerLink":
			String partnerLinkType = getType(node, "partnerLinkType");
			return isInAnyWsdl("partnerLinkType", partnerLinkType);

		case "variable":
			String variableType = getType(node, "messageType");
			if (!"".equals(variableType)) {
				return isInAnyWsdl("message", variableType);
			}

			variableType = getType(node, "type");
			if (!"".equals(variableType)) {
				return isInAnyXsd("simpleType", variableType) || isInAnyXsd("complexType", variableType);
			}

			variableType = getType(node, "element");
			return "".equals(variableType) || isInAnyXsd("element", variableType);

		case "correlationSet":
			String correlationSetType = getType(node, "properties");
			return isInAnyWsdl("property", correlationSetType);

		case "catch":
			String catchType = getType(node, "faultMessageType");
			if (!"".equals(catchType)) {
				return isInAnyWsdl("message", catchType);
			}

			catchType = getType(node, "faultElement");
			return "".equals(catchType) || isInAnyXsd("element", catchType);

		case "reply":
		case "receive":
		case "invoke":
		case "onMessage":
		case "onEvent":
			String messageActivityType = getType(node, "portType");
			return isInAnyWsdl("portType", messageActivityType);

		case "to":
		case "from":
			String copyEntityType = getType(node, "property");
			return "".equals(copyEntityType) || isInAnyWsdl("property", copyEntityType);
		default:
			return true;
		}
	}

	private String getType(Node node, String definitionType) {
		String attributeValue = new NodeHelper(node)
				.getAttribute(definitionType);
		return PrefixHelper.removePrefix(attributeValue);
	}

	private boolean isInAnyWsdl(String definitionType, String type) {
		return inAnyFile(definitionType, type, processContainer.getWsdls());
	}

	private boolean inAnyFile(String definitionType, String type,
			List<XmlFile> files) {
		for (XmlFile domEntry : files) {
			Nodes rightNamedElements = domEntry.getDocument().query(
					"//*[@name='" + type + "']", CONTEXT);

			if (isContained(definitionType, rightNamedElements)) {
				return true;
			}
		}
		return false;
	}

	private boolean isContained(String definitionType, Nodes rightNamedElements) {
		for (Node rightElement : rightNamedElements) {
			String definitionElement = NodeHelper.toElement(rightElement)
					.getQualifiedName();
			if (definitionType.equals(PrefixHelper
					.removePrefix(definitionElement))) {
				return true;
			}
		}
		return false;
	}

	private boolean isInAnyXsd(String definitionType, String type) {
		for (Node domEntry : processContainer.getSchemas()) {
			Nodes rightNamedElements = domEntry.getDocument().query(
					"//*[@name='" + type + "']", CONTEXT);
			if (isContained(definitionType, rightNamedElements)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getSaNumber() {
		return 10;
	}

}
