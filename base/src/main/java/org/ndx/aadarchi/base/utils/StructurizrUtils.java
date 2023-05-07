package org.ndx.aadarchi.base.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.structurizr.PropertyHolder;
import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.SoftwareSystem;

/**
 * Some utility classes about structurizr
 * @author nicolas-delsaux
 *
 */
public class StructurizrUtils {
	public static String getCanonicalPath(Element element) {
		String returned = "";
		if(element.getParent()!=null) {
			returned = getCanonicalPath(element.getParent());
		}
		returned += "/" + element.getName();
		return returned;
	}
	
	public static List<PropertyHolder> getHierarchy(List<? extends PropertyHolder> elements) {
		return elements.stream()
				.map(StructurizrUtils::getHierarchy)
				.flatMap(List::stream)
				.distinct()
				.collect(Collectors.toList());
	}
	
	public static List<PropertyHolder> getHierarchy(PropertyHolder element) {
		List<PropertyHolder> returned = new ArrayList<>();
		if (element instanceof Component) {
			Component component = (Component) element;
			returned.addAll(getHierarchy(component.getContainer()));
			returned.add(component);
		} else if (element instanceof Container) {
			Container container = (Container) element;
			returned.addAll(getHierarchy(container.getSoftwareSystem()));
			returned.add(container);
		} else if (element instanceof SoftwareSystem) {
			SoftwareSystem system = (SoftwareSystem) element;
			returned.add(system);
		} else if (element instanceof Workspace) {
			Workspace workspace = (Workspace) element;
			returned.add(workspace);
		}
		return returned;
	}
}
