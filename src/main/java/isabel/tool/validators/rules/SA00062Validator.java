package isabel.tool.validators.rules;

import isabel.model.Standards;
import isabel.tool.impl.ValidationCollector;
import isabel.model.ProcessContainer;
import nu.xom.Node;
import nu.xom.Nodes;

public class SA00062Validator extends Validator {

	public SA00062Validator(ProcessContainer files,
	                        ValidationCollector validationCollector) {
		super(files, validationCollector);
	}

	@Override
	public void validate() {
		Nodes picksWithCreateInstanceYes = processContainer.getBpel().getDocument()
				.query("//bpel:pick[@createInstance='yes']", Standards.CONTEXT);
		for (Node pick : picksWithCreateInstanceYes) {
			Nodes onAlarms = pick.query("bpel:onAlarm", Standards.CONTEXT);
			for (Node onAlarm : onAlarms) {
				addViolation(onAlarm);
			}
		}
	}

	@Override
	public int getSaNumber() {
		return 62;
	}
}
