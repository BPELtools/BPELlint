package bpellint.tool.validators.rules;

import bpellint.model.ProcessContainer;
import bpellint.model.wsdl.PropertyAliasElement;
import bpellint.tool.validators.result.ValidationCollector;

public class SA00020Validator extends Validator {

    public SA00020Validator(ProcessContainer files,
                            ValidationCollector violationCollector) {
        super(files, violationCollector);
    }

    @Override
    public void validate() {
        for (PropertyAliasElement propertyAlias : processContainer.getAllPropertyAliases()) {
            boolean messageTypeAndPart = propertyAlias.hasMessageType()
                    && propertyAlias.hasPart()
                    && !propertyAlias.hasType()
                    && !propertyAlias.hasElement();
            boolean type = !propertyAlias.hasMessageType()
                    && !propertyAlias.hasPart()
                    && propertyAlias.hasType()
                    && !propertyAlias.hasElement();
            boolean element = !propertyAlias.hasMessageType()
                    && !propertyAlias.hasPart()
                    && !propertyAlias.hasType()
                    && propertyAlias.hasElement();
            if (!(messageTypeAndPart || type || element)) {
                addViolation(propertyAlias);
            }
        }
    }

    @Override
    public int getSaNumber() {
        return 20;
    }
}