package de.uniba.wiai.dsg.ss12.isabel.tool.imports;

import static de.uniba.wiai.dsg.ss12.isabel.tool.Standards.CONTEXT;
import static de.uniba.wiai.dsg.ss12.isabel.tool.validators.ValidatorNavigator.getAttributeValue;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.uniba.wiai.dsg.ss12.isabel.IsabelTool;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import de.uniba.wiai.dsg.ss12.isabel.tool.Standards;
import de.uniba.wiai.dsg.ss12.isabel.tool.ValidationException;
import de.uniba.wiai.dsg.ss12.isabel.tool.validators.ValidatorNavigator;

public class XmlFileLoader {

	private DocumentEntry bpel;
	private List<DocumentEntry> wsdlList;
	private List<DocumentEntry> xsdList;
	private List<Node> xsdSchemaList = new ArrayList<Node>();
	private String absoluteBpelFilePath;

	private final Builder builder;
	private ValidatorNavigator navigator;

	public XmlFileLoader() {
		wsdlList = new ArrayList<DocumentEntry>();
		xsdList = new ArrayList<DocumentEntry>();
		builder = new Builder(new LocationAwareNodeFactory());

		navigator = new ValidatorNavigator(new BpelProcessFiles(null, null,
				null, null, null));
	}

	public BpelProcessFiles loadAllProcessFiles(String bpelFilePath)
			throws ValidationException {
		if (bpelFilePath == null) {
			throw new ValidationException(new IllegalArgumentException(
					"parameter bpelFilePath must not be null"));
		}
		try {
			setAbsoluteBpelFilePath(bpelFilePath);
			Document bpelDom = builder.build(new File(bpelFilePath));
			String qName = navigator.getTargetNamespace(bpelDom);
			bpel = new DocumentEntry(bpelFilePath, qName, bpelDom);

            String xmlSchemaFilePath = "/XMLSchema.xsd";
            InputStream stream = IsabelTool.class.getResourceAsStream(xmlSchemaFilePath);
            if(stream == null) {
                throw new ValidationException("Could not load" + xmlSchemaFilePath);
            }
            InputStreamReader schemaFile = new InputStreamReader(stream);

			Document xmlSchemaDom = builder.build(schemaFile);
			DocumentEntry xmlSchemaEntry = new DocumentEntry(xmlSchemaFilePath,
					"http://www.w3.org/2001/XMLSchema", xmlSchemaDom);
			xsdList.add(xmlSchemaEntry);

			loadBpelImports();

		} catch (ValidityException e) {
			throw new ValidationException(e,
					"Loading failed: Not a valid BPEL-File");
		} catch (ParsingException e) {
			throw new ValidationException(e, "Loading failed: Not Parsable");
		} catch (FileNotFoundException e) {
			throw new ValidationException(e,
					"Loading failed: File was not found");
		} catch (IOException e) {
			throw new ValidationException(e,
					"Loading failed: File-Reading Error");
		}

		return new BpelProcessFiles(bpel, wsdlList, xsdList, xsdSchemaList,
				absoluteBpelFilePath);
	}

	private void setAbsoluteBpelFilePath(String bpelFilePath) {
		absoluteBpelFilePath = Paths.get(bpelFilePath).getParent()
				.toAbsolutePath().toString();
	}

	private void loadBpelImports() throws ParsingException,
			IOException {
		Nodes imports = getImportLocations(bpel.getDocument());
		loadDirectImports(imports);
	}

	private void loadDirectImports(Nodes imports) throws ParsingException,
			IOException {
		for (int i = 0; i < imports.size(); i++) {
			Node node = imports.get(i);
			DocumentEntry entry = createImportDocumentEntry(node);
			if (isWsdl(entry)) {
				if (!wsdlList.contains(entry)) {
					wsdlList.add(entry);
					loadDirectImports(getImportLocations(entry.getDocument()));
					addWsdlXsd(entry);
				}
			} else if (isXsd(entry)) {
				if (!xsdList.contains(entry)) {
					xsdList.add(entry);
					loadDirectImports(getImportLocations(entry.getDocument()));
				}
			}
		}
	}

	private void addWsdlXsd(DocumentEntry entry) throws
			ParsingException, IOException {
		Nodes typesNodes = entry.getDocument().query("//wsdl:types/*", CONTEXT);

		if (typesNodes.size() == 0) {
			return;
		}

		Node schemaNode = typesNodes.get(0);
		if (isXsdNode(schemaNode)
				&& navigator.getLocalName(schemaNode).equals("schema")) {
			xsdSchemaList.add(schemaNode);
			addXsdImports(schemaNode);
		}
	}

	private void addXsdImports(Node schemaNode) throws
			ParsingException, IOException {
		DocumentEntry xsdEntry;
		Nodes schemaChildren = schemaNode.query("child::*", CONTEXT);
		for (Node node : schemaChildren) {
			if (isXsdNode(node)
					&& navigator.getLocalName(node).equals("import")) {
				xsdEntry = createImportDocumentEntry(node);
				xsdList.add(xsdEntry);
			}
		}
	}

	private String getNodeDirectory(Node node) {
		if (node.getBaseURI().isEmpty()) {
			return null;
		}

		return Paths.get(URI.create(node.getBaseURI())).getParent().toString();
	}

	private boolean isXsdNode(Node node) {
		return ((Element) node).getNamespaceURI().equals(
				Standards.XSD_NAMESPACE);
	}

	private Nodes getImportLocations(Document entry) {
		return entry.query("/*/*[name()=\"import\"]");
	}

	private boolean isXsd(DocumentEntry entry) {
		return entry.getDocument().getRootElement().getNamespaceURI()
				.startsWith(Standards.XSD_NAMESPACE);
	}

	private boolean isWsdl(DocumentEntry entry) {
		return entry.getDocument().getRootElement().getNamespaceURI()
				.startsWith(Standards.WSDL_NAMESPACE);
	}

	private DocumentEntry createImportDocumentEntry(Node importNode)
			throws ParsingException, IOException {
		String locationPath = Paths.get(getNodeDirectory(importNode),
				getImportPath(importNode)).toString();
		File importFile = new File(locationPath);
		Document importFileDom = builder.build(importFile);
		String targetNamespace = navigator.getTargetNamespace(importFileDom
				.getDocument());
		return new DocumentEntry(importFile.getAbsolutePath(), targetNamespace,
				importFileDom);
	}

	private String getImportPath(Node node) {
		String schemaLocation = getAttributeValue(node.query("@schemaLocation"));
		String location = getAttributeValue(node.query("@location"));

		if (!"".equals(schemaLocation)) {
			return Paths.get(schemaLocation).toString();
		} else if (!"".equals(location)) {
			return Paths.get(location).toString();

		}

		return null;
	}
}