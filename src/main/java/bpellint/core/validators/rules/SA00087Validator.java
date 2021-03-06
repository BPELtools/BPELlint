package bpellint.core.validators.rules;

import api.SimpleValidationResult;
import bpellint.core.model.NavigationException;
import bpellint.core.model.PrefixHelper;
import bpellint.core.model.ProcessContainer;
import bpellint.core.model.bpel.mex.OnEventElement;
import bpellint.core.model.wsdl.MessageElement;

public class SA00087Validator extends Validator {

	private static final int MESSAGE_TYPE_IS_NOT_OPERATION_MESSAGE_NAME = 1;
	private static final int MESSAGE_HAS_TWO_OR_MORE_PARTS = 2;
	private static final int ELEMENT_IS_NOT_OPERATION_MESSAGE_PART_ELEMENT = 3;
	private static final int MESSAGE_HAS_NO_PART = 4;

	public SA00087Validator(ProcessContainer files, SimpleValidationResult validationCollector) {
		super(files, validationCollector);
	}

	@Override
	public void validate() {
		for (OnEventElement onEvent : processContainer.getAllOnEvents()) {
			try {
				checkTypeOfMessage(onEvent);
			} catch (NavigationException e) {
				// ignore if the message does not exist, this is checked elsewhere
			}
		}
	}

	private void checkTypeOfMessage(OnEventElement onEvent) throws NavigationException {
		MessageElement message = onEvent.getOperation().getInput().getMessage();
		if (onEvent.hasVariableElement()) {
			try {
				if (message.getParts().size() > 1) {
					addViolation(onEvent, MESSAGE_HAS_TWO_OR_MORE_PARTS);
					return;
				}
			} catch (NavigationException e) {
				addViolation(onEvent, MESSAGE_HAS_NO_PART);
				return;
			}
			if (!isOfSameType(onEvent.getVariableElement(), message.getSinglePart().getElement())) {
				addViolation(onEvent, ELEMENT_IS_NOT_OPERATION_MESSAGE_PART_ELEMENT);
				return;
			}
		}
		if (!onEvent.hasVariableMessageType()) {
			return;
		}
		if (!isOfSameType(onEvent.getVariableMessageType(), message.getName())) {
			addViolation(onEvent, MESSAGE_TYPE_IS_NOT_OPERATION_MESSAGE_NAME);
		}
	}

	private boolean isOfSameType(String element, String messageElementType) {
		return PrefixHelper.removePrefix(element).equals(PrefixHelper.removePrefix(messageElementType));
	}

	@Override
	public int getSaNumber() {
		return 87;
	}

}
