package isabel.tool.validators.rules;

import isabel.model.bpel.mex.ReceiveElement;
import isabel.tool.impl.ValidationCollector;
import isabel.model.ProcessContainer;

public class SA00055Validator extends Validator {

	public SA00055Validator(ProcessContainer files,
	                        ValidationCollector violationCollector) {
		super(files, violationCollector);
	}

	@Override
	public void validate() {
		for (ReceiveElement receive : fileHandler.getAllReceives()) {
			if (receive.hasFromParts() && receive.hasVariable()) {
				addViolation(receive);
			}
		}
	}

	@Override
	public int getSaNumber() {
		return 55;
	}
}