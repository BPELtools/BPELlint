package bpellint.core.validators.rules;

import api.SimpleValidationResult;
import bpellint.core.model.ProcessContainer;
import bpellint.core.model.bpel.mex.OnEventElement;

public class SA00090Validator extends Validator {

	public SA00090Validator(ProcessContainer files,
			SimpleValidationResult validationCollector) {
		super(files, validationCollector);
	}

	@Override
	public void validate() {
		for (OnEventElement onEvent : processContainer.getAllOnEvents()) {
			if (!onEvent.hasVariableAttribute()) {
				continue;
			}
			if (onEvent.hasVariableElement() && onEvent.hasVariableMessageType()) {
				addViolation(onEvent);
			}
			if (!onEvent.hasVariableElement() && !onEvent.hasVariableMessageType()) {
				addViolation(onEvent);
			}
		}

	}

	@Override
	public int getSaNumber() {
		return 90;
	}

}
