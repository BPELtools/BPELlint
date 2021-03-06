package bpellint.core.validators.rules;

import api.SimpleValidationResult;
import bpellint.core.model.ProcessContainer;
import bpellint.core.model.bpel.var.VariableElement;

public class SA00025Validator extends Validator {

    private static final int MESSAGE_TYPE_AND_TYPE_AND_ELEMENT = 5;
    private static final int TYPE_AND_ELEMENT = 4;
    private static final int MESSAGE_TYPE_AND_ELEMENT = 3;
    private static final int MESSAGE_TYPE_AND_TYPE = 2;
    private static final int MESSAGE_TYPE_OR_TYPE_OR_ELEMENT_MISSING = 1;

    public SA00025Validator(ProcessContainer files, SimpleValidationResult violationCollector) {
        super(files, violationCollector);
    }

    @Override
    public void validate() {


        for (VariableElement variable : processContainer.getAllVariables()) {

            if (!variable.hasVariableMessageType() && !variable.hasType() && !variable.hasVariableElement()) {
                addViolation(variable, MESSAGE_TYPE_OR_TYPE_OR_ELEMENT_MISSING);
            } else if (variable.hasVariableMessageType() && variable.hasType() && variable.hasVariableElement()) {
                addViolation(variable, MESSAGE_TYPE_AND_TYPE_AND_ELEMENT);
            } else if (variable.hasVariableMessageType() && variable.hasType()) {
                addViolation(variable, MESSAGE_TYPE_AND_TYPE);
            } else if (variable.hasVariableMessageType() && variable.hasVariableElement()) {
                addViolation(variable, MESSAGE_TYPE_AND_ELEMENT);
            } else if (variable.hasType() && variable.hasVariableElement()) {
                addViolation(variable, TYPE_AND_ELEMENT);
            }
        }
    }

    @Override
    public int getSaNumber() {
        return 25;
    }
}
