package isabel.model.bpel;

import isabel.model.ContainerAwareReferable;
import isabel.model.NodeHelper;
import isabel.model.ProcessContainer;
import isabel.model.Standards;
import nu.xom.Node;

import java.nio.file.Path;

public class ImportElement extends ContainerAwareReferable {

    private final NodeHelper importElement;

    public ImportElement(Node importNode, ProcessContainer processContainer) {
    	super(importNode, processContainer);
        this.importElement = new NodeHelper(importNode, "import");
    }

    public boolean hasNamespaceAttribute() {
        return importElement.hasAttribute("namespace");
    }

    public String getNamespace() {
        return importElement.getAttribute("namespace");
    }

    public String getImportType() {
        return importElement.getAttribute("importType");
    }

    public String getLocation() {
        return importElement.getAttribute("location");
    }

    public Path getAbsoluteLocation(Path folder) {
        return folder.resolve(getLocation()).normalize().toAbsolutePath();
    }

    public boolean isXsdImport() {
        return Standards.XSD_NAMESPACE.equals(getImportType());
    }

    public boolean isWsdlImport() {
        return Standards.WSDL_NAMESPACE.equals(getImportType());
    }

}
