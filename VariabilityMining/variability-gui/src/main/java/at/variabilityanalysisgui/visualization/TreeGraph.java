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

import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import variabilityMining.Feature;

import java.util.List;

public class TreeGraph implements ViewerListener {

    private static final int MAX_LABEL_LEN = 9;

    private Feature root;
    private Graph graph;
    private SpriteManager sman;
    private FxDefaultView view;
    private Tooltip hoverTooltip;

    public TreeGraph(Feature root) {
        this.root = root;
    }

    public View getViewer() {
        setupGraph();
        buildModelRecursive(root);
        applyLayout();
        decorateGroupsRecursive(root);

        FxViewer viewer = new FxViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        view = (FxDefaultView) viewer.addView("view", new FxGraphRenderer());

        viewer.disableAutoLayout();

        ViewerPipe pipe = viewer.newViewerPipe();
        pipe.addViewerListener(this);
        pipe.addSink(graph);

        Thread pumpThread = new Thread(() -> {
            while (true) {
                try {
                    pipe.pump();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        pumpThread.setDaemon(true);
        pumpThread.start();

        Platform.runLater(() -> {
            hoverTooltip = new Tooltip();
            hoverTooltip.setAutoHide(false);
            hoverTooltip.setStyle(
                    "-fx-background-color: #ffffe0;" +
                            "-fx-text-fill: black;" +
                            "-fx-font-size: 12px;" +
                            "-fx-border-color: #999;" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 4px 8px;"
            );

            view.setOnMouseMoved(event -> {
                org.graphstream.ui.geom.Point3 screenPos;
                String hoveredId = null;
                double threshold = 20.0;

                for (Node n : graph) {
                    if (!n.hasAttribute("xyz")) continue;
                    Object[] xyz = (Object[]) n.getAttribute("xyz");
                    double nx = toDouble(xyz[0]);
                    double ny = toDouble(xyz[1]);

                    // convert node graph units to screen pixels
                    org.graphstream.ui.geom.Point3 nodePx =
                            view.getCamera().transformGuToPx(nx, ny, 0);

                    double dx = nodePx.x - event.getX();
                    double dy = nodePx.y - event.getY();

                    if (Math.sqrt(dx * dx + dy * dy) < threshold) {
                        hoveredId = n.getId();
                        break;
                    }
                }

                if (hoveredId != null) {
                    hoverTooltip.setText(hoveredId);
                    if (!hoverTooltip.isShowing()) {
                        hoverTooltip.show(view,
                                event.getScreenX() + 12,
                                event.getScreenY() + 12);
                    } else {
                        hoverTooltip.setX(event.getScreenX() + 12);
                        hoverTooltip.setY(event.getScreenY() + 12);
                    }
                } else {
                    hoverTooltip.hide();
                }
            });

            view.setOnMouseExited(event -> hoverTooltip.hide());
        });

        return view;
    }

    private double toDouble(Object o) {
        return o instanceof Double ? (Double) o : Double.parseDouble(o.toString());
    }

    private void setupGraph() {
        graph = new MultiGraph("TreeGraph");
        sman = new SpriteManager(graph);
        graph.setAttribute("ui.stylesheet", styleSheet());
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
    }

    private void buildModelRecursive(Feature current) {
        if (current == null) return;
        addNode(current.getName());
        List<Feature> children = current.getChildren();
        if (children != null) {
            for (Feature child : children) {
                addNode(child.getName());
                String eId = edgeId(current.getName(), child.getName());
                graph.addEdge(eId, current.getName(), child.getName());
                buildModelRecursive(child);
            }
        }
    }

    private void decorateGroupsRecursive(Feature current) {
        if (current == null) return;
        List<Feature> children = current.getChildren();
        if (children != null && !children.isEmpty()) {
            if (current.isOrParent()) {
                addOr(current.getName(), children);
            } else if (current.isAlternativeParent()) {
                addXor(current.getName(), children);
            }
            for (Feature child : children) {
                if (child.isOptional()) {
                    addOptional(edgeId(current.getName(), child.getName()));
                } else if (child.isMandatory()) {
                    addMandatory(edgeId(current.getName(), child.getName()));
                }
                decorateGroupsRecursive(child);
            }
        }
    }

    private void addNode(String id) {
        if (graph.getNode(id) == null) {
            Node n = graph.addNode(id);
            String display = id.length() > MAX_LABEL_LEN
                    ? id.substring(0, MAX_LABEL_LEN) + "."
                    : id;
            n.setAttribute("ui.label", display);
        }
    }

    private void applyLayout() {
        TreeLayout layout = new TreeLayout(50.0, 200.0);
        layout.apply(graph, root);
    }

    private void addOptional(String edgeId) {
        Sprite s = circleSprite("opt_" + edgeId, "white");
        s.attachToEdge(edgeId);
        s.setPosition(0.95);
    }

    private void addMandatory(String edgeId) {
        Sprite s = circleSprite("man_" + edgeId, "black");
        s.attachToEdge(edgeId);
        s.setPosition(0.95);
    }

    private void addOr(String parentId, List<Feature> children) {
        double width = calculateGroupWidth(children);
        triangleSprite("or_" + parentId, "black", width).attachToNode(parentId);
    }

    private void addXor(String parentId, List<Feature> children) {
        double width = calculateGroupWidth(children);
        triangleSprite("xor_" + parentId, "white", width).attachToNode(parentId);
    }

    private double calculateGroupWidth(List<Feature> children) {
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        boolean found = false;
        for (Feature child : children) {
            Node n = graph.getNode(child.getName());
            if (n != null && n.hasAttribute("xyz")) {
                double x = ((Object[]) n.getAttribute("xyz"))[0] instanceof Double ?
                        (Double) ((Object[]) n.getAttribute("xyz"))[0] :
                        Double.parseDouble(((Object[]) n.getAttribute("xyz"))[0].toString());
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
                found = true;
            }
        }
        if (!found) return 60.0;
        return Math.max((maxX - minX) * 0.20, 25.0);
    }

    private Sprite circleSprite(String id, String fill) {
        Sprite s = sman.addSprite(id);
        s.setAttribute("ui.style", "shape: circle; size: 14px; fill-color: " + fill +
                "; stroke-mode: plain; stroke-color: black; stroke-width: 1.5px; z-index: 4;");
        return s;
    }

    private Sprite triangleSprite(String id, String fill, double width) {
        Sprite s = sman.addSprite(id);
        s.setAttribute("ui.style", "shape: triangle; fill-color: " + fill +
                "; stroke-mode: plain; stroke-color: black; stroke-width: 2px; z-index: 2; " +
                "size: " + width + "px, 35px;");
        s.setPosition(0, -22, 0);
        return s;
    }

    private String edgeId(String p, String c) { return p + "_" + c; }

    private String styleSheet() {
        return """
        node {
            shape: box;
            size-mode: fit;
            padding: 4px, 6px;
            fill-color: #bc99fe;
            stroke-mode: plain;
            stroke-color: #333;
            stroke-width: 1px;
            text-size: 10px;
            text-alignment: center;
            text-offset: 2px, 3px;
            z-index: 3;
        }
        edge {
            shape: straight;
            size: 1.5px;
            fill-color: #555;
            z-index: 1;
        }
        """;
    }

    @Override public void viewClosed(String s) {}

    @Override
    public void buttonPushed(String id) {
        Node n = graph.getNode(id);
        if (n == null) return;
        String current = (String) n.getAttribute("ui.label");
        if (current.equals(id)) {
            String truncated = id.length() > MAX_LABEL_LEN
                    ? id.substring(0, MAX_LABEL_LEN) + "."
                    : id;
            n.setAttribute("ui.label", truncated);
        } else {
            n.setAttribute("ui.label", id);
        }
    }

    @Override public void buttonReleased(String s) {}
    @Override public void mouseOver(String id) {}
    @Override public void mouseLeft(String id) {}
}