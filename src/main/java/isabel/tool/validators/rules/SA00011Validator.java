package isabel.tool.validators.rules;

import isabel.model.ProcessContainer;
import isabel.model.XmlFile;
import isabel.model.bpel.ImportElement;
import isabel.tool.impl.ValidationCollector;
import org.pmw.tinylog.Logger;

import java.nio.file.Path;
import java.util.List;

public class SA00011Validator extends Validator {

    public SA00011Validator(ProcessContainer files,
                            ValidationCollector violationCollector) {
        super(files, violationCollector);
    }

    @Override
    public void validate() {
        List<XmlFile> allWsdls = fileHandler.getWsdls();
        List<XmlFile> allXsds = fileHandler.getXsds();

        for (ImportElement importElement : fileHandler.getAllImports()) {
            boolean validFile = isValidFile(allWsdls, importElement) || isValidFile(allXsds, importElement);

            if (!validFile) {
                addViolation(importElement);
            }
        }
    }

    private boolean isValidFile(List<XmlFile> xmlFileList, ImportElement importElement) {
        Path absolutePath = importElement.getAbsoluteLocation(this.fileHandler.getAbsoluteBpelFolder());

        for (XmlFile xmlFile : xmlFileList) {
            Path filePath = xmlFile.getFilePath();
            Logger.debug("Comparing [" + filePath + "] with [" + absolutePath
                    + "]");
            if (filePath.equals(absolutePath)) {

                Logger.debug("Comparing [" + importElement.getNamespace()
                        + "] with [" + xmlFile.getTargetNamespace() + "]");
                if (importElement.getNamespace().equals(xmlFile.getTargetNamespace())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getSaNumber() {
        return 11;
    }
}