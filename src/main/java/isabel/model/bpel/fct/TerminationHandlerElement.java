package isabel.model.bpel.fct;

import isabel.model.ContainerAwareReferable;
import isabel.model.NodeHelper;
import isabel.model.ProcessContainer;
import isabel.model.bpel.ScopeElement;

import java.util.List;

import nu.xom.Node;

public class TerminationHandlerElement extends ContainerAwareReferable implements FctHandler {

	private FctHandlerImpl fctHandlerImpl;

	public TerminationHandlerElement(Node terminationHandler, ProcessContainer processContainer) {
		super(terminationHandler, processContainer);
		new NodeHelper(terminationHandler, "terminationHandler");
		fctHandlerImpl = new FctHandlerImpl(toXOM(), getProcessContainer());
	}

	@Override
	public List<ScopeElement> getRootScopes() {
		return fctHandlerImpl.getRootScopes();
	}

}
