package isabel.model;

import isabel.model.bpel.ProcessElement;
import isabel.model.bpel.ScopeElement;
import nu.xom.Node;

public class ContainerAwareReferable implements Referable {
	
	private final Node node;
	private final ProcessContainer processContainer;
	
	public ContainerAwareReferable(Node node, ProcessContainer processContainer) {
		super();
		this.node = node;
		this.processContainer = processContainer;
	}
	
	public ProcessContainer getProcessContainer() {
		return processContainer;
	}
	
	@Override
	public Node toXOM() {
		return node;
	}

	@Override
	public int hashCode() {
		return new ComparableNode(node).hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof ContainerAwareReferable)) {
			return false;
		}

		return new ComparableNode(node).equals(new ComparableNode((ContainerAwareReferable) object));
	}

	public ScopeElement getEnclosingScope() {
		NodeHelper parent = new NodeHelper(this);
		while(!"process".equals(parent.getLocalName())) {
			parent = parent.getParent();
			if ("scope".equals(parent.getLocalName())) {
				return new ScopeElement(parent, getProcessContainer());
			}
		}
		return new ProcessElement(parent, getProcessContainer());
	}

}