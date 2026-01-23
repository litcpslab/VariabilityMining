package mappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.spi.MappingContext;

import guiModel.Element;
import guiModel.ExtractionType;
import guiModel.Group;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import varflixModel.IEC61499.IEC61499Variability;
import varflixModel.IEC61499.IEC61499Variant;
import varflixModel.IEC61499.JSON1499VariabilityGroup;

/*
Copyright (c) 2025 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class DataMapper1499 {
	
	private ModelMapper mapper = new ModelMapper();
	
	private TypeMap<Group, JSON1499VariabilityGroup> typeMap = this.mapper.createTypeMap(Group.class, JSON1499VariabilityGroup.class);

	public List<Group> map1499VariabilityGroup(List<JSON1499VariabilityGroup> groupings){
		
		List<Group> groups = new ArrayList<>();
		
		configureMapper();
		
		Converter<Set<IEC61499Variant>, List<String>> occurrenceMapper = new Converter<Set<IEC61499Variant>, List<String>>() {
			
			@Override
			public List<String> convert(MappingContext<Set<IEC61499Variant>, List<String>> context) {
				List<String> destination = new ArrayList<>();
				
				for(IEC61499Variant variant: context.getSource()) {
					destination.add(variant.getName());
				}
				
				return destination;
			}
		};
		
		Converter<IEC61499Variability, Element> elementConverter = new Converter<IEC61499Variability, Element>() {

			@Override
			public Element convert(MappingContext<IEC61499Variability, Element> context) {
				IEC61499Variability source = context.getSource();
				String line = source.toString();
				String name = line;
		        String location = line;
		        if (line.contains("->")) {
		            String[] connection = line.split("->");
		            int indexOld = connection[0].lastIndexOf(';');
		            int indexNew = connection[1].lastIndexOf(';');
		            if (indexOld != -1 && indexNew != -1) {
		                location = connection[0].substring(0, indexOld);
		                name = connection[0].substring(indexOld+1) + " -> " + connection[1].substring(indexNew+1);
		            } else if(indexOld == -1 && indexNew != -1) {
		            	location = connection[0].substring(0, connection[0].lastIndexOf('.'));
		            	name = connection[0] + " -> " + connection[1].substring(indexNew+1);
		            } else if(indexOld != -1 && indexNew == -1) {
		            	location = connection[0].substring(0, indexOld);
		            	name = connection[0].substring(indexOld+1) + " -> " + connection[1];
		            }
		        } else {
		            int index = line.lastIndexOf(';');
		            if (index != -1) {
		                location = line.substring(0, index);
		                name = line.substring(index+1);
		            }
		        }
				return new Element(source.getId(), name, ExtractionType.IEC61499, location, source.toString());
			}
		};
		
		Converter<String, StringProperty> nameConverter = new Converter<String, StringProperty>() {
			
			@Override
			public StringProperty convert(MappingContext<String, StringProperty> context) {
				return context.getSource() == null ? null : new SimpleStringProperty(context.getSource());
			}
		};
		
		
		mapper.addConverter(occurrenceMapper);
		mapper.addConverter(elementConverter);
		mapper.addConverter(nameConverter);
		
		TypeMap<JSON1499VariabilityGroup, Group> typeMap = this.mapper.createTypeMap(JSON1499VariabilityGroup.class, Group.class);
		
		typeMap.addMappings(mapping -> mapping.using(occurrenceMapper).map(JSON1499VariabilityGroup::getOccurrences, Group::setOccurrences));
		
		for(JSON1499VariabilityGroup group: groupings) {
			groups.add(mapper.map(group, Group.class));
		}
		
		return groups;
	}
	
	private void configureMapper() {
		mapper.getConfiguration().setFieldAccessLevel(AccessLevel.PRIVATE).setFieldMatchingEnabled(true);
	}
	
	public List<JSON1499VariabilityGroup> mapGUIGroupTo1499Group(List<Group> groups, List<IEC61499Variant> variants){
		List<JSON1499VariabilityGroup> mappedGroups = new ArrayList<>();
		
		configureMapper();
		
		Converter<Element, IEC61499Variability> variabilityConverter = new Converter<Element, IEC61499Variability>() {

			@Override
			public IEC61499Variability convert(MappingContext<Element, IEC61499Variability> context) {
				Element source = context.getSource();
				IEC61499Variability destination = new IEC61499Variability(source.getId());
				
				String name = source.getName().getValue();
				String description = source.getDescription();
				
				if(name.contains("->")) {
					destination.setEdge_source(description.substring(0, description.indexOf("->") - 1));
					destination.setEdge_target(description.substring((description.indexOf("->") + 3), description.length() - 1));
				} else {
					destination.setNode_id(description);
				}
				
				return destination;
			}
		};
		
		Converter<List<String>, Set<IEC61499Variant>> occurrenceMapper = new Converter<List<String>, Set<IEC61499Variant>>() {
			
			@Override
			public Set<IEC61499Variant> convert(MappingContext<List<String>, Set<IEC61499Variant>> context) {
				Set<IEC61499Variant> destination = new HashSet<>();
				
				for(String variant: context.getSource()) {
					IEC61499Variant dest = variants.stream().filter(v -> v.getName().equals(variant)).findFirst().orElseGet(() -> new IEC61499Variant(variant));
					destination.add(dest);
				}
				
				return destination;
			}
		};
		
		
		
		Converter<StringProperty, String> nameConverter = new Converter<StringProperty, String>() {
			
			@Override
			public String convert(MappingContext<StringProperty, String> context) {
				return context.getSource() == null ? null : new String(context.getSource().get());
			}
		};
		
		mapper.addConverter(variabilityConverter);
		mapper.addConverter(occurrenceMapper);
		mapper.addConverter(nameConverter);
		
		typeMap.addMappings(mapping -> mapping.using(occurrenceMapper).map(Group::getOccurrences, JSON1499VariabilityGroup::setOccurrences));
		
		typeMap.addMappings(mapping -> mapping.using(nameConverter).map(Group::getName, JSON1499VariabilityGroup::setAttributeName));
		
		for(Group group: groups) {
			mappedGroups.add(mapper.map(group, JSON1499VariabilityGroup.class));
		}
		
		return mappedGroups;
		
	}
}
