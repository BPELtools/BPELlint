package isabel.model;

import static isabel.model.Standards.CONTEXT;
import static isabel.model.Standards.XSD_NAMESPACE;
import isabel.model.bpel.CorrelationElement;
import isabel.model.bpel.CorrelationSetElement;
import isabel.model.bpel.CorrelationSetsElement;
import isabel.model.bpel.CorrelationsElement;
import isabel.model.bpel.EventHandlersElement;
import isabel.model.bpel.FromElement;
import isabel.model.bpel.ImportElement;
import isabel.model.bpel.LinkElement;
import isabel.model.bpel.LinkEntity;
import isabel.model.bpel.LinksElement;
import isabel.model.bpel.PartnerLinkElement;
import isabel.model.bpel.PartnerLinksElement;
import isabel.model.bpel.PickElement;
import isabel.model.bpel.ProcessElement;
import isabel.model.bpel.RethrowElement;
import isabel.model.bpel.ScopeElement;
import isabel.model.bpel.SourceElement;
import isabel.model.bpel.SourcesElement;
import isabel.model.bpel.TargetElement;
import isabel.model.bpel.TargetsElement;
import isabel.model.bpel.ToElement;
import isabel.model.bpel.VariableElement;
import isabel.model.bpel.VariablesElement;
import isabel.model.bpel.fct.CatchAllElement;
import isabel.model.bpel.fct.CatchElement;
import isabel.model.bpel.fct.CompensateElement;
import isabel.model.bpel.fct.CompensateScopeElement;
import isabel.model.bpel.fct.CompensationHandlerElement;
import isabel.model.bpel.fct.FaultHandlerContainer;
import isabel.model.bpel.fct.FctHandler;
import isabel.model.bpel.fct.TerminationHandlerElement;
import isabel.model.bpel.mex.InvokeElement;
import isabel.model.bpel.mex.MessageActivity;
import isabel.model.bpel.mex.OnEventElement;
import isabel.model.bpel.mex.OnMessageElement;
import isabel.model.bpel.mex.ReceiveElement;
import isabel.model.bpel.mex.ReplyElement;
import isabel.model.bpel.mex.StartActivity;
import isabel.model.wsdl.OperationElement;
import isabel.model.wsdl.PortTypeElement;
import isabel.model.wsdl.PropertyAliasElement;
import isabel.model.wsdl.PropertyElement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;

import org.pmw.tinylog.Logger;

public class ProcessContainer {

	private XmlFile bpel;

	private List<XmlFile> allFiles = new ArrayList<>();

	private List<XmlFile> wsdls = new ArrayList<>();
	private List<XmlFile> xsds = new ArrayList<>();

	private List<XmlFile> directlyImportedWsdls = new ArrayList<>();
	private List<XmlFile> directlyImportedXsds = new ArrayList<>();

	private List<Node> schemas = new ArrayList<>();

	private void addWsdl(XmlFile xmlFile) {
		Objects.requireNonNull(xmlFile, "a xmlFile reference is required");
		xmlFile.failUnlessWsdl();
		wsdls.add(xmlFile);
		allFiles.add(xmlFile);
	}

	private void addXsd(XmlFile xmlFile) {
		Objects.requireNonNull(xmlFile, "a xmlFile reference is required");
		xmlFile.failUnlessXsd();
		xsds.add(xmlFile);
		allFiles.add(xmlFile);
	}

	public void add(XmlFile xmlFile) {
		if (xmlFile.isWsdl()) {
			addWsdl(xmlFile);
		} else if (xmlFile.isXsd()) {
			addXsd(xmlFile);
		} else {
			throw new IllegalArgumentException(
					"Expected WSDL or XSD file, got something else for "
							+ xmlFile);
		}
	}

	public void addDirectlyImported(XmlFile xmlFile) {
		if (xmlFile.isWsdl()) {
			addWsdl(xmlFile);
			directlyImportedWsdls.add(xmlFile);
		} else if (xmlFile.isXsd()) {
			addXsd(xmlFile);
			directlyImportedXsds.add(xmlFile);
		} else {
			throw new IllegalArgumentException(
					"Expected WSDL or XSD file, got something else for "
							+ xmlFile);
		}
	}

	public void addSchema(Node schema) {
		schemas.add(Objects.requireNonNull(schema,
				"a schema reference is required"));
	}

	public void setBpel(XmlFile xmlFile) {
		Objects.requireNonNull(xmlFile, "a bpel reference is required");
		xmlFile.failUnlessBpel();
		this.bpel = xmlFile;
		allFiles.add(xmlFile);
	}

	public boolean contains(XmlFile xmlFile) {
		return allFiles.contains(xmlFile);
	}

	public Path getAbsoluteBpelFolder() {
		return this.bpel.getFilePath().getParent();
	}

	public XmlFile getBpel() {
		return bpel;
	}

	public List<XmlFile> getWsdls() {
		return wsdls;
	}

	public List<XmlFile> getXsds() {
		return xsds;
	}

	public List<XmlFile> getDirectlyImportedWsdls() {
		return directlyImportedWsdls;
	}

	public List<XmlFile> getDirectlyImportedXsds() {
		return directlyImportedXsds;
	}

	public List<Node> getSchemas() {
		List<Node> xsdSchema = new ArrayList<>(schemas);
		for (XmlFile xsdNode : getXsds())
			xsdSchema.add(xsdNode.getDocument().getChild(0));

		return xsdSchema;
	}

	public Document getXmlSchema() throws NavigationException {
		for (XmlFile xmlFile : getXsds())
			if (XSD_NAMESPACE.equals(xmlFile.getTargetNamespace()))
				return xmlFile.getDocument();

		throw new NavigationException(
				"XMLSchema should have been imported, but haven't.");
	}

	public XmlFile getWsdlByTargetNamespace(String searchedTargetNamespace)
			throws NavigationException {
		for (XmlFile wsdlEntry : getWsdls())
			if (wsdlEntry.getTargetNamespace().equals(searchedTargetNamespace))
				return wsdlEntry;

		throw new NavigationException("Document does not exist");
	}

	public Nodes getCorrelationSets() {
		return getBpel().getDocument().query("//bpel:correlationSet", CONTEXT);
	}

	public ProcessElement getProcess() {
		return new ProcessElement(getBpel().getDocument()
				.query("/bpel:*", CONTEXT).get(0));
	}

	public List<OperationElement> getAllOperations() {
		List<OperationElement> result = new LinkedList<>();

		for (XmlFile wsdlEntry : getDirectlyImportedWsdls()) {
			result.addAll(wsdlEntry.getOperations());
		}

		return result;
	}

	public List<PortTypeElement> getAllPortTypes() {
		List<PortTypeElement> result = new LinkedList<>();

		for (XmlFile wsdlEntry : getDirectlyImportedWsdls()) {
			result.addAll(wsdlEntry.getPortTypes());
		}

		return result;
	}

	public List<PropertyElement> getAllProperties() {
		List<PropertyElement> result = new LinkedList<>();

		for (XmlFile wsdlEntry : getDirectlyImportedWsdls()) {
			result.addAll(wsdlEntry.getProperties());
		}

		return result;
	}

	public List<PropertyAliasElement> getAllPropertyAliases() {
		List<PropertyAliasElement> result = new LinkedList<>();

		for (XmlFile wsdlEntry : getDirectlyImportedWsdls()) {
			result.addAll(wsdlEntry.getPropertyAliases());
		}

		return result;
	}

	public ProcessContainer validateAndFinalize() {
		Logger.info("Creating immutable ProcessContainer {0}", this);

		validate();

		wsdls = Collections.unmodifiableList(wsdls);
		xsds = Collections.unmodifiableList(xsds);
		schemas = Collections.unmodifiableList(schemas);

		return this;
	}

	public List<RethrowElement> getAllRethrows() {
		List<RethrowElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:rethrow",
				CONTEXT)) {
			result.add(new RethrowElement(node));
		}

		return result;
	}

	public List<CompensateScopeElement> getAllCompensateScopes() {
		List<CompensateScopeElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query(
				"//bpel:compensateScope", CONTEXT)) {
			result.add(new CompensateScopeElement(node));
		}

		return result;
	}

	public List<CorrelationElement> getAllCorrelations() {
		List<CorrelationElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:correlation",
				CONTEXT)) {
			result.add(new CorrelationElement(node));
		}

		return result;
	}

	public List<CorrelationsElement> getAllCorrelationContainer() {
		List<CorrelationsElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:correlations",
				CONTEXT)) {
			result.add(new CorrelationsElement(node));
		}

		return result;
	}

	public List<ScopeElement> getAllScopes() {
		List<ScopeElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:scope", CONTEXT)) {
			result.add(new ScopeElement(node));
		}

		return result;
	}

	public List<FaultHandlerContainer> getAllFaultHandlerContainers() {
		List<FaultHandlerContainer> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:faultHandlers",
				CONTEXT)) {
			result.add(new FaultHandlerContainer(node));
		}

		return result;
	}

	public List<EventHandlersElement> getAllEventHandlers() {
		List<EventHandlersElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:eventHandlers",
				CONTEXT)) {
			result.add(new EventHandlersElement(node));
		}

		return result;
	}

	public List<CorrelationsElement> getAllCorrelationsWithinInvokes() {
		List<CorrelationsElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query(
				"//bpel:invoke/bpel:correlations", CONTEXT)) {
			result.add(new CorrelationsElement(node));
		}

		return result;
	}

	public List<CompensateElement> getAllCompensates() {
		List<CompensateElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:compensate",
				CONTEXT)) {
			result.add(new CompensateElement(node));
		}

		return result;
	}

	public List<PartnerLinkElement> getAllPartnerLinks() {
		List<PartnerLinkElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:partnerLink",
				CONTEXT)) {
			result.add(new PartnerLinkElement(node));
		}

		return result;
	}

	public List<PartnerLinksElement> getAllPartnerLinksContainer() {
		List<PartnerLinksElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:partnerLinks",
				CONTEXT)) {
			result.add(new PartnerLinksElement(node));
		}

		return result;
	}

	public List<VariableElement> getAllVariables() {
		List<VariableElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:variable",
				CONTEXT)) {
			result.add(new VariableElement(node));
		}

		return result;
	}

	public List<VariablesElement> getAllVariablesContainer() {
		List<VariablesElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:variables",
				CONTEXT)) {
			result.add(new VariablesElement(node));
		}

		return result;
	}

	public List<CorrelationSetsElement> getAllCorrelationSetsContainer() {
		List<CorrelationSetsElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query(
				"//bpel:correlationSets", CONTEXT)) {
			result.add(new CorrelationSetsElement(node));
		}

		return result;
	}

	public List<CorrelationSetElement> getAllCorrelationSets() {
		List<CorrelationSetElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:correlationSet",
				CONTEXT)) {
			result.add(new CorrelationSetElement(node));
		}

		return result;
	}

	public List<InvokeElement> getAllInvokes() {
		List<InvokeElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument()
				.query("//bpel:invoke", CONTEXT)) {
			result.add(new InvokeElement(node, this));
		}

		return result;
	}

	public List<ReplyElement> getAllReplies() {
		List<ReplyElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:reply", CONTEXT)) {
			result.add(new ReplyElement(node, this));
		}

		return result;
	}

	public List<ReceiveElement> getAllReceives() {
		List<ReceiveElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:receive",
				CONTEXT)) {
			result.add(new ReceiveElement(node, this));
		}

		return result;
	}

	public List<OnMessageElement> getAllOnMessages() {
		List<OnMessageElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:onMessage",
				CONTEXT)) {
			result.add(new OnMessageElement(node, this));
		}

		return result;
	}

	public List<MessageActivity> getMessageActivities() {
		List<MessageActivity> messageActivities = new ArrayList<>();
		messageActivities.addAll(getAllInvokes());
		messageActivities.addAll(getAllReplies());
		messageActivities.addAll(getAllReceives());
		messageActivities.addAll(getAllOnMessages());
		messageActivities.addAll(getAllOnEvents());
		return messageActivities;
	}

	public List<OnEventElement> getAllOnEvents() {
		List<OnEventElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:onEvent",
				CONTEXT)) {
			result.add(new OnEventElement(node, this));
		}

		return result;
	}

	public List<ToElement> getAllTos() {
		List<ToElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:to", CONTEXT)) {
			result.add(new ToElement(node));
		}

		return result;
	}

	public List<FromElement> getAllFroms() {
		List<FromElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:from", CONTEXT)) {
			result.add(new FromElement(node));
		}

		return result;
	}

	public List<SourceElement> getAllSources() {
		List<SourceElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument()
				.query("//bpel:source", CONTEXT)) {
			result.add(new SourceElement(node));
		}

		return result;
	}

	public List<SourcesElement> getAllSourcesContainer() {
		List<SourcesElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:sources",
				CONTEXT)) {
			result.add(new SourcesElement(node));
		}

		return result;
	}

	public List<TargetElement> getAllTargets() {
		List<TargetElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument()
				.query("//bpel:target", CONTEXT)) {
			result.add(new TargetElement(node));
		}

		return result;
	}

	public List<TargetsElement> getAllTargetsContainer() {
		List<TargetsElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:targets",
				CONTEXT)) {
			result.add(new TargetsElement(node));
		}

		return result;
	}

	public List<LinkElement> getAllLinks() {
		List<LinkElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:link", CONTEXT)) {
			result.add(new LinkElement(node));
		}

		return result;
	}

	public List<LinksElement> getAllLinksContainer() {
		List<LinksElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:links", CONTEXT)) {
			result.add(new LinksElement(node));
		}

		return result;
	}

	public List<LinkEntity> getAllLinkEntities() {
		List<LinkEntity> result = new LinkedList<>();
		result.addAll(getAllSources());
		result.addAll(getAllTargets());

		return result;
	}

	public List<PickElement> getAllPicks() {
		List<PickElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:pick", CONTEXT)) {
			result.add(new PickElement(node));
		}

		return result;
	}

	public List<StartActivity> getAllStartAcivities() {
		List<StartActivity> result = new LinkedList<>();
		result.addAll(getAllReceives());
		result.addAll(getAllOnMessages());

		return result;
	}

	public List<TerminationHandlerElement> getAllTerminationHandler() {
		List<TerminationHandlerElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query(
				"//bpel:terminationHandler", CONTEXT)) {
			result.add(new TerminationHandlerElement(node));
		}

		return result;
	}

	public List<CatchElement> getAllCatch() {
		List<CatchElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:catch", CONTEXT)) {
			result.add(new CatchElement(node));
		}

		return result;
	}

	public List<CatchAllElement> getAllCatchAll() {
		List<CatchAllElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query("//bpel:catchAll",
				CONTEXT)) {
			result.add(new CatchAllElement(node));
		}

		return result;
	}

	public List<CompensationHandlerElement> getAllCompenationHandler() {
		List<CompensationHandlerElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument().query(
				"//bpel:compensationHandler", CONTEXT)) {
			result.add(new CompensationHandlerElement(node));
		}

		return result;
	}

	public List<FctHandler> getAllFctHandler() {
		List<FctHandler> result = new LinkedList<>();
		result.addAll(getAllCatch());
		result.addAll(getAllCatchAll());
		result.addAll(getAllCompenationHandler());
		result.addAll(getAllTerminationHandler());

		return result;
	}

	void validate() {
		// assertion
		if (getWsdls().isEmpty()) {
			throw new IllegalStateException(
					"At least one WSDL file is required");
		}
	}

	public String toString() {
		return new ProcessContainerPrinter(this).toString();
	}

	public List<ImportElement> getAllImports() {
		List<ImportElement> result = new LinkedList<>();

		for (Node node : getBpel().getDocument()
				.query("//bpel:import", CONTEXT)) {
			result.add(new ImportElement(node));
		}

		return result;
	}
}
