/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2026 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * Contributors:
 *  Kejda Domi- Added the feature model visualization
 ********************************************************************************/
package at.variabilityanalysisgui.visualization;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import variabilityMining.Feature;

import java.util.HashMap;
import java.util.Map;

public class TreeLayout {

    private final double xSpacing;
    private final double ySpacing;

    private double nextLeafX = 0.0;
    private final Map<String, Double> xPosMap = new HashMap<>();
    private final Map<String, Integer> depthMap = new HashMap<>();

    public TreeLayout(double xSpacing, double ySpacing) {
        this.xSpacing = xSpacing;
        this.ySpacing = ySpacing;
    }

    public void apply(Graph graph, Feature root) {
        if (root == null) return;

        // 1. reset state for a fresh layout
        nextLeafX = 0.0;
        xPosMap.clear();
        depthMap.clear();

        // 2. calculate abstract coordinates (Unit scale)
        calculateX(root);
        calculateDepth(root, 0);

        // 3. transform abstract coordinates to pixels
        for (String id : xPosMap.keySet()) {
            Node n = graph.getNode(id);
            if (n == null) continue;

            // multiply the abstract 'unit' by spacing pixels
            double finalX = xPosMap.get(id) * xSpacing;
            double finalY = -depthMap.getOrDefault(id, 0) * ySpacing; // Negative for downward growth

            //set position
            n.setAttribute("xyz", finalX, finalY, 0);
        }
    }

//postorder traversal
private double calculateX(Feature feature) {
    String id = feature.getName();
    var children = feature.getChildren();

    if (children == null || children.isEmpty()) {
        double myHalfWidth = 0.5 + (6 * 0.08); // fixed 6-char width for all nodes
        nextLeafX += myHalfWidth;
        double leafX = nextLeafX;
        xPosMap.put(id, leafX);
        nextLeafX += myHalfWidth;
        return leafX;
    }

    double sum = 0.0;
    for (Feature child : children) {
        sum += calculateX(child);
    }
    double center = sum / children.size();
    xPosMap.put(id, center);
    return center;
}


//preorder traversal
    private void calculateDepth(Feature feature, int d) {
        depthMap.put(feature.getName(), d);
        if (feature.getChildren() != null) {
            for (Feature child : feature.getChildren()) {
                calculateDepth(child, d + 1);
            }
        }
    }
}