package org.ndx.agile.architecture.inferer.spring;

import java.util.Set;

import com.structurizr.analysis.AbstractSpringComponentFinderStrategy;
import com.structurizr.analysis.SupportingTypesStrategy;
import com.structurizr.model.Component;

public final class SpringRepositoryComponentFinderStrategy extends AbstractSpringComponentFinderStrategy {

    public SpringRepositoryComponentFinderStrategy(SupportingTypesStrategy... strategies) {
        super(strategies);
    }

    @Override
    protected Set<Component> doFindComponents() {
        return findClassesWithAnnotation(
                org.springframework.stereotype.Repository.class,
                SPRING_REPOSITORY,
                includePublicTypesOnly
        );
    }

}