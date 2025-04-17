package iec61499Mining;

import java.util.List;
import com.google.gson.annotations.SerializedName;

import variabilityMining.IVariability;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class IEC61499Variability implements IVariability {

	@SerializedName("node_id")
	private String node_id;
	
	@SerializedName("edge_source")
	private String edge_source;
	
	@SerializedName("edge_target")
	private String edge_target;
	
	@SerializedName("mapped_to")
	private List<MappedNode> mapped_to;
	
	public IEC61499Variability() {

	}
	
	@Override
	public String getLocation() {
		return node_id.substring(0, node_id.lastIndexOf(".") - 1);
	}
	
	public String getNode_id() {
		return node_id;
	}
	
	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}
	
	public List<MappedNode> getMapped_to() {
		return mapped_to;
	}
	
	public void setMapped_to(List<MappedNode> mapped_to) {
		this.mapped_to = mapped_to;
	}
	
	public String getEdge_source() {
		return edge_source;
	}
	
	public void setEdge_source(String edge_source) {
		this.edge_source = edge_source;
	}
	
	public String getEdge_target() {
		return edge_target;
	}
	
	public void setEdge_target(String edge_target) {
		this.edge_target = edge_target;
	}
	
	@Override
	public String toString() {
		if(node_id != null) {
			node_id = node_id.substring(node_id.indexOf('.') + 1).replace(".", ";");
			return node_id;
		} else {
			edge_source = edge_source.substring(edge_source.indexOf('.') + 1).replace(".", ";");
			edge_target = edge_target.substring(edge_target.indexOf('.') + 1).replace(".", ";");
			edge_source.lastIndexOf(";");
			
			return edge_source.replaceFirst(";(?=[^;]*$)", ".") + " -> " + edge_target.replaceFirst(";(?=[^;]*$)", ".");
			
		}
	}
}
