/***
 The MIT License (MIT)

 Copyright (c) 2025 Michael Schmidhammer
 */

package guiModel;

import javafx.beans.property.SimpleStringProperty;


public class Element extends Difference {

	private int id;
    private String location;
    private String description;
    private String separatorSymbol;
    private ExtractionType extractionType;

    public Element(int id, String name, ExtractionType type, String location, String description) {
    	this.id = id;
    	this.name = new SimpleStringProperty(name);
        this.extractionType = type;
        if (extractionType == ExtractionType.JAVA) {
            this.location = location.replaceAll("[()]", "");
            this.separatorSymbol = "/";
        } else {
            this.location = location;
            this.separatorSymbol = ";";
        }
        this.description = description;
    }
    
    public Element() {
    	
    }

    public int getId() {
		return id;
	}
    
    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getSeparateSymbol() {
        return separatorSymbol;
    }

    public ExtractionType getExtractionType() {
        return extractionType;
    }
    
    public void setId(int id) {
		this.id = id;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSeparatorSymbol(String separatorSymbol) {
		this.separatorSymbol = separatorSymbol;
	}

	public void setExtractionType(ExtractionType extractionType) {
		this.extractionType = extractionType;
	}

	@Override
    public String toString() {
        return location + ":\n" + description;
    }
}