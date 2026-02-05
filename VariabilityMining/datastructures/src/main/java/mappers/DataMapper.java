package mappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.spi.MappingContext;

import guiModel.Element;
import guiModel.ExtractionType;
import guiModel.Group;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import varflixModel.IVariability;
import varflixModel.IVariabilityGroup;
import varflixModel.IVariant;
import varflixModel.IEC61499.JSON1499VariabilityGroup;

/*
Copyright (c) 2026 Johannes Kepler University Linz
LIT Cyber-Physical Systems Lab
*Contributors:
Alexander Stummer - Initial Implementation
*/
public class DataMapper {
	private ModelMapper mapper = new ModelMapper();

	private TypeMap<Group, IVariabilityGroup> destinationMap = this.mapper.createTypeMap(Group.class, IVariabilityGroup.class);//token.getRawType());
	
	public <V extends IVariant, E extends IVariability> List<Group> mapVariabilityGroup(List<IVariabilityGroup<V, E>> groupings){
		
		List<Group> groups = new ArrayList<>();
		
		configureMapper();
		
		Converter<Set<V>, List<String>> occurrenceMapper = new Converter<Set<V>, List<String>>() {
			
			@Override
			public List<String> convert(MappingContext<Set<V>, List<String>> context) {
				List<String> destination = new ArrayList<>();
				
				for(V variant: context.getSource()) {
					destination.add(variant.getName());
				}
				
				return destination;
			}
		};
		
		Converter<List<E>, List<Element>> elementConverter = new Converter<List<E>, List<Element>>() {

			@Override
			public List<Element> convert(MappingContext<List<E>, List<Element>> context) {
				List<Element> elements = new ArrayList<>();
				List<E> sourceList = context.getSource();
				
				for(E source: sourceList) {
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
			        //TODO Further generify this by setting Extraction type based on E
					elements.add(new Element(source.getId(), name, ExtractionType.IEC61499, location, source.toString()));
				}
				
				return elements;
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
		
		TypeToken<IVariabilityGroup<V, E>> token = TypeToken.of(new TypeToken<IVariabilityGroup<V, E>>() {}.getType());
		
		TypeMap<IVariabilityGroup<V, E>, Group> sourceMap = this.mapper.createTypeMap(token.getRawType(), Group.class);
		
		sourceMap.addMappings(mapping -> mapping.using(occurrenceMapper).map(IVariabilityGroup::getOccurrences, Group::setOccurrences));	
		
		for(IVariabilityGroup<V, E> group: groupings) {
			groups.add(mapper.map(group, Group.class));
		}
		
		return groups;
	}
	
	private void configureMapper() {
		mapper.getConfiguration().setFieldAccessLevel(AccessLevel.PRIVATE).setFieldMatchingEnabled(true);
	}
	
	public <V extends IVariant, E extends IVariability> List<IVariabilityGroup<V, E>> mapGUIGroupTo1499VariabilityGroup(List<Group> groups, List<V> variants, List<E> elements){
		List<IVariabilityGroup<V, E>> mappedGroups = new ArrayList<>();
		
		configureMapper();
		
		Converter<List<Element>, List<E>> variabilityConverter = new Converter<List<Element>, List<E>>() {

			@Override
			public List<E> convert(MappingContext<List<Element>, List<E>> context) {
				List<Element> source = context.getSource();
				
				List<E> mappedElements = new ArrayList<>();
				
				for(Element element: source) {
					E destination = elements.stream().filter(v -> v.getId() == element.getId()).findFirst().get();
					mappedElements.add(destination);
				}
				
				return mappedElements;
			}
		};
		
		Converter<List<String>, Set<V>> occurrenceMapper = new Converter<List<String>, Set<V>>() {
			
			@Override
			public Set<V> convert(MappingContext<List<String>, Set<V>> context) {
				Set<V> destination = new HashSet<>();
				
				for(String variant: context.getSource()) {
					V dest = variants.stream().filter(v -> v.getName().equals(variant)).findFirst().get();
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
		
		//TODO Create a factory providing the correct group depending on the artifact type
		destinationMap.setProvider(request -> new JSON1499VariabilityGroup());
		
		destinationMap.addMappings(mapping -> mapping.using(occurrenceMapper).map(Group::getOccurrences, IVariabilityGroup<V, E>::setOccurrences));
		
		destinationMap.addMappings(mapping -> mapping.using(nameConverter).map(Group::getName, IVariabilityGroup<V, E>::setAttributeName));
		
		TypeToken<IVariabilityGroup<V, E>> token = TypeToken.of(new TypeToken<IVariabilityGroup<V, E>>() {}.getType());
		
		for(Group group: groups) {
			mappedGroups.add(mapper.map(group, token.getType()));
		}
		
		return mappedGroups;
	}
}
