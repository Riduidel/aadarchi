package org.ndx.aadarchi.base;

import java.util.Comparator;

import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.structurizr.model.Element;

public class OrderedModelElement implements Comparator<Element> {

	@Override
	public int compare(Element o1, Element o2) {
		if(o1.getProperties().containsKey(ModelElementKeys.ORDERING)) {
			if(o2.getProperties().containsKey(ModelElementKeys.ORDERING)) {
				long n1 = Long.parseLong(o1.getProperties().get(ModelElementKeys.ORDERING));
				long n2 = Long.parseLong(o1.getProperties().get(ModelElementKeys.ORDERING));
				return (int) (n1-n2);
			}
		}
		return o1.getCanonicalName().compareTo(o2.getCanonicalName());
	}

}
