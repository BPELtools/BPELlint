package isabel.tool.validators.rules;

import isabel.model.NavigationException;
import isabel.model.NodeHelper;
import isabel.model.ProcessContainer;
import isabel.tool.impl.ValidationCollector;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;

import static isabel.model.Standards.CONTEXT;

public class SA00035Validator extends Validator {

    private static final int NO_MY_ROLE_ATTRIBUTE = 1;
    private static final int PARTNER_LINK_IS_MISSING = 2;
    private int errorType = NO_MY_ROLE_ATTRIBUTE;

    public SA00035Validator(ProcessContainer files, ValidationCollector validationCollector) {
        super(files, validationCollector);
    }

    @Override
    public void validate() {
        Nodes endpointReferenceFroms = getEndpoinReferenceFroms();
        for (Node from : endpointReferenceFroms) {
            if (!correspondingPartnerLinkHasMyRole(new NodeHelper(from))) {
                addViolation(from, errorType);
            }
        }
    }

    private Nodes getEndpoinReferenceFroms() {
        Document bpelDocument = fileHandler.getBpel().getDocument();
        return bpelDocument.query("//bpel:from[@endpointReference='myRole']", CONTEXT);
    }

    private boolean correspondingPartnerLinkHasMyRole(NodeHelper from) {
        try {
            String partnerLinkName = from.getAttribute("partnerLink");
            return from.getPartnerLink(partnerLinkName).hasMyRole();
        } catch (NavigationException e) {
            errorType = PARTNER_LINK_IS_MISSING;
            return false;
        }
    }

    @Override
    public int getSaNumber() {
        return 35;
    }

}
