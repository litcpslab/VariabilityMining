package iec61499Mining;

import java.lang.reflect.Type;

import com.google.gson.InstanceCreator;

import varflixModel.IEC61499.IEC61499Variability;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class IEC61499VariabilityInstanceCreator implements InstanceCreator<IEC61499Variability>{

	private int elementId = -1;

	@Override
	public IEC61499Variability createInstance(Type type) {
		elementId++;
		return new IEC61499Variability(elementId);
	}
	
	
}
