package isabel.tool.validators.rules;

import isabel.model.ProcessContainer;
import isabel.model.bpel.mex.OnEventElement;
import isabel.tool.impl.ValidationCollector;

public class SA00090Validator extends Validator {

	public SA00090Validator(ProcessContainer files,
			ValidationCollector validationCollector) {
		super(files, validationCollector);
	}

	@Override
	public void validate() {
		for (OnEventElement onEvent : fileHandler.getAllOnEvents()) {
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
