/***
 
    This Source Code Form is subject to the terms of the Mozilla
    Public License, v. 2.0. If a copy of the MPL was not distributed
    with this file, You can obtain one at
    https://mozilla.org/MPL/2.0/.*
    Contributors:
    Michael Schmidhammer
**/

package at.variabilityanalysisgui.controller;

import at.variabilityanalysisgui.controller.Filter.Filter;
import at.variabilityanalysisgui.controller.Filter.MultipleChoiceFilter;
import at.variabilityanalysisgui.controller.Filter.SearchFilter;
import at.variabilityanalysisgui.view.FilterItem;
import guiModel.Element;
import guiModel.ExtractionType;
import guiModel.Group;
import javafx.collections.SetChangeListener;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static at.variabilityanalysisgui.controller.Controller.findGroupContainingElement;

public class FilterController {

    private final TextField searchTextField;
    private final Button filterButton;

    private ContextMenu filterContextMenu;
    private final List<Filter> filters = new ArrayList<>();
    private final Controller mainController;
    private final FeatureViewController featureViewController;

    public FilterController(Controller controller, FeatureViewController featureViewController, TextField searchTextField, Button filterButton) {
        this.mainController = controller;
        this.featureViewController = featureViewController;
        this.searchTextField = searchTextField;
        this.filterButton = filterButton;
        //Filter and Search setup
        setupFilterListener();
    }

    public void setupFilterListener() {
        filters.clear();
        searchTextField.clear();

        // setup filter listener and ui elements
        if(mainController.getArtifactType() == ExtractionType.IEC61499) {
            this.filters.add(createOccurrenceFilter());
            this.filters.add(createConnectionBlockFilter());
            this.filterButton.setDisable(false);
            this.searchTextField.setDisable(false);

        } else if (mainController.getArtifactType() == ExtractionType.JAVA) {
            this.filters.add(createOccurrenceFilter());
            this.filterButton.setDisable(false);
            this.searchTextField.setDisable(false);

        } else {
            this.filterButton.setDisable(true);
            this.searchTextField.setDisable(true);
        }

        setupSearchFilter();
        setupContextMenu();
    }

    private MultipleChoiceFilter createConnectionBlockFilter() {
        return new MultipleChoiceFilter("Element Type", Arrays.asList("Connection", "Function Block"), (selected, element) -> {
            if (selected == null || selected.isEmpty()) return element;
            else if (selected.contains("Connection") && element.getName().get().contains(" -> ")) return element;
            else if (selected.contains("Function Block") && !element.getName().get().contains(" -> ")) return element;
            return null;
        });
    }

    private MultipleChoiceFilter createOccurrenceFilter() {
        List<String> allOccurrences = mainController.getOriginalGroups().stream().map(Group::getOccurrences).flatMap(Set::stream).sorted().distinct().toList();
        return new MultipleChoiceFilter("Occurrences", allOccurrences, (selected, element) -> {
            if (selected == null || selected.isEmpty()) return element;
            Group group = findGroupContainingElement(mainController.getOriginalGroups(), element);
            if (group != null && group.getOccurrences().stream().anyMatch(selected::contains)) return element;
            return null;
        });
    }

    private void setupSearchFilter() {
        Filter searchFilter = new SearchFilter("Search");
        searchTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchFilter.setValue(newVal);
            searchFilter.setEnabled(newVal != null && !newVal.trim().isEmpty());
            if (newVal != null && newVal.isEmpty()) {
                featureViewController.populateTreeView(getFilteredGroups(), getFilteredElements());
                return;
            }
            featureViewController.populateTreeView(getFilteredGroups(), getFilteredElements());
        });
        this.filters.add(searchFilter);
    }

    private void setupContextMenu() {
        filterContextMenu = new ContextMenu();
        filterContextMenu.setAutoHide(true);

        for(Filter filter : filters) {
            if (filter instanceof MultipleChoiceFilter multipleChoiceFilter) {
                multipleChoiceFilter.getSelectedValues().addListener((SetChangeListener<String>) change -> {
                    filter.setEnabled(!change.getSet().isEmpty());
                    featureViewController.populateTreeView(getFilteredGroups(), getFilteredElements());
                });
            } else { // SearchFilter and SingleChoiceFilter
                filter.valueProperty().addListener((obs, oldVal, newVal) -> {
                    filter.setEnabled(newVal != null && !newVal.trim().isEmpty());
                    featureViewController.populateTreeView(getFilteredGroups(), getFilteredElements());
                });
            }

            if(!(filter instanceof SearchFilter)) {
                filter.enabledProperty().addListener((obs, oldVal, newVal) -> featureViewController.populateTreeView(getFilteredGroups(), getFilteredElements()));
                CustomMenuItem customMenuItem = new CustomMenuItem(new FilterItem(filter), false);
                filterContextMenu.getItems().add(customMenuItem);
            }
        }

        filterContextMenu.hide();
        filterButton.setOnAction(event -> {
            if (filterContextMenu.isShowing()) {
                filterContextMenu.hide();
            } else {
                filterContextMenu.show(filterButton, javafx.geometry.Side.BOTTOM, 0, 0);
            }
        });
    }

    public List<Group> getFilteredGroups() {
        List<Group> filteredGroups = mainController.getOriginalGroups();
        for(Filter filter : filters) {
            if (filter.isEnabled()) {
                filteredGroups = filteredGroups.stream().filter(group -> filter.filter(group) != null).toList();
            }
        }
        return filteredGroups;
    }

    public List<Element> getFilteredElements() {
        List<Element> elements = mainController.getOriginalGroups().stream().flatMap(group -> group.getElements().stream()).toList();
        for (Filter filter : filters) {
            if (filter.isEnabled()) {
                elements = elements.stream()
                        .map(filter::filter)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        }
        return elements;
    }
}
