package bpellint.model.bpel.var;

import bpellint.model.ContainerAwareReferable;
import bpellint.model.NavigationException;
import bpellint.model.NodeHelper;
import bpellint.model.ProcessContainer;
import bpellint.model.bpel.PartnerLinkElement;
import bpellint.model.wsdl.PropertyAliasElement;
import nu.xom.Node;

public class ToElement extends ContainerAwareReferable implements CopyEntity {

    private final CopyEntity delegate;

	public ToElement(Node to, ProcessContainer processContainer) {
        super(to, processContainer);
        new NodeHelper(to, "to");
        delegate = new CopyEntityImpl(to, processContainer);
    }

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean isMessageVariableAssignment() {
		return delegate.isMessageVariableAssignment();
	}

	@Override
	public boolean isPartnerLinkAssignment() {
		return delegate.isPartnerLinkAssignment();
	}

	@Override
	public boolean isVariableAssignment() {
		return delegate.isVariableAssignment();
	}

	@Override
	public boolean isQueryResultAssignment() {
		return delegate.isQueryResultAssignment();
	}

	@Override
	public boolean isLiteralAssignment() {
		return delegate.isLiteralAssignment();
	}

	@Override
	public PartnerLinkElement getPartnerLink() throws NavigationException {
		return delegate.getPartnerLink();
	}

	@Override
	public PropertyAliasElement getVariablePropertyAlias() throws NavigationException {
		return delegate.getVariablePropertyAlias();
	}

}