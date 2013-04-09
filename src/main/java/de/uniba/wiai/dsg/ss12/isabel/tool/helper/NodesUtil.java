package de.uniba.wiai.dsg.ss12.isabel.tool.helper;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

public class NodesUtil {

	public static List<Node> toList(Nodes nodes) {
		List<Node> nodeList = new ArrayList<>(nodes.size());

		for (Node node : nodes) {
			nodeList.add(node);
		}

		return nodeList;
	}

}
