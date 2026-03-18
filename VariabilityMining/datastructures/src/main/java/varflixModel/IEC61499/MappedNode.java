/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2025 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * Contributors:
 *  Alexander Stummer - Initial Implementation
********************************************************************************/

package varflixModel.IEC61499;

import com.google.gson.annotations.SerializedName;

public class MappedNode {

	@SerializedName("node_id")
    private String node_id;

	@SerializedName("edge_source")
    private String edge_source;

	@SerializedName("edge_target")
    private String edge_target;
    
    public String getEdge_source() {
		return edge_source;
	}
    
    public String getEdge_target() {
		return edge_target;
	}
    
    public String getNode_id() {
		return node_id;
	}
    
    public void setEdge_source(String edge_source) {
		this.edge_source = edge_source;
	}
    
    public void setEdge_target(String edge_target) {
		this.edge_target = edge_target;
	}
    
    public void setNode_id(String node_id) {
		this.node_id = node_id;
	}
}
