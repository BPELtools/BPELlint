package isabel.model.wsdl;

import nu.xom.Node;
import isabel.model.ContainerAwareReferable;
import isabel.model.NodeHelper;
import isabel.model.ProcessContainer;

public class PartElement extends ContainerAwareReferable {

	private final NodeHelper part;
	
	public PartElement(Node part, ProcessContainer processContainer) {
		super(part, processContainer);
		this.part = new NodeHelper(part, "part");
	}
	
	public String getElement() {
		return part.getAttribute("element");
	}

}
