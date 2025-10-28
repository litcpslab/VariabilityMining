/***
 The MIT License (MIT)

 Copyright (c) 2025 Michael Schmidhammer
 */

package guiModel;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class Group extends Difference {
	
	private int id;
    private List<String> occurrences;
    private List<Element> elements;

    public Group(int id, String name) {
    	this.id = id;
        this.name = new SimpleStringProperty(name);
        this.occurrences = new ArrayList<>();
        this.elements = new ArrayList<>();
    }
    
    public Group() {
    	
    }

    public int getId() {
		return id;
	}
    
    public List<String> getOccurrences() {
        return occurrences;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void addOccurrence(String occurrence) {
        this.occurrences.add(occurrence);
    }

    public void addElement(Element element) {
        this.elements.add(element);
    }

    public void setId(int id) {
		this.id = id;
	}

	public void setOccurrences(List<String> occurrences) {
		this.occurrences = occurrences;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	@Override
    public String toString() {
        return name.get();
    }
}
