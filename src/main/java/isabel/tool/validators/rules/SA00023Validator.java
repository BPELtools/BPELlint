package isabel.tool.validators.rules;

import isabel.model.ProcessContainer;
import isabel.model.bpel.VariableElement;
import isabel.model.bpel.VariablesElement;
import isabel.tool.impl.ValidationCollector;

import java.util.HashSet;
import java.util.Set;

public class SA00023Validator extends Validator {

    public SA00023Validator(ProcessContainer files,
                            ValidationCollector violationCollector) {
        super(files, violationCollector);
    }

    @Override
    public void validate() {
        for (VariablesElement variablesContainer : fileHandler.getAllVariablesContainer()) {
            Set<String> names = new HashSet<>();
            for (VariableElement variable : variablesContainer.getVariables()) {
                String name = variable.getVariableName();
                if (names.contains(name)) {
                    addViolation(variable);
                } else {
                    names.add(name);
                }
            }
        }
    }

    @Override
    public int getSaNumber() {
        return 23;
    }

}
